<?php
?>

<!DOCTYPE html>
<html lang="pt-br">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title><?php echo $titulo; ?></title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- CSS principal do projeto -->
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

    <!-- Cabeçalho da página -->
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
            Precisa de ajuda com seu móvel?
        </h1>

        <p class="descricao-pagina">
            Com o MonteMax, você poderá encontrar profissionais
            preparados para auxiliar na montagem, desmontagem
            e transporte de móveis. Conheça como essa experiência
            funcionará na plataforma.
        </p>


        <!-- Etapas do funcionamento -->
        <div class="row g-4">

            <div class="col-md-6 col-lg-3">

                <div class="card-etapa">

                    <div class="numero-etapa">1</div>

                    <h3>Descreva sua necessidade</h3>

                    <p>
                        Informe qual móvel precisa ser montado,
                        desmontado ou transportado. Você também
                        poderá compartilhar fotografias para
                        facilitar o entendimento do serviço.
                    </p>

                </div>

            </div>


            <div class="col-md-6 col-lg-3">

                <div class="card-etapa">

                    <div class="numero-etapa">2</div>

                    <h3>Encontre profissionais</h3>

                    <p>
                        A plataforma permitirá localizar
                        profissionais disponíveis na região,
                        facilitando o contato entre clientes
                        e prestadores de serviço.
                    </p>

                </div>

            </div>


            <div class="col-md-6 col-lg-3">

                <div class="card-etapa">

                    <div class="numero-etapa">3</div>

                    <h3>Analise as propostas</h3>

                    <p>
                        O cliente poderá analisar as informações
                        do profissional, sua avaliação e o valor
                        apresentado para escolher a opção mais
                        adequada à sua necessidade.
                    </p>

                </div>

            </div>


            <div class="col-md-6 col-lg-3">

                <div class="card-etapa">

                    <div class="numero-etapa">4</div>

                    <h3>Entre em contato</h3>

                    <p>
                        Após escolher um profissional, o cliente
                        poderá entrar em contato para combinar
                        os detalhes do serviço e dar continuidade
                        à contratação.
                    </p>

                </div>

            </div>

        </div>


        <!-- Destaque informativo -->
        <div class="dica-profissional">

            <h3>Mais praticidade para você!</h3>

            <p>
                O MonteMax tem como objetivo aproximar pessoas
                que precisam de serviços relacionados a móveis
                de profissionais que possuem experiência na área.
                A proposta é facilitar a busca, a comunicação e
                a escolha do prestador de serviço.
            </p>

        </div>


        <!-- Botão para retornar à página inicial -->
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
