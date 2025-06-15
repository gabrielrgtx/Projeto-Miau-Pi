// js/script-login.js

// --- CONFIGURAÇÕES GERAIS ---
const API_AUTH_URL = 'http://localhost:8080/api/auth'; // URL do seu endpoint de autenticação

// Defina as URLs de redirecionamento com base nas roles
// AJUSTE ESTES CAMINHOS PARA AS SUAS PÁGINAS REAIS DE DASHBOARD
const DASHBOARD_URLS = {
    ROLE_ADMIN: 'admin-dashboard.html', // APENAS O NOME DO ARQUIVO SE ESTIVER NA MESMA PASTA
    ROLE_FUNCIONARIO: 'funcionario-dashboard.html',
    ROLE_VETERINARIO: 'veterinario-dashboard.html',
    ROLE_TUTOR: 'tutor-dashboard.html'
};
// URL padrão caso a role não seja reconhecida ou não esteja definida (pode ser a página de login)
const DEFAULT_REDIRECT_URL = 'Login(1920).html'; 

// --- FUNÇÕES AUXILIARES DE FEEDBACK ---
function showFormMessage(message, isError = false) {
    let messageElement = document.getElementById('form-message');
    if (!messageElement) {
        messageElement = document.createElement('p');
        messageElement.id = 'form-message';
        messageElement.style.marginTop = '10px';
        document.getElementById('login-form').appendChild(messageElement);
    }
    messageElement.textContent = message;
    messageElement.style.color = isError ? 'red' : 'green';
    messageElement.style.display = 'block';
}

function hideFormMessage() {
    const messageElement = document.getElementById('form-message');
    if (messageElement) {
        messageElement.style.display = 'none';
    }
}

function hideStatusIcons() {
    const emailStatusIcon = document.getElementById('email-status');
    const senhaStatusIcon = document.getElementById('senha-status');
    if (emailStatusIcon) emailStatusIcon.className = 'status-icon';
    if (senhaStatusIcon) senhaStatusIcon.className = 'status-icon';
}


