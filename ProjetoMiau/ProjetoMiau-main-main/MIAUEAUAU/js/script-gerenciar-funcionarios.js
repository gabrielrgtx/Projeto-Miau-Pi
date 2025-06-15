// js/script-gerenciar-funcionarios.js

// --- CONFIGURAÇÕES GERAIS ---
const API_FUNCIONARIOS_URL = 'http://localhost:8080/api/funcionarios';
const API_USERS_URL = 'http://localhost:8080/api/users';

// URLs de redirecionamento para acesso negado ou logout
const LOGIN_PAGE_URL = 'Login(1920).html';
const ADMIN_DASHBOARD_URL = 'admin-dashboard.html'; 

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
    document.getElementById('funcionario-id').value = '';
    document.getElementById('user-id').value = '';
    document.getElementById('nome').value = '';
    document.getElementById('sobrenome').value = '';
    document.getElementById('email').value = '';
    document.getElementById('cpf').value = '';
    document.getElementById('password').value = '';
    document.getElementById('cargo').value = '';
    document.getElementById('administrador').checked = false;
    document.getElementById('password').required = true;
    document.getElementById('password').placeholder = '';
    showFormMessage('', false);
}

// --- LÓGICA DE CARREGAMENTO E EXIBIÇÃO ---
async function loadFuncionarios() {
    const tbody = document.querySelector('#funcionarios-table tbody');
    tbody.innerHTML = '<tr><td colspan="7">Carregando funcionários...</td></tr>';
    showListMessage('', false);

    try {
        const response = await fetchAuthenticated(API_FUNCIONARIOS_URL);
        const funcionarios = await response.json();

        tbody.innerHTML = '';

        if (funcionarios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7">Nenhum funcionário cadastrado.</td></tr>';
            return;
        }

        funcionarios.forEach(func => {
            const row = tbody.insertRow();
            row.insertCell().textContent = func.id;
            row.insertCell().textContent = func.user.nome + ' ' + func.user.sobrenome;
            row.insertCell().textContent = func.user.email;
            row.insertCell().textContent = func.user.cpf;
            row.insertCell().textContent = func.cargo;
            row.insertCell().textContent = func.administrador ? 'Sim' : 'Não';

            const actionsCell = row.insertCell();
            const editButton = document.createElement('button');
            editButton.textContent = 'Editar';
            editButton.className = 'button edit';
            editButton.addEventListener('click', () => editFuncionario(func));
            actionsCell.appendChild(editButton);

            if (getUserRole() === 'ROLE_ADMIN' && func.user.email !== getUserEmail()) {
                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Excluir';
                deleteButton.className = 'button delete';
                deleteButton.addEventListener('click', () => deleteFuncionario(func.id));
                actionsCell.appendChild(deleteButton);
            }
        });
        showListMessage('Funcionários carregados com sucesso.', false);

    } catch (error) {
        showListMessage(`Erro ao carregar funcionários: ${error.message}`, true);
        console.error('Erro ao carregar funcionários:', error);
    }
}

