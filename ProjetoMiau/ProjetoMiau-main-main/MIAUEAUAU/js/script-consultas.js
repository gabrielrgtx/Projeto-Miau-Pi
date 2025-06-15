// js/script-gerenciar-consultas.js

// --- CONFIGURAÇÕES GERAIS ---
const API_CONSULTAS_URL = 'http://localhost:8080/api/consultas';
const API_PACIENTES_URL = 'http://localhost:8080/api/pacientes'; // Para validar ID do paciente
const API_VETERINARIOS_URL = 'http://localhost:8080/api/veterinarios'; // Para validar ID do veterinário

// URLs de redirecionamento
const LOGIN_PAGE_URL = 'Login(1920).html';
const ADMIN_DASHBOARD_URL = 'admin-dashboard.html';
const FUNCIONARIO_DASHBOARD_URL = 'funcionario-dashboard.html';
const VETERINARIO_DASHBOARD_URL = 'veterinario-dashboard.html';

// --- FUNÇÕES AUXILIARES DE AUTENTICAÇÃO E REQUISIÇÃO (do script-dashboard-universal.js) ---
function getUserEmail() { return localStorage.getItem('userEmail'); }
function getUserPassword() { return localStorage.getItem('userPassword'); }
function getUserRole() { return localStorage.getItem('userRole'); }

async function fetchAuthenticated(url, options = {}) {
    const userEmail = getUserEmail();
    const userPassword = getUserPassword();

    if (!userEmail || !userPassword) {
        alert('Sua sessão expirou ou credenciais incompletas. Por favor, faça login novamente.');
        window.location.href = LOGIN_PAGE_URL;
        throw new Error('Credenciais de autenticação ausentes.');
    }

    const authHeader = 'Basic ' + btoa(`${userEmail}:${userPassword}`);
    
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': authHeader,
        ...options.headers
    };

    const config = {
        ...options,
        headers: headers
    };

    const response = await fetch(url, config);

    if (response.status === 401 || response.status === 403) {
        alert('Acesso negado. Você não tem permissão para esta ação. Por favor, faça login novamente.');
        window.location.href = LOGIN_PAGE_URL;
        throw new Error('Acesso não autorizado.');
    }

    return response;
}

// --- FUNÇÕES DE FEEDBACK ---
function showFormMessage(message, isError = false) {
    const messageElement = document.getElementById('form-message');
    messageElement.textContent = message;
    messageElement.className = isError ? 'error' : 'success';
    messageElement.style.display = 'block';
}

function showListMessage(message, isError = false) {
    const messageElement = document.getElementById('list-message');
    messageElement.textContent = message;
    messageElement.className = isError ? 'error' : 'success';
    messageElement.style.display = 'block';
}

function clearForm() {
    document.getElementById('consulta-id').value = '';
    document.getElementById('paciente-id').value = '';
    document.getElementById('veterinario-id').value = '';
    document.getElementById('data-hora').value = '';
    document.getElementById('motivo').value = '';
    document.getElementById('tipo-atendimento').value = '';
    document.getElementById('confirmada').checked = false;
    showFormMessage('', false);
}

