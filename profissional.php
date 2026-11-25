<?php

//  título da página.
$titulo = "Pedir um profissional | MonteMax";

?>

<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title><?php echo $titulo; ?></title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- CSS principal -->
    <link rel="stylesheet" href="css/style.css">

    <style>
        .navbar .nav-link {
            color: #123456 !important;
            font-weight: 500;
        }

        .navbar .nav-link:hover {
            color: #f28c28 !important;
        }
        .conteudo-profissional {
            max-width: 1100px;
            margin: 50px auto;
            padding: 20px;
        }

        .titulo-pagina {
            text-align: center;
            color: #123456;
            font-weight: bold;
            margin-bottom: 20px;
        }

        .descricao-pagina {
            text-align: center;
            color: #333;
            line-height: 1.8;
            max-width: 850px;
            margin: 0 auto 40px;
        }

        .card-etapa {
            background-color: #ffffff;
            padding: 25px;
            border-radius: 10px;
            height: 100%;
            box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
            border-top: 4px solid #f28c28;
        }

        .card-etapa h3 {
            color: #123456;
            font-size: 21px;
            font-weight: bold;
            margin-bottom: 15px;
        }

        .card-etapa p {
            color: #333333;
            line-height: 1.7;
            margin-bottom: 0;
        }

        .numero-etapa {
            background-color: #123456;
            color: white;
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            margin-bottom: 15px;
        }

        .dica-profissional {
            background-color: #fff3e0;
            border-left: 5px solid #f28c28;
            padding: 25px;
            border-radius: 8px;
            margin-top: 40px;
            color: #333333;
        }

        .dica-profissional h3 {
            color: #123456;
            font-weight: bold;
        }

        .dica-profissional p {
            color: #333333;
            line-height: 1.7;
            margin-bottom: 0;
        }

        .botao-voltar {
            display: block;
            width: fit-content;
            margin: 35px auto 0;
            text-decoration: none;
        }
    </style>

</head>

<body>

    <!-- Cabeçalho -->
    <header>

        <nav class="navbar navbar-expand-lg">

            <div class="container">

                <a class="navbar-brand" href="index.php">
                    <img src="imagens/logo-montemax-horizontal.jpg"
                         alt="Logo MonteMax"
                         style="max-width: 200px;">
                </a>

                <button class="navbar-toggler" type="button"
                        data-bs-toggle="collapse"
                        data-bs-target="#menuPrincipal">

                    <span class="navbar-toggler-icon"></span>

                </button>

                <div class="collapse navbar-collapse" id="menuPrincipal">

                    <ul class="navbar-nav ms-auto">

                        <li class="nav-item">
                            <a class="nav-link" href="index.php">Início</a>
                        </li>

                        <li class="nav-item">
                            <a class="nav-link" href="index.php#como-funciona">
                                Como funciona
                            </a>
                        </li>

                        <li class="nav-item">
                            <a class="nav-link" href="index.php#inicio">
                                Sobre o MonteMax
                            </a>
                        </li>

                    </ul>

                </div>

            </div>

        </nav>

    </header>


    <!-- Conteúdo principal -->
    <main class="conteudo-profissional">

        <h1 class="titulo-pagina">
            Encontre um profissional para seu móvel
        </h1>

        <p class="descricao-pagina">
            Nem sempre é fácil realizar a montagem ou desmontagem
            de um móvel sozinho. O MonteMax tem como objetivo
            aproximar clientes de profissionais que oferecem
            serviços especializados, facilitando a busca por ajuda.
        </p>


        <!-- Etapas de funcionamento -->
        <div class="row g-4">

            <div class="col-md-6 col-lg-3">

                <div class="card-etapa">

                    <div class="numero-etapa">1</div>

                    <h3>Encontre profissionais</h3>

                    <p>
                        A plataforma permitirá buscar profissionais
                        considerando informações como cidade,
                        localização e área de atendimento.
                    </p>

                </div>

            </div>


            <div class="col-md-6 col-lg-3">

                <div class="card-etapa">

                    <div class="numero-etapa">2</div>

                    <h3>Conheça os serviços</h3>

                    <p>
                        Consulte o perfil, a descrição dos serviços
                        oferecidos e as categorias de atuação,
                        como montagem de móveis para quarto e cozinha.
                    </p>

                </div>

            </div>


            <div class="col-md-6 col-lg-3">

                <div class="card-etapa">

                    <div class="numero-etapa">3</div>

                    <h3>Consulte as avaliações</h3>

                    <p>
                        As avaliações permitem conhecer a experiência
                        de outros clientes e ajudam na escolha
                        de um profissional.
                    </p>

                </div>

            </div>


            <div class="col-md-6 col-lg-3">

                <div class="card-etapa">

                    <div class="numero-etapa">4</div>

                    <h3>Acompanhe o serviço</h3>

                    <p>
                        Após enviar uma solicitação, o cliente poderá
                        acompanhar o andamento do atendimento e
                        confirmar a conclusão do serviço.
                    </p>

                </div>

            </div>

        </div>


      
        <div class="dica-profissional">

            <h3>Mais facilidade na hora de montar!</h3>

            <p>
                O MonteMax busca facilitar o contato entre clientes
                e profissionais de montagem e desmontagem de móveis.
                A proposta é reunir informações sobre os serviços,
                avaliações e solicitações em uma única plataforma,
                tornando essa experiência mais prática e organizada.
            </p>

        </div>


        <!-- Botão voltar -->
        <a href="index.php" class="botao-entrar botao-voltar">
            Voltar para o início
        </a>

    </main>


    <!-- Rodapé -->
    <footer>

        <div class="container text-center">

            <p>
                &copy; <?php echo date('Y'); ?> MonteMax.
                
            </p>

        </div>

    </footer>


    <!-- JavaScript do Bootstrap -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>

</body>

</html>
