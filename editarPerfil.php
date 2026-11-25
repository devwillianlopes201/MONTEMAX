<?php

declare(strict_types=1);

// Inicia a sessão.
session_start();

// Verifica se o usuário está logado.
if (!isset($_SESSION['id_usuario'])) {
    header('Location: login.php');
    exit;
}

// Carrega as configurações do banco de dados.
$config = require_once __DIR__ . '/config/conexao.php';

// Cria a conexão com o banco.
$pdo = new PDO(
    "mysql:host={$config['host']};port={$config['port']};dbname={$config['name']};charset=utf8mb4",
    $config['user'],
    $config['pass']
);

// Define o PDO para apresentar erros como exceções.
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

// Inicializa as mensagens.
$mensagemErro = '';
$mensagemSucesso = '';

// Gera o token de segurança do formulário.
if (empty($_SESSION['token_csrf'])) {
    $_SESSION['token_csrf'] = bin2hex(random_bytes(32));
}

// Busca os dados atuais do usuário.
$sql = "SELECT
            u.id_usuario,
            u.nome,
            u.email,
            u.tipo_usuario,
            p.whatsapp,
            p.descricao,
            p.cidade
        FROM usuario u
        LEFT JOIN profissional p
            ON p.id_usuario = u.id_usuario
        WHERE u.id_usuario = :id_usuario";

$stmt = $pdo->prepare($sql);

$stmt->execute([
    ':id_usuario' => $_SESSION['id_usuario']
]);

$usuario = $stmt->fetch(PDO::FETCH_ASSOC);

// Verifica se a conta existe.
if (!$usuario) {
    session_unset();
    session_destroy();

    header('Location: login.php');
    exit;
}

// Inicializa os campos com os dados atuais.
$nome = $usuario['nome'];
$email = $usuario['email'];
$whatsapp = $usuario['whatsapp'] ?? '';
$descricao = $usuario['descricao'] ?? '';
$cidade = $usuario['cidade'] ?? '';

// Verifica se o formulário foi enviado.
if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    // Recebe e trata os dados enviados.
    $nome = trim($_POST['nome'] ?? '');
    $email = trim($_POST['email'] ?? '');
    $whatsapp = trim($_POST['whatsapp'] ?? '');
    $descricao = trim($_POST['descricao'] ?? '');
    $cidade = trim($_POST['cidade'] ?? '');

    // Verifica o token de segurança.
    $token = $_POST['token_csrf'] ?? '';

    if (
        !is_string($token) ||
        !hash_equals($_SESSION['token_csrf'], $token)
    ) {
        $mensagemErro = 'Formulário inválido. Atualize a página e tente novamente.';
    }

    // Verifica os campos obrigatórios.
    elseif ($nome === '' || $email === '') {
        $mensagemErro = 'Preencha o nome e o e-mail.';
    }

    // Verifica se o e-mail é válido.
    elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $mensagemErro = 'Informe um e-mail válido.';
    }

    // Verifica os campos específicos do profissional.
    elseif (
        $usuario['tipo_usuario'] === 'profissional' &&
        ($whatsapp === '' || $descricao === '' || $cidade === '')
    ) {
        $mensagemErro = 'Preencha todos os campos do profissional.';
    }

    // Verifica se a descrição corresponde a uma opção permitida.
    elseif (
        $usuario['tipo_usuario'] === 'profissional' &&
        !in_array($descricao, ['Quarto', 'Cozinha'], true)
    ) {
        $mensagemErro = 'Selecione um serviço válido.';
    }

    else {

        try {

            // Verifica se o e-mail pertence a outra conta.
            $sqlEmail = "SELECT id_usuario
                         FROM usuario
                         WHERE email = :email
                         AND id_usuario <> :id_usuario";

            $stmtEmail = $pdo->prepare($sqlEmail);

            $stmtEmail->execute([
                ':email' => $email,
                ':id_usuario' => $_SESSION['id_usuario']
            ]);

            if ($stmtEmail->fetch()) {

                $mensagemErro = 'Este e-mail já está cadastrado em outra conta.';

            } else {

                // Inicia uma transação.
                $pdo->beginTransaction();

                // Atualiza os dados da tabela usuario.
                $sqlUsuario = "UPDATE usuario
                               SET nome = :nome,
                                   email = :email
                               WHERE id_usuario = :id_usuario";

                $stmtUsuario = $pdo->prepare($sqlUsuario);

                $stmtUsuario->execute([
                    ':nome' => $nome,
                    ':email' => $email,
                    ':id_usuario' => $_SESSION['id_usuario']
                ]);

                // Atualiza os dados específicos do profissional.
                if ($usuario['tipo_usuario'] === 'profissional') {

                    $sqlProfissional = "UPDATE profissional
                                        SET whatsapp = :whatsapp,
                                            descricao = :descricao,
                                            cidade = :cidade
                                        WHERE id_usuario = :id_usuario";

                    $stmtProfissional = $pdo->prepare($sqlProfissional);

                    $stmtProfissional->execute([
                        ':whatsapp' => $whatsapp,
                        ':descricao' => $descricao,
                        ':cidade' => $cidade,
                        ':id_usuario' => $_SESSION['id_usuario']
                    ]);
                }

                // Confirma as alterações.
                $pdo->commit();

                $mensagemSucesso = 'Perfil atualizado com sucesso!';

                // Atualiza os dados exibidos na página.
                $usuario['nome'] = $nome;
                $usuario['email'] = $email;
                $usuario['whatsapp'] = $whatsapp;
                $usuario['descricao'] = $descricao;
                $usuario['cidade'] = $cidade;
            }

        } catch (PDOException $e) {

            // Desfaz as alterações em caso de erro.
            if ($pdo->inTransaction()) {
                $pdo->rollBack();
            }

            error_log($e->getMessage());

            $mensagemErro = 'Não foi possível atualizar o perfil. Tente novamente.';
        }
    }
}

