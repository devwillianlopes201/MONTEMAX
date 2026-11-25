// Aguarda a página carregar completamente.
document.addEventListener("DOMContentLoaded", function () {

    // ========================================
    // ENVIO DOS FORMULÁRIOS
    // ========================================

    // Procura todos os formulários da página.
    const formularios = document.querySelectorAll("form");

    // Percorre todos os formulários encontrados.
    formularios.forEach(function (formulario) {

        // Executa quando o formulário for enviado.
        formulario.addEventListener("submit", function () {

            // Procura o botão de envio do formulário.
            const botao = formulario.querySelector("button[type='submit']");

            // Verifica se o botão existe.
            if (botao) {

                // Desabilita o botão para evitar vários cliques.
                botao.disabled = true;

                // Altera o texto do botão somente se ele
                // ainda estiver com o texto original de envio.
                if (botao.textContent.trim() === "Enviar") {
                    botao.textContent = "Enviando...";
                }
            }
        });
    });


    // ========================================
    // CONFIRMAÇÃO DO LOGOUT
    // ========================================

    // Procura o link de sair da conta.
    const linkLogout = document.querySelector("a[href='logout.php']");

    // Verifica se o link de logout existe.
    if (linkLogout) {

        // Executa quando o usuário clicar em Sair.
        linkLogout.addEventListener("click", function (evento) {

            // Pergunta se o usuário realmente deseja sair.
            const confirmar = confirm("Deseja realmente sair da sua conta?");

            // Se clicar em Cancelar, impede o logout.
            if (!confirmar) {
                evento.preventDefault();
            }
        });
    }

});