// --- LÓGICA DE CARREGAMENTO E EXIBIÇÃO ---
async function loadConsultas() {
    const tbody = document.querySelector('#consultas-table tbody');
    tbody.innerHTML = '<tr><td colspan="8">Carregando consultas...</td></tr>';
    showListMessage('', false);

    try {
        const response = await fetchAuthenticated(API_CONSULTAS_URL);
        const consultas = await response.json();

        tbody.innerHTML = '';

        if (consultas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8">Nenhuma consulta cadastrada.</td></tr>';
            return;
        }

        consultas.forEach(consulta => {
            const row = tbody.insertRow();
            row.insertCell().textContent = consulta.id;
            row.insertCell().textContent = consulta.paciente ? consulta.paciente.nome : 'N/A';
            row.insertCell().textContent = consulta.veterinario ? consulta.veterinario.user.nome + ' ' + consulta.veterinario.user.sobrenome : 'N/A';
            row.insertCell().textContent = consulta.dataHora ? new Date(consulta.dataHora).toLocaleString() : 'N/A';
            row.insertCell().textContent = consulta.motivo || 'N/A';
            row.insertCell().textContent = consulta.tipoAtendimento || 'N/A';
            row.insertCell().textContent = consulta.confirmada ? 'Sim' : 'Não';

            const actionsCell = row.insertCell();
            const editButton = document.createElement('button');
            editButton.textContent = 'Editar';
            editButton.className = 'button edit';
            editButton.addEventListener('click', () => editConsulta(consulta));
            actionsCell.appendChild(editButton);

            const userRole = getUserRole();
            // Botão Confirmar (se não estiver confirmada e usuário tiver permissão)
            if (!consulta.confirmada && (userRole === 'ROLE_FUNCIONARIO' || userRole === 'ROLE_VETERINARIO' || userRole === 'ROLE_ADMIN')) {
                const confirmButton = document.createElement('button');
                confirmButton.textContent = 'Confirmar';
                confirmButton.className = 'button confirm';
                confirmButton.addEventListener('click', () => confirmConsulta(consulta.id));
                actionsCell.appendChild(confirmButton);
            }

            // Apenas ROLE_ADMIN pode deletar consultas
            if (userRole === 'ROLE_ADMIN') {
                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Excluir';
                deleteButton.className = 'button delete';
                deleteButton.addEventListener('click', () => deleteConsulta(consulta.id));
                actionsCell.appendChild(deleteButton);
            }
        });
        showListMessage('Consultas carregadas com sucesso.', false);

    } catch (error) {
        showListMessage(`Erro ao carregar consultas: ${error.message}`, true);
        console.error('Erro ao carregar consultas:', error);
    }
}

// --- LÓGICA DE EDIÇÃO ---
function editConsulta(consulta) {
    document.getElementById('consulta-id').value = consulta.id;
    document.getElementById('paciente-id').value = consulta.paciente ? consulta.paciente.id : '';
    document.getElementById('veterinario-id').value = consulta.veterinario ? consulta.veterinario.id : '';
    document.getElementById('data-hora').value = consulta.dataHora ? new Date(consulta.dataHora).toISOString().slice(0, 16) : '';
    document.getElementById('motivo').value = consulta.motivo || '';
    document.getElementById('tipo-atendimento').value = consulta.tipoAtendimento || '';
    document.getElementById('confirmada').checked = consulta.confirmada;
    showFormMessage('Editando consulta...', false);
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

document.getElementById('cancelar-edicao').addEventListener('click', () => {
    clearForm();
    showFormMessage('Formulário limpo.', false);
});

// --- LÓGICA DE SUBMISSÃO DO FORMULÁRIO (ADD/EDIT) ---
document.getElementById('consulta-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const consultaId = document.getElementById('consulta-id').value;
    const pacienteId = document.getElementById('paciente-id').value;
    const veterinarioId = document.getElementById('veterinario-id').value;
    const dataHora = document.getElementById('data-hora').value; // String YYYY-MM-DDTHH:MM
    const motivo = document.getElementById('motivo').value.trim();
    const tipoAtendimento = document.getElementById('tipo-atendimento').value;
    const confirmada = document.getElementById('confirmada').checked;

    // Validações básicas do front-end
    if (!pacienteId || !veterinarioId || !dataHora || !tipoAtendimento) {
        showFormMessage('Por favor, preencha os campos obrigatórios.', true);
        return;
    }
    if (isNaN(pacienteId) || parseInt(pacienteId) <= 0) {
        showFormMessage('ID do Paciente deve ser um número positivo.', true);
        return;
    }
    if (isNaN(veterinarioId) || parseInt(veterinarioId) <= 0) {
        showFormMessage('ID do Veterinário deve ser um número positivo.', true);
        return;
    }

    const consultaData = {
        id: consultaId === '' ? null : Number(consultaId),
        paciente: { id: Number(pacienteId) }, // Envia apenas o ID do paciente
        veterinario: { id: Number(veterinarioId) }, // Envia apenas o ID do veterinário
        dataHora: dataHora + ':00', // Adiciona segundos para formato ISO completo se necessário (backend precisa)
        motivo: motivo,
        tipoAtendimento: tipoAtendimento,
        confirmada: confirmada
    };

    let url = API_CONSULTAS_URL;
    let method = 'POST';

    if (consultaId) {
        url = `${API_CONSULTAS_URL}/${consultaId}`;
        method = 'PUT';
    }
    
    try {
        const response = await fetchAuthenticated(url, {
            method: method,
            body: JSON.stringify(consultaData)
        });

        const data = await response.json();
        if (response.ok) {
            showFormMessage(data.message || `Consulta ${consultaId ? 'atualizada' : 'agendada'} com sucesso!`, false);
            clearForm();
            loadConsultas();
        } else {
            const errorMessage = data.message || 'Erro ao salvar consulta.';
            showFormMessage(errorMessage, true);
        }

    } catch (error) {
        showFormMessage(`Erro na requisição: ${error.message}`, true);
        console.error('Erro ao salvar consulta:', error);
    }
});