// --- LÓGICA PRINCIPAL (Executada quando o HTML é carregado) ---
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const emailInput = document.getElementById('email');
    const senhaInput = document.getElementById('senha');
    const emailStatus = document.getElementById('email-status');
    const senhaStatus = document.getElementById('senha-status');

    // --- EFEITOS VISUAIS E ANIMAÇÕES (Seu código original mantido) ---
    const particleCanvas = document.getElementById('particle-canvas');
    const pCtx = particleCanvas.getContext('2d');
    let pW, pH;
    function resizeParticles(){ pW = particleCanvas.width = window.innerWidth; pH = particleCanvas.height = window.innerHeight; }
    window.addEventListener('resize', resizeParticles); resizeParticles();
    const particles = []; for(let i=0; i<100; i++){ particles.push({ x: Math.random()*pW, y: Math.random()*pH, vx: (Math.random()-0.5)*0.5, vy: (Math.random()-0.5)*0.5, size: Math.random()*2+1 }); }
    function updateParticles(){ pCtx.clearRect(0,0,pW,pH); particles.forEach(p=>{ p.x+=p.vx; p.y+=p.vy; if(p.x<0||p.x>pW) p.vx*=-1; if(p.y<0||p.y>pH) p.vy*=-1; pCtx.fillStyle='rgba(200,200,200,0.5)'; pCtx.beginPath(); pCtx.arc(p.x,p.y,p.r,0,Math.PI*2); pCtx.fill(); }); requestAnimationFrame(updateParticles); }
    updateParticles();

    const mCanvas = document.getElementById('mascot-canvas');
    const mCtx = mCanvas.getContext('2d');
    const mascots = [{emoji:'😸',x:50,y:40,blink:true},{emoji:'🐶',x:150,y:40,blink:false}];
    function drawMascots(){ mCtx.clearRect(0,0,mCanvas.width,mCanvas.height); mascots.forEach((m,i)=>{ m.y = 40 + Math.sin(Date.now()/500 + i)*5; if(m.blink && Math.floor(Date.now()/500)%20===0){ m.emoji = m.emoji==='😸'?'😺':'😸'; } mCtx.font='48px serif'; mCtx.fillText(m.emoji,m.x,m.y); }); requestAnimationFrame(drawMascots); }
    drawMascots();
    
    document.querySelectorAll('.button').forEach((btn,idx)=>{ setTimeout(()=>btn.classList.add('show'),100*idx); });
    // --- FIM DOS EFEITOS VISUAIS ---


    // --- LÓGICA DE VALIDAÇÃO DO FRONTEND ---
    function validateEmailFormat(){
        const valid = /^\S+@\S+\.\S+$/.test(emailInput.value);
        emailStatus.textContent = valid?'✓':'';
        emailStatus.className = 'status-icon '+(valid?'valid':'invalid');
        return valid;
    }

    function validateSenhaLength(){
        const valid = senhaInput.value.length >= 6;
        senhaStatus.textContent = valid?'✓':'';
        senhaStatus.className = 'status-icon '+(valid?'valid':'invalid');
        return valid;
    }

    emailInput.addEventListener('input', validateEmailFormat);
    senhaInput.addEventListener('input', validateSenhaLength);


    // --- LÓGICA DO MINIGAME (REMOVIDA OU AJUSTADA PARA O REDIRECIONAMENTO FINAL) ---
    const gameOverlay = document.getElementById('game-overlay');
    const gameCanvas = document.getElementById('game-canvas');
    const gCtx = gameCanvas.getContext('2d');
    let bubbles = [], bubbleCount = 10;

    function startGameOrRedirect(userRole) {
        if (gameOverlay && gameCanvas) {
            console.log('Minigame presente, iniciando...');
             handleSuccessfulLoginRedirect(userRole); // Direciona direto para testar o fluxo
        } else {
            console.log('Minigame não presente ou desativado. Redirecionando diretamente...');
            handleSuccessfulLoginRedirect(userRole);
        }
    }


    // --- LÓGICA DE REDIRECIONAMENTO POR ROLE ---
    function handleSuccessfulLoginRedirect(role) {
        let redirectUrl = DEFAULT_REDIRECT_URL; 

        switch(role) {
            case 'ROLE_ADMIN':
                redirectUrl = DASHBOARD_URLS.ROLE_ADMIN;
                break;
            case 'ROLE_FUNCIONARIO':
                redirectUrl = DASHBOARD_URLS.ROLE_FUNCIONARIO;
                break;
            case 'ROLE_VETERINARIO':
                redirectUrl = DASHBOARD_URLS.ROLE_VETERINARIO;
                break;
            case 'ROLE_TUTOR':
                redirectUrl = DASHBOARD_URLS.ROLE_TUTOR;
                break;
            default:
                console.warn('Role desconhecida:', role, 'Redirecionando para URL padrão.');
                redirectUrl = DEFAULT_REDIRECT_URL; 
        }
        console.log('DEBUG: Redirecionando para:', redirectUrl);
        window.location.href = redirectUrl;
    }


    // --- LÓGICA PRINCIPAL: ENVIO DO FORMULÁRIO DE LOGIN PARA O BACKEND ---
    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        hideFormMessage();
        hideStatusIcons();

        const email = emailInput.value.trim();
        const senha = senhaInput.value;

        if (!email || !senha || !validateEmailFormat() || !validateSenhaLength()) {
            showFormMessage('Por favor, preencha todos os campos corretamente.', true);
            return;
        }

        try {
            const response = await fetch(`${API_AUTH_URL}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({ email: email, password: senha })
            });

            if (response.ok) { 
                const data = await response.json();

                if (data.message === "Login realizado com sucesso!" && data.email && data.role) {
                    console.log("LOGIN BEM-SUCEDIDO:", data.message, "Email:", data.email, "Role:", data.role);
                    showFormMessage('Login bem-sucedido!', false);

                    localStorage.setItem('userEmail', data.email);
                    localStorage.setItem('userRole', data.role); 
                    // NOVO/CORRIGIDO: ARMAZENA A SENHA NO LOCALSTORAGE
                    localStorage.setItem('userPassword', senha); 

                    startGameOrRedirect(data.role);

                } else {
                    showFormMessage('Resposta inesperada do servidor após login bem-sucedido.', true);
                    console.error('Resposta inesperada ou email/role ausente:', data);
                }

            } else {
                let errorData;
                try {
                    errorData = await response.json();
                } catch (jsonError) {
                    errorData = { message: `Erro do servidor: Status ${response.status}` };
                }
                const errorMessage = errorData.message || 'Credenciais inválidas ou erro desconhecido.';
                showFormMessage(errorMessage, true);
                console.error('Erro de login:', response.status, errorData);
            }

        } catch (error) {
            console.error('Erro na requisição de login:', error);
            showFormMessage('Não foi possível conectar ao servidor. Verifique sua conexão ou o status do backend.', true);
        }
    });
});