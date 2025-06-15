// js/script-tutor-dashboard.js

// --- CONFIGURAÇÕES GERAIS ---
const API_PACIENTES_URL = 'http://localhost:8080/api/pacientes';
const API_CONSULTAS_URL = 'http://localhost:8080/api/consultas';

// URLs de redirecionamento (já definidas em script-dashboard-universal.js)
const LOGIN_PAGE_URL = 'Login(1920).html';
const TUTOR_DASHBOARD_URL = 'tutor-dashboard.html'; // Usada para redirecionamento interno


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
function showListMessage(message, isError = false, elementId = 'pets-list') { // Pode ter mensagens diferentes para pets/consultas
    const messageElement = document.getElementById(elementId);
    if (messageElement) {
        messageElement.textContent = message;
        messageElement.className = isError ? 'error' : 'success';
        messageElement.style.display = 'block';
    }
}


// --- LÓGICA DE CARREGAMENTO DE DADOS ESPECÍFICOS DO TUTOR ---

// Carrega Pets do Tutor
async function loadPetsForTutor() {
    const petsListElement = document.getElementById('pets-list');
    if (!petsListElement) return;

    petsListElement.innerHTML = '<p>Carregando seus pets...</p>';

    try {
        const response = await fetchAuthenticated(`${API_PACIENTES_URL}/meus-pets`, { method: 'GET' });
        const pets = await response.json(); // Recebe lista de PacienteResponseDTO

        petsListElement.innerHTML = ''; // Limpa a mensagem de carregamento

        if (pets.length === 0) {
            petsListElement.innerHTML = '<p>Você ainda não tem pets cadastrados.</p>';
            return;
        }

        pets.forEach(petDto => {
            const petCard = document.createElement('div');
            petCard.className = 'card';
            petCard.innerHTML = `
                <h4>${petDto.nome}</h4>
                <p><strong>Espécie:</strong> ${petDto.especie}</p>
                <p><strong>Raça:</strong> ${petDto.raca}</p>
                <p><strong>Data Nasc.:</strong> ${petDto.dataNascimento ? new Date(petDto.dataNascimento).toLocaleDateString() : 'N/A'}</p>
                <p><strong>Peso:</strong> ${petDto.peso} kg</p>
                <p><strong>Tutor:</strong> ${petDto.tutorNomeCompleto || 'N/A'} (ID: ${petDto.tutorId || 'N/A'})</p>
            `;
            petsListElement.appendChild(petCard);
        });
        // showListMessage('Pets carregados com sucesso.', false, 'pets-list'); // Não precisa, os cards já mostram

    } catch (error) {
        showListMessage(`Erro ao carregar pets: ${error.message}`, true, 'pets-list');
        console.error('Erro ao carregar pets:', error);
    }
}

// Carrega Próximas Consultas
async function loadUpcomingConsultasForTutor() {
    const consultasListElement = document.getElementById('consultas-list');
    if (!consultasListElement) return;

    consultasListElement.innerHTML = '<p>Carregando próximas consultas...</p>';

    try {
        const response = await fetchAuthenticated(`${API_CONSULTAS_URL}/minhas-consultas`, { method: 'GET' });
        const consultas = await response.json(); // Recebe lista de ConsultaResponseDTO

        consultasListElement.innerHTML = '';

        if (consultas.length === 0) {
            consultasListElement.innerHTML = '<p>Você não tem próximas consultas agendadas.</p>';
            return;
        }

        consultas.forEach(consultaDto => {
            const consultaCard = document.createElement('div');
            consultaCard.className = 'card consulta-item';
            consultaCard.innerHTML = `
                <h4>Consulta de ${consultaDto.tipoAtendimento}</h4>
                <p><strong>Pet:</strong> ${consultaDto.paciente ? consultaDto.paciente.nome : 'N/A'}</p>
                <p><strong>Veterinário:</strong> ${consultaDto.veterinario ? consultaDto.veterinario.nome + ' ' + consultaDto.veterinario.sobrenome : 'N/A'}</p>
                <p><strong>Data/Hora:</strong> ${consultaDto.dataHora ? new Date(consultaDto.dataHora).toLocaleString() : 'N/A'}</p>
                <p><strong>Motivo:</strong> ${consultaDto.motivo || 'N/A'}</p>
                <p><strong>Confirmada:</strong> ${consultaDto.confirmada ? 'Sim' : 'Não'}</p>
            `;
            consultasListElement.appendChild(consultaCard);
        });
        // showListMessage('Consultas carregadas com sucesso.', false, 'consultas-list'); // Não precisa

    } catch (error) {
        showListMessage(`Erro ao carregar consultas: ${error.message}`, true, 'consultas-list');
        console.error('Erro ao carregar consultas:', error);
    }
}