// --- LÓGICA DE CONFIRMAÇÃO DE CONSULTA ---
async function confirmConsulta(id) {
    if (!confirm('Tem certeza que deseja CONFIRMAR esta consulta?')) {
        return;
    }
    showListMessage(`Confirmando consulta ${id}...`, false);

    try {
        const response = await fetchAuthenticated(`${API_CONSULTAS_URL}/${id}/confirmar`, {
            method: 'PUT'
        });

        if (response.ok) {
            showListMessage('Consulta confirmada com sucesso!', false);
            loadConsultas();
        } else {
            const errorData = await response.json();
            const errorMessage = errorData.message || 'Erro ao confirmar consulta.';
            showListMessage(errorMessage, true);
        }
    } catch (error) {
        showListMessage(`Erro na requisição de confirmação: ${error.message}`, true);
        console.error('Erro ao confirmar consulta:', error);
    }
}


// --- LÓGICA DE EXCLUSÃO ---
async function deleteConsulta(id) {
    if (!confirm('Tem certeza que deseja excluir esta consulta?')) {
        return;
    }
    showListMessage(`Excluindo consulta ${id}...`, false);

    try {
        const response = await fetchAuthenticated(`${API_CONSULTAS_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.status === 204) {
            showListMessage('Consulta excluída com sucesso!', false);
            loadConsultas();
        } else {
            const errorData = await response.json();
            const errorMessage = errorData.message || 'Erro ao excluir consulta.';
            showListMessage(errorMessage, true);
        }
    } catch (error) {
        showListMessage(`Erro na requisição de exclusão: ${error.message}`, true);
        console.error('Erro ao excluir consulta:', error);
    }
}


// --- LÓGICA DE INICIALIZAÇÃO DA PÁGINA ---
document.addEventListener('DOMContentLoaded', () => {
    const userRole = getUserRole();

    // Permissões: ADMIN, FUNCIONARIO, VETERINARIO podem gerenciar consultas
    if (userRole !== 'ROLE_ADMIN' && userRole !== 'ROLE_FUNCIONARIO' && userRole !== 'ROLE_VETERINARIO') {
        alert('Acesso negado. Você não tem permissão para gerenciar consultas.');
        window.location.href = 'Login(1920).html';
        return;
    }

    // Lógica para o botão Voltar ao Painel Inicial
    const voltarPainelButton = document.getElementById('voltar-painel-button-consultas');
    if (voltarPainelButton) {
        voltarPainelButton.addEventListener('click', () => {
            let redirectUrl;
            switch(userRole) {
                case 'ROLE_ADMIN':
                    redirectUrl = ADMIN_DASHBOARD_URL;
                    break;
                case 'ROLE_FUNCIONARIO':
                case 'ROLE_VETERINARIO':
                    redirectUrl = FUNCIONARIO_DASHBOARD_URL;
                    break;
                default:
                    redirectUrl = LOGIN_PAGE_URL;
            }
            window.location.href = redirectUrl;
        });
    } else {
        console.error("DEBUG: Botão 'Voltar ao Painel Inicial' não encontrado no HTML!");
    }

    loadConsultas();
});