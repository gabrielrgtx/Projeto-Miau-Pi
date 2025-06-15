// js/script-cadastro.js

// --- CONFIGURAÇÕES GERAIS ---
const API_AUTH_URL = 'http://localhost:8080/api/auth';
const LOGIN_PAGE_URL = 'Login(1920).html'; // Página de login para redirecionar


// --- FUNÇÕES AUXILIARES DE FEEDBACK ---
function showFormMessage(message, isError = false) {
    const messageElement = document.getElementById('form-message');
    messageElement.textContent = message;
    messageElement.style.color = isError ? 'red' : 'green';
    messageElement.style.display = 'block';
}

// --- LÓGICA PRINCIPAL: ENVIO DO FORMULÁRIO DE CADASTRO ---
document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('register-form');
    const roleSelect = document.getElementById('role'); // Seleciona o elemento select da role

    registerForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const nome = document.getElementById('nome').value.trim();
        const sobrenome = document.getElementById('sobrenome').value.trim();
        const email = document.getElementById('email').value.trim();
        const cpf = document.getElementById('cpf').value.trim().replace(/\D/g, ''); // Limpa CPF
        const password = document.getElementById('password').value;
        const endereco = document.getElementById('endereco').value.trim();
        const telefone = document.getElementById('telefone').value.trim();
        const dataNascimento = document.getElementById('data-nascimento').value; // Formato YYYY-MM-DD
        const role = roleSelect.value; // Pega o VALOR selecionado do select


        // Validações básicas do front-end
        if (!nome || !sobrenome || !email || !cpf || !password || !role) {
            showFormMessage('Por favor, preencha todos os campos obrigatórios.', true);
            return;
        }
        if (password.length < 6) {
            showFormMessage('A senha deve ter no mínimo 6 caracteres.', true);
            return;
        }
        // Adicionar validação de formato de e-mail e CPF se desejar

        const registerData = {
            nome: nome,
            sobrenome: sobrenome,
            email: email,
            cpf: cpf,
            password: password,
            role: role // Garante que o valor da role está aqui
        };

        // Adiciona campos opcionais apenas se preenchidos
        if (endereco) registerData.endereco = endereco;
        if (telefone) registerData.telefone = telefone;
        if (dataNascimento) registerData.dataNascimento = dataNascimento; // Data de Nascimento para Tutor

        try {
            const response = await fetch(`${API_AUTH_URL}/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(registerData)
            });

            const data = await response.json(); // Tenta ler a resposta JSON

            if (response.ok) { // Status 200-299 (ex: 201 Created)
                showFormMessage(data.message || 'Cadastro realizado com sucesso!', false);
                registerForm.reset(); // Limpa o formulário
                // Opcional: Redirecionar para login após o cadastro
                setTimeout(() => {
                    window.location.href = LOGIN_PAGE_URL;
                }, 2000); // Redireciona após 2 segundos

            } else { // Status de erro (4xx, 5xx)
                // Tenta exibir a mensagem de erro do backend se houver
                const errorMessage = data.message || `Erro ${response.status}: Erro desconhecido ao cadastrar.`;
                showFormMessage(errorMessage, true);
                console.error('Erro de cadastro:', response.status, data);
            }

        } catch (error) {
            // Erro na requisição (ex: rede offline, JSON malformado do back-end)
            showFormMessage(`Erro na requisição: ${error.message}. Verifique sua conexão ou o servidor.`, true);
            console.error('Erro na requisição de cadastro:', error);
        }
    });
});