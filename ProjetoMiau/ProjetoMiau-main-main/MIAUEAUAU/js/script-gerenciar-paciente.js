// js/script-gerenciar-pacientes.js

// --- CONFIGURAÇÕES GERAIS ---
const API_PACIENTES_URL = 'http://localhost:8080/api/pacientes';
const API_TUTORES_URL = 'http://localhost:8080/api/tutores';

// URLs de redirecionamento - DEFINIDAS AQUI PARA ESTE SCRIPT
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
    document.getElementById('paciente-id').value = '';
    document.getElementById('nome-pet').value = '';
    document.getElementById('especie').value = '';
    document.getElementById('raca').value = '';
    document.getElementById('data-nascimento').value = '';
    document.getElementById('peso').value = '';
    document.getElementById('tutor-id').value = '';
    showFormMessage('', false);
}

// --- LÓGICA DE CARREGAMENTO E EXIBIÇÃO ---
async function loadPacientes() {
    const tbody = document.querySelector('#pacientes-table tbody');
    tbody.innerHTML = '<tr><td colspan="9">Carregando pacientes...</td></tr>';
    showListMessage('', false);

    try {
        const response = await fetchAuthenticated(API_PACIENTES_URL);
        const pacientes = await response.json(); // Recebe a lista de PacienteResponseDTO

        tbody.innerHTML = '';

        if (pacientes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9">Nenhum paciente cadastrado.</td></tr>';
            return;
        }

        pacientes.forEach(pacienteDto => {
            const row = tbody.insertRow();
            row.insertCell().textContent = pacienteDto.id;
            row.insertCell().textContent = pacienteDto.nome;
            row.insertCell().textContent = pacienteDto.especie;
            row.insertCell().textContent = pacienteDto.raca;
            row.insertCell().textContent = pacienteDto.dataNascimento ? new Date(pacienteDto.dataNascimento).toLocaleDateString() : 'N/A';
            row.insertCell().textContent = pacienteDto.peso;
            row.insertCell().textContent = pacienteDto.tutorId || 'N/A';
            row.insertCell().textContent = pacienteDto.tutorNomeCompleto || 'N/A';

            const actionsCell = row.insertCell();
            const editButton = document.createElement('button');
            editButton.textContent = 'Editar';
            editButton.className = 'button edit';
            editButton.addEventListener('click', () => editPaciente(pacienteDto));
            actionsCell.appendChild(editButton);

            const userRole = getUserRole();
            if (userRole === 'ROLE_ADMIN') {
                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Excluir';
                deleteButton.className = 'button delete';
                deleteButton.addEventListener('click', () => deletePaciente(pacienteDto.id));
                actionsCell.appendChild(deleteButton);
            }
        });
        showListMessage('Pacientes carregados com sucesso.', false);

    } catch (error) {
        showListMessage(`Erro ao carregar pacientes: ${error.message}`, true);
        console.error('Erro ao carregar pacientes:', error);
    }
}

// --- LÓGICA DE EDIÇÃO ---
function editPaciente(pacienteDto) {
    document.getElementById('paciente-id').value = pacienteDto.id;
    document.getElementById('nome-pet').value = pacienteDto.nome;
    document.getElementById('especie').value = pacienteDto.especie;
    document.getElementById('raca').value = pacienteDto.raca;
    document.getElementById('data-nascimento').value = pacienteDto.dataNascimento || '';
    document.getElementById('peso').value = pacienteDto.peso;
    document.getElementById('tutor-id').value = pacienteDto.tutorId || '';

    showFormMessage('Editando paciente...', false);
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

document.getElementById('cancelar-edicao').addEventListener('click', () => {
    clearForm();
    showFormMessage('Formulário limpo.', false);
});

// --- LÓGICA DE SUBMISSÃO DO FORMULÁRIO (ADD/EDIT) ---
document.getElementById('paciente-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const pacienteId = document.getElementById('paciente-id').value;
    const nome = document.getElementById('nome-pet').value.trim();
    const especie = document.getElementById('especie').value.trim();
    const raca = document.getElementById('raca').value.trim();
    const dataNascimento = document.getElementById('data-nascimento').value;
    const peso = parseFloat(document.getElementById('peso').value);
    const tutorId = document.getElementById('tutor-id').value;

    if (!nome || !especie || !raca || isNaN(peso) || peso <= 0 || !tutorId || isNaN(tutorId) || parseInt(tutorId) <= 0) {
        showFormMessage('Por favor, preencha todos os campos obrigatórios corretamente.', true);
        return;
    }

    const pacienteData = {
        id: pacienteId === '' ? null : Number(pacienteId),
        nome: nome,
        especie: especie,
        raca: raca,
        dataNascimento: dataNascimento,
        peso: peso,
        tutor: { id: Number(tutorId) }
    };

    let url = API_PACIENTES_URL;
    let method = 'POST';

    if (pacienteId) {
        url = `${API_PACIENTES_URL}/${pacienteId}`;
        method = 'PUT';
    }
    
    try {
        const response = await fetchAuthenticated(url, {
            method: method,
            body: JSON.stringify(pacienteData)
        });

        const data = await response.json();
        if (response.ok) {
            showFormMessage(data.message || `Paciente ${pacienteId ? 'atualizado' : 'adicionado'} com sucesso!`, false);
            clearForm();
            loadPacientes();
        } else {
            const errorMessage = data.message || 'Erro ao salvar paciente.';
            showFormMessage(errorMessage, true);
        }

    } catch (error) {
        showFormMessage(`Erro na requisição: ${error.message}`, true);
        console.error('Erro ao salvar paciente:', error);
    }
});


// --- LÓGICA DE EXCLUSÃO ---
async function deletePaciente(id) {
    if (!confirm('Tem certeza que deseja excluir este paciente?')) {
        return;
    }
    showListMessage(`Excluindo paciente ${id}...`, false);

    try {
        const response = await fetchAuthenticated(`${API_PACIENTES_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.status === 204) {
            showListMessage('Paciente excluído com sucesso!', false);
            loadPacientes();
        } else {
            const errorData = await response.json();
            const errorMessage = errorData.message || 'Erro ao excluir paciente.';
            showListMessage(errorMessage, true);
        }
    } catch (error) {
        showListMessage(`Erro na requisição de exclusão: ${error.message}`, true);
        console.error('Erro ao excluir paciente:', error);
    }
}


// --- LÓGICA DE INICIALIZAÇÃO DA PÁGINA ---
document.addEventListener('DOMContentLoaded', () => {
    const userRole = getUserRole();

    // Permissões: ADMIN, FUNCIONARIO, VETERINARIO podem gerenciar pacientes
    if (userRole !== 'ROLE_ADMIN' && userRole !== 'ROLE_FUNCIONARIO' && userRole !== 'ROLE_VETERINARIO') {
        alert('Acesso negado. Você não tem permissão para gerenciar pacientes.');
        window.location.href = LOGIN_PAGE_URL;
        return;
    }

    // Lógica para o botão Voltar ao Painel Inicial
    const voltarPainelButton = document.getElementById('voltar-painel-button-pacientes');
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

    loadPacientes();
});