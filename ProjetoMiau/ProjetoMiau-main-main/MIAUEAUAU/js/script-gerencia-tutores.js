// js/script-gerenciar-tutores.js

// --- CONFIGURAÇÕES GERAIS ---
const API_TUTORES_URL = 'http://localhost:8080/api/tutores'; // Endpoint de tutores
const API_USERS_URL = 'http://localhost:8080/api/users'; // Endpoint de usuários (para editar/deletar diretamente)

// URLs de redirecionamento para acesso negado ou logout
const LOGIN_PAGE_URL = 'Login(1920).html'; // Página de login
const ADMIN_DASHBOARD_URL = 'admin-dashboard.html'; // Painel do Admin
const FUNCIONARIO_DASHBOARD_URL = 'funcionario-dashboard.html'; // Painel do Funcionário

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
    document.getElementById('tutor-id').value = '';
    document.getElementById('user-id').value = '';
    document.getElementById('nome').value = '';
    document.getElementById('sobrenome').value = '';
    document.getElementById('email').value = '';
    document.getElementById('cpf').value = '';
    document.getElementById('password').value = '';
    document.getElementById('endereco').value = '';
    document.getElementById('telefone').value = '';
    document.getElementById('data-nascimento').value = '';
    document.getElementById('password').required = true;
    document.getElementById('password').placeholder = '';
    showFormMessage('', false);
}

// --- LÓGICA DE CARREGAMENTO E EXIBIÇÃO ---
async function loadTutores() {
    const tbody = document.querySelector('#tutores-table tbody');
    tbody.innerHTML = '<tr><td colspan="8">Carregando tutores...</td></tr>';
    showListMessage('', false);

    try {
        const response = await fetchAuthenticated(API_TUTORES_URL);
        const tutores = await response.json();

        tbody.innerHTML = '';

        if (tutores.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8">Nenhum tutor cadastrado.</td></tr>';
            return;
        }

        tutores.forEach(tutor => {
            const row = tbody.insertRow();
            row.insertCell().textContent = tutor.id;
            row.insertCell().textContent = tutor.user.nome + ' ' + tutor.user.sobrenome;
            row.insertCell().textContent = tutor.user.email;
            row.insertCell().textContent = tutor.user.cpf;
            row.insertCell().textContent = tutor.endereco;
            row.insertCell().textContent = tutor.telefone;
            row.insertCell().textContent = tutor.dataNascimento ? new Date(tutor.dataNascimento).toLocaleDateString() : 'N/A';

            const actionsCell = row.insertCell();
            const editButton = document.createElement('button');
            editButton.textContent = 'Editar';
            editButton.className = 'button edit';
            editButton.addEventListener('click', () => editTutor(tutor));
            actionsCell.appendChild(editButton);

            const userRole = getUserRole();
            // Apenas ROLE_ADMIN pode deletar outros tutores
            if (userRole === 'ROLE_ADMIN' && tutor.user.email !== getUserEmail()) {
                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Excluir';
                deleteButton.className = 'button delete';
                deleteButton.addEventListener('click', () => deleteTutor(tutor.id));
                actionsCell.appendChild(deleteButton);
            }
        });
        showListMessage('Tutores carregados com sucesso.', false);

    } catch (error) {
        showListMessage(`Erro ao carregar tutores: ${error.message}`, true);
        console.error('Erro ao carregar tutores:', error);
    }
}

