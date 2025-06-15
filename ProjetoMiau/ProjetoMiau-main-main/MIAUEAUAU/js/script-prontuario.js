// js/script-prontuarios.js

// --- CONFIGURAÇÕES GERAIS ---
const API_PRONTUARIOS_URL = 'http://localhost:8080/api/prontuarios';
const API_PACIENTES_URL = 'http://localhost:8080/api/pacientes'; // Para validar ID do paciente

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
    document.getElementById('prontuario-id').value = '';
    document.getElementById('paciente-id').value = '';
    document.getElementById('data-criacao').value = '';
    document.getElementById('observacoes-gerais').value = '';
    showFormMessage('', false);
}

// --- LÓGICA DE CARREGAMENTO E EXIBIÇÃO ---
async function loadProntuarios() {
    const tbody = document.querySelector('#prontuarios-table tbody');
    tbody.innerHTML = '<tr><td colspan="5">Carregando prontuários...</td></tr>';
    showListMessage('', false);

    try {
        const response = await fetchAuthenticated(API_PRONTUARIOS_URL);
        const prontuarios = await response.json();

        tbody.innerHTML = '';

        if (prontuarios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5">Nenhum prontuário cadastrado.</td></tr>';
            return;
        }

        prontuarios.forEach(prontuario => {
            const row = tbody.insertRow();
            row.insertCell().textContent = prontuario.id;
            row.insertCell().textContent = prontuario.paciente ? prontuario.paciente.nome : 'N/A';
            row.insertCell().textContent = prontuario.dataCriacao ? new Date(prontuario.dataCriacao).toLocaleString() : 'N/A';
            row.insertCell().textContent = prontuario.observacoesGerais || 'N/A';

            const actionsCell = row.insertCell();
            const editButton = document.createElement('button');
            editButton.textContent = 'Editar';
            editButton.className = 'button edit';
            editButton.addEventListener('click', () => editProntuario(prontuario));
            actionsCell.appendChild(editButton);

            const userRole = getUserRole();
            if (userRole === 'ROLE_ADMIN') {
                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Excluir';
                deleteButton.className = 'button delete';
                deleteButton.addEventListener('click', () => deleteProntuario(prontuario.id));
                actionsCell.appendChild(deleteButton);
            }
        });
        showListMessage('Prontuários carregados com sucesso.', false);

    } catch (error) {
        showListMessage(`Erro ao carregar prontuários: ${error.message}`, true);
        console.error('Erro ao carregar prontuários:', error);
    }
}

// --- LÓGICA DE EDIÇÃO ---
function editProntuario(prontuario) {
    document.getElementById('prontuario-id').value = prontuario.id;
    document.getElementById('paciente-id').value = prontuario.paciente ? prontuario.paciente.id : '';
    document.getElementById('data-criacao').value = prontuario.dataCriacao ? new Date(prontuario.dataCriacao).toISOString().slice(0, 16) : '';
    document.getElementById('observacoes-gerais').value = prontuario.observacoesGerais || '';

    showFormMessage('Editando prontuário...', false);
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

document.getElementById('cancelar-edicao').addEventListener('click', () => {
    clearForm();
    showFormMessage('Formulário limpo.', false);
});

// --- LÓGICA DE SUBMISSÃO DO FORMULÁRIO (ADD/EDIT) ---
document.getElementById('prontuario-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const prontuarioId = document.getElementById('prontuario-id').value;
    const pacienteId = document.getElementById('paciente-id').value;
    const dataCriacao = document.getElementById('data-criacao').value; // String YYYY-MM-DDTHH:MM
    const observacoesGerais = document.getElementById('observacoes-gerais').value.trim();

    // Validações básicas do front-end
    if (!pacienteId || !dataCriacao) {
        showFormMessage('Por favor, preencha os campos obrigatórios.', true);
        return;
    }
    if (isNaN(pacienteId) || parseInt(pacienteId) <= 0) {
        showFormMessage('ID do Paciente deve ser um número positivo.', true);
        return;
    }

    const prontuarioData = {
        id: prontuarioId === '' ? null : Number(prontuarioId),
        paciente: { id: Number(pacienteId) }, // Envia apenas o ID do paciente
        dataCriacao: dataCriacao + ':00', // Adiciona segundos para formato ISO completo
        observacoesGerais: observacoesGerais
    };

    let url = API_PRONTUARIOS_URL;
    let method = 'POST';

    if (prontuarioId) {
        url = `${API_PRONTUARIOS_URL}/${prontuarioId}`;
        method = 'PUT';
    }
    
    try {
        const response = await fetchAuthenticated(url, {
            method: method,
            body: JSON.stringify(prontuarioData)
        });

        const data = await response.json();
        if (response.ok) {
            showFormMessage(data.message || `Prontuário ${prontuarioId ? 'atualizado' : 'adicionado'} com sucesso!`, false);
            clearForm();
            loadProntuarios();
        } else {
            const errorMessage = data.message || 'Erro ao salvar prontuário.';
            showFormMessage(errorMessage, true);
        }

    } catch (error) {
        showFormMessage(`Erro na requisição: ${error.message}`, true);
        console.error('Erro ao salvar prontuário:', error);
    }
});


// --- LÓGICA DE EXCLUSÃO ---
async function deleteProntuario(id) {
    if (!confirm('Tem certeza que deseja excluir este prontuário?')) {
        return;
    }
    showListMessage(`Excluindo prontuário ${id}...`, false);

    try {
        const response = await fetchAuthenticated(`${API_PRONTUARIOS_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.status === 204) {
            showListMessage('Prontuário excluído com sucesso!', false);
            loadProntuarios();
        } else {
            const errorData = await response.json();
            const errorMessage = errorData.message || 'Erro ao excluir prontuário.';
            showListMessage(errorMessage, true);
        }
    } catch (error) {
        showListMessage(`Erro na requisição de exclusão: ${error.message}`, true);
        console.error('Erro ao excluir prontuário:', error);
    }
}


// --- LÓGICA DE INICIALIZAÇÃO DA PÁGINA ---
document.addEventListener('DOMContentLoaded', () => {
    const userRole = getUserRole();

    // Permissões: ADMIN, FUNCIONARIO, VETERINARIO podem gerenciar prontuários
    if (userRole !== 'ROLE_ADMIN' && userRole !== 'ROLE_FUNCIONARIO' && userRole !== 'ROLE_VETERINARIO') {
        alert('Acesso negado. Você não tem permissão para gerenciar prontuários.');
        window.location.href = 'Login(1920).html';
        return;
    }

    // Lógica para o botão Voltar ao Painel Inicial
    const voltarPainelButton = document.getElementById('voltar-painel-button-prontuarios');
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

    loadProntuarios();
});