<?php

declare(strict_types=1);

// Inicia a sessão para acessar os dados do usuário que fez login.
session_start();

// Carrega as configurações do banco de dados.
$config = require_once __DIR__ . '/config/conexao.php';

// Verifica se existe um usuário logado.
if (!isset($_SESSION['id_usuario'])) {

    // Se não estiver logado, volta para a página de login.
    header('Location: login.php');
    exit;
}

// Cria a conexão com o banco de dados.
$pdo = new PDO(
    "mysql:host={$config['host']};port={$config['port']};dbname={$config['name']};charset=utf8mb4",
    $config['user'],
    $config['pass']
);

// Define o PDO para mostrar erros como exceções.
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

// Busca os dados do usuário e, quando ele for profissional,
// também busca os dados específicos da tabela profissional.
$sql = "SELECT
            u.id_usuario,
            u.nome,
            u.email,
            u.tipo_usuario,
            u.foto_perfil,
            p.whatsapp,
            p.descricao,
            p.cidade
        FROM usuario u
        LEFT JOIN profissional p
            ON p.id_usuario = u.id_usuario
        WHERE u.id_usuario = :id_usuario";

// Prepara a consulta para evitar SQL Injection.
$stmt = $pdo->prepare($sql);

// Envia o ID do usuário para a consulta.
$stmt->execute([
    ':id_usuario' => $_SESSION['id_usuario']
]);

// Guarda os dados encontrados em uma variável.
$usuario = $stmt->fetch(PDO::FETCH_ASSOC);

// Verifica se o usuário foi encontrado no banco de dados.
if (!$usuario) {

    // Se não encontrou, encerra a sessão.
    session_unset();
    session_destroy();

    // Volta para a página de login.
    header('Location: login.php');
    exit;
}

?>

<!DOCTYPE html>
<html lang="pt-BR">

<head>

    <!-- Define a codificação da página. -->
    <meta charset="UTF-8">

    <!-- Permite que a página se adapte a celulares. -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Título da página. -->
    <title>Meu Perfil - MonteMax</title>

    <!-- Bootstrap. -->
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
        rel="stylesheet"
    >

    <!-- CSS do MonteMax. -->
    <link rel="stylesheet" href="css/style.css">

    <!-- JavaScript principal do MonteMax. -->
    <script src="javaScript/javascript.js"></script>

</head>

<body>

<!-- Cabeçalho do perfil. -->
<header class="cabecalho">

    <div class="container py-3 d-flex justify-content-between align-items-center">

        <!-- Logo do MonteMax. -->
        <a href="index.php">
            <img
                src="imagens/logo-montemax.jpg"
                alt="Logo MonteMax"
                class="logo-montemax"
            >
        </a>

        <!-- Link para voltar à página inicial. -->
        <a href="index.php" class="botao-entrar">
            Início
        </a>

    </div>

</header>

<!-- Conteúdo principal do perfil. -->
<main class="perfil-container">

    <!-- Cartão que apresenta os dados do usuário. -->
    <div class="perfil-card">

        <!-- Exibe a foto do usuário quando ela estiver cadastrada. -->
        <?php if (!empty($usuario['foto_perfil'])): ?>

            <img
                src="<?= htmlspecialchars($usuario['foto_perfil']) ?>"
                alt="Foto de perfil de <?= htmlspecialchars($usuario['nome']) ?>"
                class="foto-perfil"
            >

        <?php endif; ?>

        <!-- Título da página. -->
        <h1>Meu Perfil</h1>

        <!-- Nome do usuário. -->
        <h2>
            <?= htmlspecialchars($usuario['nome']) ?>
        </h2>

        <!-- E-mail do usuário. -->
        <p>
            <?= htmlspecialchars($usuario['email']) ?>
        </p>

        <!-- Tipo da conta. -->
        <p>
            Tipo de conta:
            <?= htmlspecialchars($usuario['tipo_usuario']) ?>
        </p>

        <!-- Mostra informações adicionais somente para profissionais. -->
        <?php if ($usuario['tipo_usuario'] === 'profissional'): ?>

            <!-- WhatsApp do profissional. -->
            <p>
                WhatsApp:
                <?= htmlspecialchars($usuario['whatsapp'] ?? '') ?>
            </p>

            <!-- Descrição dos serviços oferecidos pelo profissional. -->
            <p>
                Serviços:
                <?= htmlspecialchars($usuario['descricao'] ?? '') ?>
            </p>

            <!-- Cidade onde o profissional atende. -->
            <p>
                Cidade:
                <?= htmlspecialchars($usuario['cidade'] ?? '') ?>
            </p>

        <?php endif; ?>

        <!-- Área dos botões de navegação. -->
        <div class="botoes-perfil">

            <!-- Botão para editar os dados da conta. -->
            <a href="editarPerfil.php" class="botao-cadastro">
                Editar perfil
            </a>

            <!-- Botão para acessar a exclusão definitiva da conta. -->
            <a href="excluirConta.php" class="btn btn-danger">
                Excluir minha conta
            </a>

            <!-- Botão para sair da conta. -->
            <a href="logout.php" class="botao-cadastro">
                Sair
            </a>

        </div>

    </div>

</main>

<!-- Rodapé do MonteMax. -->
<footer class="rodape">

    <p>© 2026 MonteMax. Todos os direitos reservados.</p>

</footer>

</body>

</html>