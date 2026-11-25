<?php

// Define o título da página.
$titulo = "Montar sozinho | MonteMax";

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
            Monte seu móvel com mais facilidade!
        </h1>

        <p class="descricao-pagina">

            Montar um móvel por conta própria pode parecer
            complicado no início. Porém, com organização,
            paciência e atenção às instruções, essa tarefa
            pode se tornar mais simples.

            O MonteMax reuniu algumas orientações para
            ajudar você nessa experiência.

        </p>


        <!-- Orientações para montagem -->
        <div class="row g-4">

            <div class="col-md-6 col-lg-4">

                <div class="card-etapa">

                    <h3>1. Leia o manual</h3>

                    <p>
                        Antes de começar, leia o manual de
                        instruções do móvel. Ele apresenta
                        informações importantes sobre as peças,
                        os encaixes e as etapas da montagem.
                    </p>

                </div>

            </div>


            <div class="col-md-6 col-lg-4">

                <div class="card-etapa">

                    <h3>2. Organize as peças</h3>

                    <p>
                        Separe as peças, parafusos e acessórios.
                        Confira se os itens correspondem à lista
                        apresentada no manual. Isso facilita
                        a identificação durante a montagem.
                    </p>

                </div>

            </div>


            <div class="col-md-6 col-lg-4">

                <div class="card-etapa">

                    <h3>3. Prepare o ambiente</h3>

                    <p>
                        Escolha um espaço adequado, com boa
                        iluminação e espaço suficiente para
                        organizar as peças e movimentar
                        o móvel com segurança.
                    </p>

                </div>

            </div>


            <div class="col-md-6 col-lg-4">

                <div class="card-etapa">

                    <h3>4. Separe as ferramentas</h3>

                    <p>
                        Verifique quais ferramentas são
                        necessárias para a montagem e deixe
                        tudo preparado antes de iniciar
                        o trabalho.
                    </p>

                </div>

            </div>


            <div class="col-md-6 col-lg-4">

                <div class="card-etapa">

                    <h3>5. Siga cada etapa</h3>

                    <p>
                        Respeite a ordem das instruções
                        e confira os encaixes antes de
                        continuar. Evite pular etapas
                        ou forçar peças.
                    </p>

                </div>

            </div>
             <div class="col-md-6 col-lg-4">

            <div class="card-etapa">

                <h3>6. Tenha paciência</h3>

               	<p>
                    Faça a montagem com calma.
                    Caso encontre alguma dificuldade,
                    consulte novamente o manual antes
                    de prosseguir.
                </p>

            </div>

        </div>

    </div>


    <!-- Destaque MonteMax -->
    <div class="dica-profissional">

        <h3>Dica MonteMax!</h3>

        <p>
            Nem sempre é possível realizar uma montagem
            sozinho. Se você encontrar dificuldades ou
            não tiver as ferramentas necessárias, considere
            procurar a ajuda de um profissional especializado.

            O mais importante é realizar a montagem com
            atenção e segurança.
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