?>

<!DOCTYPE html>
<html lang="pt-BR">

<head>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Editar Perfil - MonteMax</title>

    <!-- Bootstrap. -->
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
        rel="stylesheet"
    >

    <!-- CSS do MonteMax. -->
    <link rel="stylesheet" href="css/style.css">

</head>

<body>

<!-- Cabeçalho da página. -->
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

        <!-- Link para voltar ao perfil. -->
        <a href="perfil.php" class="botao-entrar">
            Meu perfil
        </a>

    </div>

</header>

<!-- Conteúdo principal. -->
<main class="pagina-cadastro">

    <div class="card-cadastro">

        <h1>Editar Perfil</h1>

        <p>Atualize suas informações no MonteMax.</p>

        <!-- Mensagem de erro. -->
        <?php if ($mensagemErro !== ''): ?>

            <div class="alert alert-danger" role="alert">
                <?= htmlspecialchars($mensagemErro) ?>
            </div>

        <?php endif; ?>

        <!-- Mensagem de sucesso. -->
        <?php if ($mensagemSucesso !== ''): ?>

            <div class="alert alert-success" role="alert">
                <?= htmlspecialchars($mensagemSucesso) ?>
            </div>

        <?php endif; ?>

        <!-- Formulário de edição. -->
        <form method="POST" action="editarPerfil.php">

            <!-- Token de segurança. -->
            <input
                type="hidden"
                name="token_csrf"
                value="<?= htmlspecialchars($_SESSION['token_csrf']) ?>"
            >

            <!-- Nome. -->
            <label for="nome">Nome completo:</label>

            <input
                type="text"
                id="nome"
                name="nome"
                value="<?= htmlspecialchars($nome) ?>"
                maxlength="100"
                required
            >

            <!-- E-mail. -->
            <label for="email">E-mail:</label>

            <input
                type="email"
                id="email"
                name="email"
                value="<?= htmlspecialchars($email) ?>"
                maxlength="150"
                required
            >

            <!-- Campos exclusivos do profissional. -->
            <?php if ($usuario['tipo_usuario'] === 'profissional'): ?>

                <hr>

                <h2>Dados profissionais</h2>

                <!-- WhatsApp. -->
                <label for="whatsapp">WhatsApp:</label>

                <input
                    type="text"
                    id="whatsapp"
                    name="whatsapp"
                    value="<?= htmlspecialchars($whatsapp) ?>"
                    maxlength="20"
                    required
                >

                <!-- Serviços oferecidos. -->
                <label for="descricao">Serviço oferecido:</label>

                <select id="descricao" name="descricao" required>

                    <option value="">Selecione</option>

                    <option
                        value="Quarto"
                        <?= $descricao === 'Quarto' ? 'selected' : '' ?>
                    >
                        Quarto
                    </option>

                    <option
                        value="Cozinha"
                        <?= $descricao === 'Cozinha' ? 'selected' : '' ?>
                    >
                        Cozinha
                    </option>

                </select>

                <!-- Cidade. -->
                <label for="cidade">Cidade:</label>

                <input
                    type="text"
                    id="cidade"
                    name="cidade"
                    value="<?= htmlspecialchars($cidade) ?>"
                    maxlength="100"
                    required
                >

            <?php endif; ?>

            <!-- Botão para salvar. -->
            <button type="submit" class="botao-cadastro-form">
                Salvar alterações
            </button>

            <!-- Voltar sem alterar. -->
            <a href="perfil.php" class="voltar-login">
                Cancelar
            </a>

            <!-- Voltar para a página inicial. -->
            <a href="index.php" class="botao-voltar-inicio">
                Voltar para a página inicial
            </a>

        </form>

    </div>

</main>

<!-- Rodapé. -->
<footer class="rodape">

    <p>© 2026 MonteMax. Todos os direitos reservados.</p>

</footer>

</body>

</html>
