
document.addEventListener('DOMContentLoaded', () => {
    const cadastroForm = document.getElementById('cadastro-form');
    const nomeInput = document.getElementById('nome');
    const sobrenomeInput = document.getElementById('sobrenome');
    const emailInput = document.getElementById('email');
    const cpfInput = document.getElementById('cpf');
    const senhaInput = document.getElementById('senha');
    const confirmarSenhaInput = document.getElementById('confirmar_senha');
    const termosCheckbox = document.getElementById('termos');
    const messageElement = document.getElementById('cadastro-form-message'); // Elemento para feedback

    function showMessage(message, isError = false) {
        messageElement.textContent = message;
        messageElement.style.color = isError ? 'red' : 'green';
        messageElement.style.display = 'block';
    }

    function hideMessage() {
        messageElement.style.display = 'none';
        messageElement.textContent = '';
    }

    cpfInput.addEventListener('input', (e) => {
        let value = e.target.value.replace(/\D/g, ''); // Remove tudo que não é dígito
        if (value.length > 3) {
            value = value.substring(0, 3) + '.' + value.substring(3);
        }
        if (value.length > 7) {
            value = value.substring(0, 7) + '.' + value.substring(7);
        }
        if (value.length > 11) {
            value = value.substring(0, 11) + '-' + value.substring(11, 13); // Limita ao máximo de 11 dígitos para o CPF
        }
        if (value.length > 14) { // Impede que digite mais que o formato do CPF
            value = value.substring(0, 14);
        }
        e.target.value = value;
    });

    // Event Listener para a submissão do formulário
    cadastroForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // Impede o envio padrão do formulário (recarregar a página)
        hideMessage(); // Limpa mensagens anteriores

        // Captura os valores dos campos
        const nome = nomeInput.value.trim();
        const sobrenome = sobrenomeInput.value.trim();
        const email = emailInput.value.trim();
        const cpf = cpfInput.value.trim();
        const senha = senhaInput.value;
        const confirmarSenha = confirmarSenhaInput.value;

        // --- Validações do Frontend (antes de enviar ao backend) ---
        if (!nome || !sobrenome || !email || !cpf || !senha || !confirmarSenha) {
            showMessage('Por favor, preencha todos os campos obrigatórios.', true);
            return; // Interrompe a função se a validação falhar
        }

        if (senha !== confirmarSenha) {
            showMessage('As senhas não coincidem. Por favor, verifique.', true);
            return;
        }

        if (senha.length < 8) { // Boa prática de segurança: senha mínima de 8 caracteres
            showMessage('A senha deve ter pelo menos 8 caracteres.', true);
            return;
        }

        // Validação de formato de email básico (o 'type="email"' do HTML já ajuda, mas JS é mais robusto)
        if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email)) {
            showMessage('Por favor, insira um endereço de email válido.', true);
            return;
        }

        // Remove caracteres não numéricos do CPF e valida o comprimento
        const cpfLimpo = cpf.replace(/\D/g, '');
        if (cpfLimpo.length !== 11) {
            showMessage('CPF inválido. Deve conter 11 dígitos numéricos.', true);
            return;
        }
        // *Opcional: Adicionar uma validação de CPF mais avançada (algoritmo de validação de dígitos) aqui.*

        if (!termosCheckbox.checked) {
            showMessage('Você deve aceitar os termos e condições para criar a conta.', true);
            return;
        }

        // Prepara os dados para enviar como JSON para o backend
        const userData = {
            nome: nome,
            sobrenome: sobrenome,
            email: email,
            cpf: cpfLimpo, // Envia o CPF limpo (apenas números)
            password: senha // O nome do campo de senha no backend é 'password'
        };

        try {
            // Faz a requisição POST para o endpoint de registro do seu backend
            const response = await fetch('http://localhost:8080/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json' // Indica que aceita JSON como resposta
                },
                body: JSON.stringify(userData) // Converte o objeto JavaScript em string JSON
            });

            // Verifica se a resposta foi bem-sucedida (status 2xx)
            if (response.ok) {
                // Cadastro realizado com sucesso
                const result = await response.json(); // Pega a resposta JSON do backend
                showMessage('Cadastro realizado com sucesso! Você já pode fazer login.', false);
                console.log('Usuário cadastrado:', result);
                
                // Limpa o formulário após o sucesso
                cadastroForm.reset();
                // Redireciona para a página de login após 3 segundos
                setTimeout(() => {
                    window.location.href = 'index.html'; // Assume que 'index.html' é a página de login
                }, 3000); 

            } else {
                // Houve um erro no backend (status 4xx ou 5xx)
                let errorMessageText = 'Erro desconhecido ao cadastrar. Tente novamente.';
                
                // Primeiro, tente ler como JSON. Se falhar, tente como texto.
                // Usamos `response.clone()` para poder tentar ler o body duas vezes se necessário.
                let errorBody;
                try {
                    errorBody = await response.json(); // Tenta ler como JSON
                    errorMessageText = errorBody.message || errorMessageText;
                } catch (jsonError) {
                    // Se não foi possível ler como JSON, tenta ler como texto simples
                    try {
                        errorBody = await response.text(); // Tenta ler como texto
                        errorMessageText = errorBody.trim() !== '' ? errorBody : errorMessageText;
                    } catch (textError) {
                        console.error("Erro ao tentar ler o corpo da resposta (JSON ou texto):", textError);
                        // Se não conseguiu ler de jeito nenhum, mantém a mensagem padrão.
                    }
                }
                
                showMessage(`Erro no cadastro: ${errorMessageText}`, true);
                console.error('Erro de cadastro:', response.status, errorBody);
            }
        } catch (error) {
            // Erro de rede (servidor não acessível, sem internet, etc.)
            showMessage('Não foi possível conectar ao servidor. Verifique sua conexão.', true);
            console.error('Erro na requisição de cadastro:', error);
        }
    });
});