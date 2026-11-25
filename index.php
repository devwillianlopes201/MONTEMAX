<?php

declare(strict_types=1);

// Inicia a sessão para verificar se existe um usuário logado.
session_start();

// Define o título exibido na página.
$titulo = "MONTEMAX";

// Define a mensagem principal apresentada ao visitante.
$mensagem = "Plataforma para montar móveis ou solicitar um profissional";

// Verifica se existe um usuário identificado na sessão.
$usuarioLogado = isset($_SESSION['id_usuario']);

// Define o tipo de usuário com a primeira letra maiúscula.
$tipoUsuario = '';

if ($usuarioLogado) {
    $tipoUsuario = ucfirst($_SESSION['tipo_usuario']);
}

?>

<!DOCTYPE html>
<html lang="pt-br">

<head>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title><?= htmlspecialchars($titulo) ?></title>

    <!-- Bootstrap fornece a estrutura visual e o componente de carrossel. -->
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
        rel="stylesheet"
    >

    <!-- Carrega o CSS próprio do MonteMax. -->
    <link rel="stylesheet" href="css/style.css">

    <!-- JavaScript principal do MonteMax. -->
    <script src="javaScript/javascript.js"></script>

</head>

<body>

    <!-- Cabeçalho principal do MonteMax. -->
    <header class="cabecalho">

        <div class="container">

            <div class="d-flex justify-content-between align-items-center py-3">

                <!-- Logo do MonteMax. -->
                <a href="index.php">

                    <img
                        src="imagens/logo-montemax-horizontal.jpg"
                        alt="Logo do MonteMax"
                        class="logo-montemax"
                    >

                </a>

                <!-- Menu principal da página inicial. -->
                <nav>

                    <a href="#inicio">Início</a>

                    <a href="#como-funciona">Como funciona</a>

                </nav>

                <!-- Área de acesso do usuário. -->
                <div>

                    <?php if ($usuarioLogado): ?>

                        <!-- Mostra o nome e o tipo da conta do usuário logado. -->
                        <a href="perfil.php" class="nome-usuario">
                            Olá, <?= htmlspecialchars($_SESSION['nome']) ?>
                            (<?= htmlspecialchars($tipoUsuario) ?>)!
                        </a>

                        <!-- Permite encerrar a sessão. -->
                        <a href="logout.php">Sair</a>

                    <?php else: ?>

                        <!-- Permite acessar a página de login. -->
                        <a href="login.php" class="botao-entrar">
                            Entrar
                        </a>

                    <?php endif; ?>

                </div>

            </div>

        </div>

    </header>


    <!-- Carrossel principal do MonteMax. -->
    <section id="inicio" class="carrossel-montemax">

        <div
            id="carrosselMonteMax"
            class="carousel slide"
            data-bs-ride="carousel"
        >

            <!-- Indicadores dos slides. -->
            <div class="carousel-indicators">

                <button
                    type="button"
                    data-bs-target="#carrosselMonteMax"
                    data-bs-slide-to="0"
                    class="active"
                    aria-current="true"
                    aria-label="Montar sozinho"
                ></button>

                <button
                    type="button"
                    data-bs-target="#carrosselMonteMax"
                    data-bs-slide-to="1"
                    aria-label="Pedir ajuda na comunidade"
                ></button>

                <button
                    type="button"
                    data-bs-target="#carrosselMonteMax"
                    data-bs-slide-to="2"
                    aria-label="Pedir um profissional"
                ></button>

            </div>


            <!-- Conteúdo dos slides. -->
            <div class="carousel-inner">


                <!-- PRIMEIRO SLIDE: MONTAR SOZINHO. -->
                <div class="carousel-item active">

                    <img
                        src="imagens/carrossel-montar.jpg"
                        class="d-block w-100 imagem-carrossel"
                        alt="Pessoa montando um móvel"
                    >

                    <div class="carousel-caption">

                        <h2>
                            Montar
                            <span>sozinho</span>
                        </h2>

                        <p>
                            Tutoriais passo a passo, dicas e informações
                            para ajudar você a montar seus próprios móveis.
                        </p>

                    </div>

                </div>


                <!-- SEGUNDO SLIDE: COMUNIDADE. -->
                <div class="carousel-item">

                    <img
                        src="imagens/carrossel-comunidade.jpg"
                        class="d-block w-100 imagem-carrossel"
                        alt="Pessoa buscando ajuda na comunidade"
                    >

                    <div class="carousel-caption">

                        <h2>
                            Pedir ajuda
                            <span>na comunidade</span>
                        </h2>

                        <p>
                            Compartilhe dúvidas, experiências, fotos e dicas
                            com outros usuários do MonteMax.
                        </p>

                    </div>

                </div>


                <!-- TERCEIRO SLIDE: PROFISSIONAL. -->
                <div class="carousel-item">

                    <img
                        src="imagens/carrossel-profissional.jpg"
                        class="d-block w-100 imagem-carrossel"
                        alt="Profissional montando um móvel"
                    >

                    <div class="carousel-caption">

                        <h2>
                            Pedir um
                            <span>profissional</span>
                        </h2>

                        <p>
                            Conheça a proposta do MonteMax para encontrar
                            profissionais especializados em montagem de móveis.
                        </p>

                    </div>

                </div>

            </div>


            <!-- Botão para voltar para o slide anterior. -->
            <button
                class="carousel-control-prev"
                type="button"
                data-bs-target="#carrosselMonteMax"
                data-bs-slide="prev"
            >

                <span
                    class="carousel-control-prev-icon"
                    aria-hidden="true"
                ></span>

                <span class="visually-hidden">
                    Anterior
                </span>

            </button>


            <!-- Botão para avançar para o próximo slide. -->
            <button
                class="carousel-control-next"
                type="button"
                data-bs-target="#carrosselMonteMax"
                data-bs-slide="next"
            >

                <span
                    class="carousel-control-next-icon"
                    aria-hidden="true"
                ></span>

                <span class="visually-hidden">
                    Próximo
                </span>

            </button>

        </div>

    </section>


    <!-- Seção que apresenta o MonteMax. -->
    <section id="sobre" class="sobre-montemax">

        <div class="container text-center">

            <h2>O que é o MonteMax?</h2>

            <p>
                O MonteMax é uma plataforma criada para ajudar pessoas
                na montagem de móveis, oferecendo informações, experiências
                compartilhadas e acesso a profissionais.
            </p>

        </div>

    </section>


    <!-- Seção que apresenta os recursos do MonteMax. -->
    <section id="como-funciona" class="recursos-montemax">

        <div class="container text-center">

            <h2>Como o MonteMax pode ajudar?</h2>

            <div class="row">

                <!-- Recurso para quem deseja montar o móvel sozinho. -->
                <div class="col-md-4">

                    <!-- Ícone dos tutoriais. -->
                    <img
                        src="imagens/icone-tutoriais.png"
                        alt="Ícone de tutoriais"
                        class="icone-recurso"
                    >

                    <h3>Montar sozinho</h3>

                    <p>
                        Consulte orientações, tutoriais e experiências
                        que podem ajudar na montagem do seu móvel.
                    </p>
                    <a href="montar-sozinho.php" class="botao-entrar" >

                      Saiba mais
                    </a>
                    
                      
                      

                </div>


                <!-- Recurso para quem deseja pedir ajuda na comunidade. -->
                <div class="col-md-4">

                    <!-- Ícone da comunidade. -->
                    <img
                        src="imagens/icone-comunidade.png"
                        alt="Ícone da comunidade"
                        class="icone-recurso"
                    >

                    <h3>Pedir ajuda</h3>

                    <p>
                        Compartilhe dúvidas, fotos e experiências
                        com outros usuários da comunidade.
                    </p>
                    <a href="comunidade.php" class="botao-entrar">
                        Saiba mais
                    </a>

                </div>


                <!-- Recurso para quem deseja contratar um profissional. -->
                <div class="col-md-4">

                    <!-- Ícone dos profissionais. -->
                    <img
                        src="imagens/icone-profissionais.png"
                        alt="Ícone de profissionais"
                        class="icone-recurso"
                    >

                    <h3>Pedir um profissional</h3>

                    <p>
                        Encontre profissionais para ajudar na montagem
                        ou desmontagem do seu móvel.
                    </p>
                     <a href="profissional.php" class="botao-entrar">
                        Saiba mais
                    </a>

                </div>

            </div>

        </div>

    </section>


    <!-- Seção de cadastro. -->
    <section class="cadastro-montemax">

        <div class="container text-center">

            <h2>Faça parte do MonteMax</h2>

            <p>
                Crie sua conta e conheça a plataforma.
            </p>

            <div class="botoes-cadastro">

                <!-- Cadastro unificado para clientes e profissionais. -->
                <a
                    href="cadastro.php"
                    class="botao-cadastro"
                >
                    Criar minha conta
                </a>

            </div>

        </div>

    </section>


    <!-- Rodapé do MonteMax. -->
    <footer class="rodape">

        <p>
            © 2026 MonteMax. Todos os direitos reservados.
        </p>

    </footer>


    <!-- JavaScript do Bootstrap necessário para o funcionamento do carrossel. -->
    <script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js">
    </script>

</body>

</html>
