// js/script-procedimentos.js

// --- CONFIGURAÇÕES GERAIS ---
const API_PROCEDIMENTOS_URL = 'http://localhost:8080/api/procedimentos';

// URLs de redirecionamento
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
    document.getElementById('procedimento-id').value = '';
    document.getElementById('nome').value = '';
    document.getElementById('descricao').value = '';
    document.getElementById('preco').value = '';
    showFormMessage('', false);
}

// --- LÓGICA DE CARREGAMENTO E EXIBIÇÃO ---
async function loadProcedimentos() {
    const tbody = document.querySelector('#procedimentos-table tbody');
    tbody.innerHTML = '<tr><td colspan="5">Carregando procedimentos...</td></tr>';
    showListMessage('', false);

    try {
        const response = await fetchAuthenticated(API_PROCEDIMENTOS_URL);
        const procedimentos = await response.json(); // Array de procedimentos

        tbody.innerHTML = '';

        if (procedimentos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5">Nenhum procedimento cadastrado.</td></tr>';
            return;
        }

        procedimentos.forEach(procedimento => {
            const row = tbody.insertRow();
            row.insertCell().textContent = procedimento.id;
            row.insertCell().textContent = procedimento.nome;
            row.insertCell().textContent = procedimento.descricao || 'N/A';
            row.insertCell().textContent = procedimento.preco ? `R$ ${procedimento.preco.toFixed(2)}` : 'N/A';

            const actionsCell = row.insertCell();
            const editButton = document.createElement('button');
            editButton.textContent = 'Editar';
            editButton.className = 'button edit';
            editButton.addEventListener('click', () => editProcedimento(procedimento));
            actionsCell.appendChild(editButton);

            const userRole = getUserRole();
            // Apenas ADMIN pode deletar procedimentos
            if (userRole === 'ROLE_ADMIN') {
                const deleteButton = document.createElement('button');
                deleteButton.textContent = 'Excluir';
                deleteButton.className = 'button delete';
                deleteButton.addEventListener('click', () => deleteProcedimento(procedimento.id));
                actionsCell.appendChild(deleteButton);
            }
        });
        showListMessage('Procedimentos carregados com sucesso.', false);

    } catch (error) {
        showListMessage(`Erro ao carregar procedimentos: ${error.message}`, true);
        console.error('Erro ao carregar procedimentos:', error);
    }
}

// --- LÓGICA DE EDIÇÃO ---
function editProcedimento(procedimento) {
    document.getElementById('procedimento-id').value = procedimento.id;
    document.getElementById('nome').value = procedimento.nome;
    document.getElementById('descricao').value = procedimento.descricao || '';
    document.getElementById('preco').value = procedimento.preco || '';

    showFormMessage('Editando procedimento...', false);
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

document.getElementById('cancelar-edicao').addEventListener('click', () => {
    clearForm();
    showFormMessage('Formulário limpo.', false);
});

// --- LÓGICA DE SUBMISSÃO DO FORMULÁRIO (ADD/EDIT) ---
document.getElementById('procedimento-form').addEventListener('submit', async (event) => {
    event.preventDefault();

    const procedimentoId = document.getElementById('procedimento-id').value;
    const nome = document.getElementById('nome').value.trim();
    const descricao = document.getElementById('descricao').value.trim();
    const preco = parseFloat(document.getElementById('preco').value);

    // Validações básicas do front-end
    if (!nome || isNaN(preco) || preco <= 0) {
        showFormMessage('Por favor, preencha o nome e um preço válido (positivo).', true);
        return;
    }

    const procedimentoData = {
        id: procedimentoId === '' ? null : Number(procedimentoId),
        nome: nome,
        descricao: descricao,
        preco: preco
    };

    let url = API_PROCEDIMENTOS_URL;
    let method = 'POST';

    if (procedimentoId) {
        url = `${API_PROCEDIMENTOS_URL}/${procedimentoId}`;
        method = 'PUT';
    }
    
    try {
        const response = await fetchAuthenticated(url, {
            method: method,
            body: JSON.stringify(procedimentoData)
        });

        const data = await response.json();
        if (response.ok) {
            showFormMessage(data.message || `Procedimento ${procedimentoId ? 'atualizado' : 'adicionado'} com sucesso!`, false);
            clearForm();
            loadProcedimentos();
        } else {
            const errorMessage = data.message || 'Erro ao salvar procedimento.';
            showFormMessage(errorMessage, true);
        }

    } catch (error) {
        showFormMessage(`Erro na requisição: ${error.message}`, true);
        console.error('Erro ao salvar procedimento:', error);
    }
});


// --- LÓGICA DE EXCLUSÃO ---
async function deleteProcedimento(id) {
    if (!confirm('Tem certeza que deseja excluir este procedimento?')) {
        return;
    }
    showListMessage(`Excluindo procedimento ${id}...`, false);

    try {
        const response = await fetchAuthenticated(`${API_PROCEDIMENTOS_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.status === 204) {
            showListMessage('Procedimento excluído com sucesso!', false);
            loadProcedimentos();
        } else {
            const errorData = await response.json();
            const errorMessage = errorData.message || 'Erro ao excluir procedimento.';
            showListMessage(errorMessage, true);
        }
    } catch (error) {
        showListMessage(`Erro na requisição de exclusão: ${error.message}`, true);
        console.error('Erro ao excluir procedimento:', error);
    }
}


// --- LÓGICA DE INICIALIZAÇÃO DA PÁGINA ---
document.addEventListener('DOMContentLoaded', () => {
    const userRole = getUserRole();

    // Permissões: ADMIN pode gerenciar procedimentos
    if (userRole !== 'ROLE_ADMIN') {
        alert('Acesso negado. Você não tem permissão para gerenciar procedimentos.');
        window.location.href = 'Login(1920).html'; // Redirecionando para o login
        return;
    }

    // Lógica para o botão Voltar ao Painel Inicial
    const voltarPainelButton = document.getElementById('voltar-painel-button-procedimentos');
    if (voltarPainelButton) {
        voltarPainelButton.addEventListener('click', () => {
            window.location.href = ADMIN_DASHBOARD_URL; // Redireciona para o painel do administrador
        });
    } else {
        console.error("DEBUG: Botão 'Voltar ao Painel Inicial' não encontrado no HTML!");
    }

    loadProcedimentos();
});