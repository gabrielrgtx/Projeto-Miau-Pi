// js/script-admin-dashboard.js (Script específico para o Painel do Administrador)

// --- CONFIGURAÇÕES ESPECÍFICAS DESTE PAINEL ---
// As URLs das páginas de gerenciamento (devem ser relativas à raiz do Live Server - MIAUEAUAU/)
const GERENCIAR_FUNCIONARIOS_URL = 'gerenciar-funcionarios.html';
const GERENCIAR_VETERINARIOS_URL = 'gerenciar-veterinarios.html';
const GERENCIAR_TUTORES_URL = 'gerenciar-tutores.html'; // Nova página
const GERENCIAR_PACIENTES_URL = 'gerenciar-pacientes.html'; // Nova página
const GERENCIAR_CONSULTAS_URL = 'gerenciar-consultas.html'; // Nova página
const GERENCIAR_PRONTUARIOS_URL = 'gerenciar-prontuarios.html'; // Nova página
const GERENCIAR_PROCEDIMENTOS_URL = 'gerenciar-procedimentos.html'; // Nova página
const VER_RELATORIOS_FINANCEIROS_URL = 'relatorios-financeiros.html'; // Nova página
const VER_RELATORIOS_ATENDIMENTO_URL = 'relatorios-atendimento.html'; // Nova página


// --- LÓGICA DE INICIALIZAÇÃO DA PÁGINA (Executada após script-dashboard-universal.js) ---
document.addEventListener('DOMContentLoaded', () => {
    const userRole = getUserRole(); // Obtém a role do localStorage (função de script-dashboard-universal.js)

    // Esconder/mostrar seções com base na role
    const adminSections = ['gerenciamento-usuarios', 'gerenciamento-clinica', 'relatorios-sistema'];
    const funcionarioSections = ['gerenciamento-atendimento', 'gerenciamento-prontuarios', 'outras-acoes']; // Exemplo de IDs de seção de funcionário
    const tutorSections = ['pets-section', 'consultas-section', 'historico-section']; // Exemplo de IDs de seção de tutor

    function hideAllSections() {
        // Inclua TODOS os IDs de seções possíveis de TODAS as dashboards aqui
        const allPossibleSections = [
            ...adminSections,
            ...funcionarioSections,
            ...tutorSections
        ];
        allPossibleSections.forEach(id => {
            const section = document.getElementById(id);
            if (section) section.style.display = 'none'; // Esconde tudo por padrão
        });
    }
    hideAllSections(); 

    // Mostra apenas as seções relevantes para o ADMIN
    switch (userRole) {
        case 'ROLE_ADMIN':
            adminSections.forEach(id => { const section = document.getElementById(id); if (section) section.style.display = 'block'; });
            break;
        default:
            // Se o usuário não for admin e tentar acessar, o script-dashboard-universal.js já redirecionaria
            hideAllSections(); // Esconde tudo se, por algum motivo, não for admin
            console.warn('DEBUG: Usuário não ADMIN tentou carregar script admin-dashboard.');
            break;
    }

    // --- ATRELAR EVENTOS DE CLIQUE AOS BOTÕES "GERENCIAR" ---
    // Fazemos isso apenas se os botões existem na página (se a seção for visível)
    const gerenciarFuncionariosBtn = document.getElementById('gerenciar-funcionarios-btn');
    if (gerenciarFuncionariosBtn) gerenciarFuncionariosBtn.addEventListener('click', () => { window.location.href = GERENCIAR_FUNCIONARIOS_URL; });

    const gerenciarVeterinariosBtn = document.getElementById('gerenciar-veterinarios-btn');
    if (gerenciarVeterinariosBtn) gerenciarVeterinariosBtn.addEventListener('click', () => { window.location.href = GERENCIAR_VETERINARIOS_URL; });

    const gerenciarTutoresBtn = document.getElementById('gerenciar-tutores-btn');
    if (gerenciarTutoresBtn) gerenciarTutoresBtn.addEventListener('click', () => { window.location.href = GERENCIAR_TUTORES_URL; });

    const gerenciarPacientesBtn = document.getElementById('gerenciar-pacientes-btn');
    if (gerenciarPacientesBtn) gerenciarPacientesBtn.addEventListener('click', () => { window.location.href = GERENCIAR_PACIENTES_URL; });

    const gerenciarConsultasBtn = document.getElementById('gerenciar-consultas-btn');
    if (gerenciarConsultasBtn) gerenciarConsultasBtn.addEventListener('click', () => { window.location.href = GERENCIAR_CONSULTAS_URL; });

    const gerenciarProntuariosBtn = document.getElementById('gerenciar-prontuarios-btn');
    if (gerenciarProntuariosBtn) gerenciarProntuariosBtn.addEventListener('click', () => { window.location.href = GERENCIAR_PRONTUARIOS_URL; });

    const gerenciarProcedimentosBtn = document.getElementById('gerenciar-procedimentos-btn');
    if (gerenciarProcedimentosBtn) gerenciarProcedimentosBtn.addEventListener('click', () => { window.location.href = GERENCIAR_PROCEDIMENTOS_URL; });

    const verRelatoriosFinanceirosBtn = document.getElementById('ver-relatorios-financeiros-btn');
    if (verRelatoriosFinanceirosBtn) verRelatoriosFinanceirosBtn.addEventListener('click', () => { window.location.href = VER_RELATORIOS_FINANCEIROS_URL; });

    const verRelatoriosAtendimentoBtn = document.getElementById('ver-relatorios-atendimento-btn');
    if (verRelatoriosAtendimentoBtn) verRelatoriosAtendimentoBtn.addEventListener('click', () => { window.location.href = VER_RELATORIOS_ATENDIMENTO_URL; });
});