// --- LÓGICA DE EDIÇÃO ---
function editFuncionario(func) {
    document.getElementById('funcionario-id').value = func.id;
    document.getElementById('user-id').value = func.user.id;
    document.getElementById('nome').value = func.user.nome;
    document.getElementById('sobrenome').value = func.user.sobrenome;
    document.getElementById('email').value = func.user.email;
    document.getElementById('cpf').value = func.user.cpf;
    document.getElementById('cargo').value = func.cargo;
    document.getElementById('administrador').checked = func.administrador;
    document.getElementById('password').value = '';
    document.getElementById('password').required = false;
    document.getElementById('password').placeholder = 'Deixe em branco para não alterar';
    showFormMessage('Editando funcionário...', false);
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

document.getElementById('cancelar-edicao').addEventListener('click', () => {
    clearForm();
    showFormMessage('Formulário limpo.', false);
});

// --- LÓGICA DE SUBMISSÃO DO FORMULÁRIO (ADD/EDIT) ---
document.getElementById('funcionario-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const funcionarioId = document.getElementById('funcionario-id').value;
    const userId = document.getElementById('user-id').value;
    const nome = document.getElementById('nome').value.trim();
    const sobrenome = document.getElementById('sobrenome').value.trim();
    const email = document.getElementById('email').value.trim();
    const cpf = document.getElementById('cpf').value.trim().replace(/\D/g, '');
    const password = document.getElementById('password').value;
    const cargo = document.getElementById('cargo').value.trim();
    const administrador = document.getElementById('administrador').checked;

    if (!nome || !sobrenome || !email || !cpf || !cargo) {
        showFormMessage('Por favor, preencha todos os campos obrigatórios (exceto senha na edição).', true);
        return;
    }
    if (!funcionarioId && password.length < 6) { 
        showFormMessage('A senha é obrigatória e deve ter no mínimo 6 caracteres para novos funcionários.', true);
        return;
    }

    const userData = {
        id: userId === '' ? null : Number(userId),
        nome: nome,
        sobrenome: sobrenome,
        email: email,
        cpf: cpf,
        password: password,
        role: administrador ? 'ROLE_ADMIN' : 'ROLE_FUNCIONARIO'
    };

    const funcionarioData = {
        id: funcionarioId === '' ? null : Number(funcionarioId),
        cargo: cargo,
        administrador: administrador,
        user: userData
    };

    let url = API_FUNCIONARIOS_URL;
    let method = 'POST';

    if (funcionarioId) {
        url = `${API_FUNCIONARIOS_URL}/${funcionarioId}`;
        method = 'PUT';
        if (password === '') {
            delete funcionarioData.user.password;
        }
    } else {
        if (password === '') {
             showFormMessage('A senha é obrigatória para novos funcionários.', true);
             return;
        }
    }
    
    try {
        const response = await fetchAuthenticated(url, {
            method: method,
            body: JSON.stringify(funcionarioData)
        });

        const data = await response.json();
        if (response.ok) {
            showFormMessage(data.message || `Funcionário ${funcionarioId ? 'atualizado' : 'adicionado'} com sucesso!`, false);
            clearForm();
            loadFuncionarios();
        } else {
            const errorMessage = data.message || 'Erro ao salvar funcionário.';
            showFormMessage(errorMessage, true);
        }

    } catch (error) {
        showFormMessage(`Erro na requisição: ${error.message}`, true);
        console.error('Erro ao salvar funcionário:', error);
    }
});


// --- LÓGICA DE EXCLUSÃO ---
async function deleteFuncionario(id) {
    if (!confirm('Tem certeza que deseja excluir este funcionário?')) {
        return;
    }
    showListMessage(`Excluindo funcionário ${id}...`, false);

    try {
        const response = await fetchAuthenticated(`${API_FUNCIONARIOS_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.status === 204) {
            showListMessage('Funcionário excluído com sucesso!', false);
            loadFuncionarios();
        } else {
            const errorData = await response.json(); // Erro se não for 204, tentar ler JSON
            const errorMessage = errorData.message || 'Erro ao excluir funcionário.';
            showListMessage(errorMessage, true);
        }
    } catch (error) {
        showListMessage(`Erro na requisição de exclusão: ${error.message}`, true);
        console.error('Erro ao excluir funcionário:', error);
    }
}


// --- LÓGICA DE INICIALIZAÇÃO DA PÁGINA ---
document.addEventListener('DOMContentLoaded', () => {
    // A lógica de sessão e logout é tratada em script-dashboard-universal.js

    const userRole = getUserRole();

    if (userRole !== 'ROLE_ADMIN') {
        alert('Acesso negado. Você não tem permissão para gerenciar funcionários.');
        // Redireciona para o painel apropriado ou para o login
        // A URL deve ser a URL da dashboard de um funcionário comum ou a página de login
        window.location.href = 'Login(1920).html'; // Redirecionando para o login como padrão para não-admins
        return;
    }

    // Lógica para o botão Voltar ao Painel Inicial
    const voltarPainelButton = document.getElementById('voltar-painel-button-veterinarios'); // ID no HTML
    if (voltarPainelButton) {
        voltarPainelButton.addEventListener('click', () => {
            window.location.href = ADMIN_DASHBOARD_URL; // Redireciona para o painel do administrador
        });
    } else {
        console.error("DEBUG: Botão 'Voltar ao Painel Inicial' não encontrado no HTML!");
    }

    loadFuncionarios();
});