async function loadConsultasHistorico() {
    const historicoListElement = document.getElementById('historico-list');
    if (!historicoListElement) return;
    historicoListElement.innerHTML = '<p>Funcionalidade de histórico em desenvolvimento. Verifique suas próximas consultas acima.</p>';
}


// --- LÓGICA DE INICIALIZAÇÃO DA PÁGINA ---
document.addEventListener('DOMContentLoaded', () => {
    const userRole = getUserRole(); // Função de script-dashboard-universal.js

    // Apenas Tutores (e Admins que acessarem diretamente) podem carregar este painel
    if (userRole !== 'ROLE_TUTOR' && userRole !== 'ROLE_ADMIN') { // Admin pode acessar para testes
        alert('Acesso negado. Você não tem permissão para acessar este painel.');
        window.location.href = LOGIN_PAGE_URL;
        return;
    }

    // Lógica para o botão Voltar ao Painel Inicial (se existir no cabeçalho)
    const voltarPainelButton = document.getElementById('voltar-painel-button'); // ID do botão no cabeçalho
    if (voltarPainelButton) {
        voltarPainelButton.addEventListener('click', () => {
            // Redireciona para a dashboard apropriada com base na role
            let redirectUrl;
            switch(userRole) {
                case 'ROLE_ADMIN':
                    redirectUrl = ADMIN_DASHBOARD_URL;
                    break;
                case 'ROLE_TUTOR': // Tutor volta para o próprio painel, se ele não for o dashboard principal
                    redirectUrl = TUTOR_DASHBOARD_URL; // Ou para o Login se quiser
                    break;
                default:
                    redirectUrl = LOGIN_PAGE_URL;
            }
            window.location.href = redirectUrl;
        });
    } else {
        console.error("DEBUG: Botão 'Voltar ao Painel Inicial' não encontrado no HTML!");
    }


    // Carrega os dados específicos para a ROLE_TUTOR
    if (userRole === 'ROLE_TUTOR' || userRole === 'ROLE_ADMIN') {
        // Mostra as seções do tutor (se elas estiverem escondidas por padrão no HTML)
        const petsSection = document.getElementById('pets-section');
        if (petsSection) petsSection.style.display = 'block';
        const consultasSection = document.getElementById('consultas-section');
        if (consultasSection) consultasSection.style.display = 'block';
        const historicoSection = document.getElementById('historico-section');
        if (historicoSection) historicoSection.style.display = 'block';

        loadPetsForTutor();
        loadUpcomingConsultasForTutor();
        loadConsultasHistorico();
    }

    // Event listeners para botões de ação específicos (cadastrar pet, marcar consulta)
    const cadastrarPetButton = document.getElementById('cadastrar-pet-button');
    if (cadastrarPetButton) {
        cadastrarPetButton.addEventListener('click', () => {
            alert('Funcionalidade de cadastrar pet será implementada em uma nova página/modal.');
            // window.location.href = 'cadastrar-pet.html'; // Exemplo
        });
    }

    const marcarConsultaButton = document.getElementById('marcar-consulta-button');
    if (marcarConsultaButton) {
        marcarConsultaButton.addEventListener('click', () => {
            alert('Funcionalidade de marcar consulta será implementada em uma nova página/modal.');
            // window.location.href = 'marcar-consulta.html'; // Exemplo
        });
    }
});