// --- LÓGICA DE EDIÇÃO ---
function editTutor(tutor) {
    document.getElementById('tutor-id').value = tutor.id;
    document.getElementById('user-id').value = tutor.user.id;
    document.getElementById('nome').value = tutor.user.nome;
    document.getElementById('sobrenome').value = tutor.user.sobrenome;
    document.getElementById('email').value = tutor.user.email;
    document.getElementById('cpf').value = tutor.user.cpf;
    document.getElementById('endereco').value = tutor.endereco;
    document.getElementById('telefone').value = tutor.telefone;
    document.getElementById('data-nascimento').value = tutor.dataNascimento || '';
    document.getElementById('password').value = '';
    document.getElementById('password').required = false;
    document.getElementById('password').placeholder = 'Deixe em branco para não alterar';
    showFormMessage('Editando tutor...', false);
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

document.getElementById('cancelar-edicao').addEventListener('click', () => {
    clearForm();
    showFormMessage('Formulário limpo.', false);
});

// --- LÓGICA DE SUBMISSÃO DO FORMULÁRIO (ADD/EDIT) ---
document.getElementById('tutor-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const tutorId = document.getElementById('tutor-id').value;
    const userId = document.getElementById('user-id').value;
    const nome = document.getElementById('nome').value.trim();
    const sobrenome = document.getElementById('sobrenome').value.trim();
    const email = document.getElementById('email').value.trim();
    const cpf = document.getElementById('cpf').value.trim().replace(/\D/g, '');
    const password = document.getElementById('password').value;
    const endereco = document.getElementById('endereco').value.trim();
    const telefone = document.getElementById('telefone').value.trim();
    const dataNascimento = document.getElementById('data-nascimento').value;

    if (!nome || !sobrenome || !email || !cpf || !endereco || !telefone) {
        showFormMessage('Por favor, preencha todos os campos obrigatórios (exceto senha na edição).', true);
        return;
    }
    if (!tutorId && password.length < 6) { 
        showFormMessage('A senha é obrigatória e deve ter no mínimo 6 caracteres para novos tutores.', true);
        return;
    }

    const userData = {
        id: userId === '' ? null : Number(userId),
        nome: nome,
        sobrenome: sobrenome,
        email: email,
        cpf: cpf,
        password: password,
        role: 'ROLE_TUTOR' // Role fixa para tutores
    };

    const tutorData = {
        id: tutorId === '' ? null : Number(tutorId),
        endereco: endereco,
        telefone: telefone,
        dataNascimento: dataNascimento,
        user: userData
    };

    let url = API_TUTORES_URL;
    let method = 'POST';

    if (tutorId) {
        url = `${API_TUTORES_URL}/${tutorId}`;
        method = 'PUT';
        if (password === '') {
            delete tutorData.user.password;
        }
    } else {
        if (password === '') {
            showFormMessage('A senha é obrigatória para novos tutores.', true);
            return;
        }
    }
    
    try {
        const response = await fetchAuthenticated(url, {
            method: method,
            body: JSON.stringify(tutorData)
        });

        const data = await response.json();
        if (response.ok) {
            showFormMessage(data.message || `Tutor ${tutorId ? 'atualizado' : 'adicionado'} com sucesso!`, false);
            clearForm();
            loadTutores();
        } else {
            const errorMessage = data.message || 'Erro ao salvar tutor.';
            showFormMessage(errorMessage, true);
        }

    } catch (error) {
        showFormMessage(`Erro na requisição: ${error.message}`, true);
        console.error('Erro na requisição de tutores:', error);
    }
});


// --- LÓGICA DE EXCLUSÃO ---
async function deleteTutor(id) {
    if (!confirm('Tem certeza que deseja excluir este tutor?')) {
        return;
    }
    showListMessage(`Excluindo tutor ${id}...`, false);

    try {
        const response = await fetchAuthenticated(`${API_TUTORES_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.status === 204) {
            showListMessage('Tutor excluído com sucesso!', false);
            loadTutores();
        } else {
            const errorData = await response.json();
            const errorMessage = errorData.message || 'Erro ao excluir tutor.';
            showListMessage(errorMessage, true);
        }
    } catch (error) {
        showListMessage(`Erro na requisição de exclusão: ${error.message}`, true);
        console.error('Erro ao excluir tutor:', error);
    }
}


// --- LÓGICA DE INICIALIZAÇÃO DA PÁGINA ---
document.addEventListener('DOMContentLoaded', () => {
    const userRole = getUserRole();

    // Permissões: ADMIN e FUNCIONARIO/VETERINARIO podem gerenciar tutores
    if (userRole !== 'ROLE_ADMIN' && userRole !== 'ROLE_FUNCIONARIO' && userRole !== 'ROLE_VETERINARIO') {
        alert('Acesso negado. Você não tem permissão para gerenciar tutores.');
        window.location.href = 'Login(1920).html';
        return;
    }

    // Lógica para o botão Voltar ao Painel Inicial
    const voltarPainelButton = document.getElementById('voltar-painel-button-tutores'); // ID do botão no HTML
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

    loadTutores();
});