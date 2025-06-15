// js/script-dashboard-universal.js (Script universal para todas as dashboards)

// --- CONFIGURAÇÕES GERAIS ---
const LOGIN_PAGE_URL = 'Login(1920).html'; // Página de login

// URLs das dashboards - AJUSTE ESTES CAMINHOS PARA OS SEUS ARQUIVOS HTML REAIS
// Se você está abrindo o Live Server da pasta MIAUEAUAU, os caminhos devem ser assim:
const DASHBOARD_URLS = {
    ROLE_ADMIN: 'admin-dashboard.html',
    ROLE_FUNCIONARIO: 'funcionario-dashboard.html',
    ROLE_VETERINARIO: 'veterinario-dashboard.html',
    ROLE_TUTOR: 'tutor-dashboard.html'
};

// --- FUNÇÕES AUXILIARES DE SESSÃO E AUTENTICAÇÃO ---
function getUserEmail() {
    return localStorage.getItem('userEmail');
}

function getUserRole() {
    return localStorage.getItem('userRole');
}

function getUserPassword() {
    return localStorage.getItem('userPassword');
}

// Função para lidar com o logout
function handleLogout() {
    console.log("DEBUG: Botão Sair clicado. Realizando logout...");
    localStorage.removeItem('userEmail');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userPassword');
    window.location.href = LOGIN_PAGE_URL;
}

// Esta função deve ser usada por *qualquer* script que faça requisições autenticadas
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


// --- LÓGICA DE INICIALIZAÇÃO UNIVERSAL PARA DASHBOARDS ---
// Esta parte será executada em TODAS as páginas de dashboard
document.addEventListener('DOMContentLoaded', () => {
    const userEmailSpan = document.getElementById('user-email');
    const logoutButton = document.getElementById('logout-button');

    const loggedInEmail = getUserEmail();
    const userRole = getUserRole();

    // 1. Verificação de sessão e redirecionamento de segurança
    if (!loggedInEmail || !userRole) {
        console.log("DEBUG: Sem email ou role no localStorage. Redirecionando para login.");
        alert('Sua sessão expirou ou você não está logado. Por favor, faça login novamente.');
        window.location.href = LOGIN_PAGE_URL;
        return;
    }

    userEmailSpan.textContent = loggedInEmail;
    if (logoutButton) {
        logoutButton.addEventListener('click', handleLogout);
    } else {
        console.error("DEBUG: Botão de logout não encontrado no HTML!");
    }

    // 2. Lógica de redirecionamento se o usuário tentar acessar uma página incorreta
    const currentPagePath = window.location.pathname;
    const currentFileName = currentPagePath.substring(currentPagePath.lastIndexOf('/') + 1);

    const expectedPageForRole = DASHBOARD_URLS[userRole];

    if (userRole !== 'ROLE_ADMIN' && expectedPageForRole && currentFileName !== expectedPageForRole) {
         alert('Você não tem permissão para acessar esta página.');
         window.location.href = expectedPageForRole;
         return;
    }
});