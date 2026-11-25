<?php

declare(strict_types=1);

// Inicia a sessão.
session_start();

// Carrega as configurações do banco de dados.
$config = require_once __DIR__ . '/config/conexao.php';

// Inicializa os campos.
$nome = '';
$email = '';
$tipoUsuario = 'usuario';
$whatsapp = '';
$descricao = '';
$cidade = '';

$mensagemErro = '';
$mensagemSucesso = '';

// Verifica se o formulário foi enviado.
if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    // Recebe e trata os dados.
    $nome = trim($_POST['nome'] ?? '');
    $email = trim($_POST['email'] ?? '');
    $tipoUsuario = $_POST['tipo_usuario'] ?? 'usuario';
    $senha = $_POST['senha'] ?? '';
    $confirmarSenha = $_POST['confirmar_senha'] ?? '';

    $whatsapp = trim($_POST['whatsapp'] ?? '');
    $descricao = trim($_POST['descricao'] ?? '');
    $cidade = trim($_POST['cidade'] ?? '');
    $rgFrente = $_FILES['rg_frente'] ?? null; //
    $rgVerso = $_FILES['rg_verso'] ?? null;  //

    // Verifica se o tipo de usuário é permitido.
    if (!in_array($tipoUsuario, ['usuario', 'profissional'], true)) {

        $mensagemErro = 'Selecione um tipo de conta válido.';

    }

    // Verifica os campos obrigatórios.
    elseif ($nome === '' || $email === '' || $senha === '' || $confirmarSenha === '') {

        $mensagemErro = 'Preencha todos os campos obrigatórios.';

    }

    // Verifica o tamanho do nome.
    elseif (strlen($nome) > 100) {

        $mensagemErro = 'O nome deve ter no máximo 100 caracteres.';

    }

    // Verifica o formato do e-mail.
    elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {

        $mensagemErro = 'Informe um e-mail válido.';

    }

    // Verifica se as senhas são iguais.
    elseif ($senha !== $confirmarSenha) {

        $mensagemErro = 'As senhas não coincidem.';

    }

    // Verifica os campos obrigatórios do profissional.
    elseif (
        $tipoUsuario === 'profissional' &&
        ($whatsapp === '' || $descricao === '' || $cidade === '')
    ) {

        $mensagemErro = 'Preencha todos os dados profissionais obrigatórios.';

    }
    // Verifica se os documentos RG foram enviados.
    
    elseif (
        $tipoUsuario === 'profissional' &&
        (
            !$rgFrente ||
            !$rgVerso ||
            $rgFrente['error'] !== UPLOAD_ERR_OK ||
            $rgVerso['error'] !== UPLOAD_ERR_OK
        )
    ) {

    $mensagemErro = 'Envie a imagem da frente e do verso do RG.';
    }
        // Verifica o formato e o tamanho das imagens do RG.
    elseif ($tipoUsuario === 'profissional') {

    $tiposPermitidos = ['image/jpeg', 'image/png', 'image/webp'];

    $finfo = new finfo(FILEINFO_MIME_TYPE);

    $mimeFrente = $finfo->file($rgFrente['tmp_name']);
    $mimeVerso = $finfo->file($rgVerso['tmp_name']);

    $tamanhoMaximo = 5 * 1024 * 1024; // 5 MB

    if (
        !in_array($mimeFrente, $tiposPermitidos, true) ||
        !in_array($mimeVerso, $tiposPermitidos, true)
    ) {
        $mensagemErro = 'Envie imagens JPG, PNG ou WEBP.';
    }
    elseif (
        $rgFrente['size'] > $tamanhoMaximo ||
        $rgVerso['size'] > $tamanhoMaximo
    ) {
        $mensagemErro = 'Cada imagem deve ter no máximo 5 MB.';
    }
    }

    // Verifica se o serviço selecionado é válido.
    elseif (
        $tipoUsuario === 'profissional' &&
        !in_array($descricao, ['Quarto', 'Cozinha'], true)
    ) {

        $mensagemErro = 'Selecione um serviço válido.';

    }

    else {

        try {

            // Cria a conexão com o banco.
            $pdo = new PDO(
                "mysql:host={$config['host']};port={$config['port']};dbname={$config['name']};charset=utf8mb4",
                $config['user'],
                $config['pass']
            );

            // Configura o PDO.
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            // Verifica se o e-mail já está cadastrado.
            $sqlEmail = "SELECT id_usuario
                         FROM usuario
                         WHERE email = :email";

            $stmtEmail = $pdo->prepare($sqlEmail);

            $stmtEmail->execute([
                ':email' => $email
            ]);

            if ($stmtEmail->fetch()) {

                $mensagemErro = 'Este e-mail já está cadastrado.';

            } else {

                // Criptografa a senha.
                $senhaHash = password_hash($senha, PASSWORD_DEFAULT);

                // Inicia uma transação.
                $pdo->beginTransaction();

                // Pasta de armazenamento dos documentos RG Que esta Dentro Do montemax.
                $pastaDocumentos = __DIR__ . '/uploads/documentos_rg/';

            

                // Cadastra o usuário.
                $sqlUsuario = "INSERT INTO usuario
                               (nome, email, senha, tipo_usuario)
                               VALUES
                               (:nome, :email, :senha, :tipo_usuario)";

                $stmtUsuario = $pdo->prepare($sqlUsuario);

                $stmtUsuario->execute([
                    ':nome' => $nome,
                    ':email' => $email,
                    ':senha' => $senhaHash,
                    ':tipo_usuario' => $tipoUsuario
                ]);

                // Recupera o ID do usuário cadastrado.
                $idUsuario = $pdo->lastInsertId();

                // Se for profissional, cadastra os dados profissionais.
                if ($tipoUsuario === 'profissional') {

                    $sqlProfissional = "INSERT INTO profissional 
                                    (id_usuario, whatsapp, descricao, cidade, rg_frente, rg_verso, data_cadastro)
                                    VALUES
                                    (:id_usuario, :whatsapp, :descricao, :cidade, :rg_frente, :rg_verso, NOW())";

                    $stmtProfissional = $pdo->prepare($sqlProfissional);

                            // coloca a extensao correta de cada arquivo e armazena de maneira correta 
                    $extensoes = [
                    'image/jpeg' => 'jpg',
                    'image/png' => 'png',
                    'image/webp' => 'webp'
                ];

                    $extensaoFrente = $extensoes[$mimeFrente];
                    $extensaoVerso = $extensoes[$mimeVerso];

                    $stmtProfissional->execute([
                        ':id_usuario' => $idUsuario,
                        ':whatsapp' => $whatsapp,
                        ':descricao' => $descricao,
                        ':cidade' => $cidade,
                        ':rg_frente' => 'uploads/documentos_rg/' . $nomeRgFrente, // guardando caminho dos documentos
                        ':rg_verso' => 'uploads/documentos_rg/' . $nomeRgVerso,
                    ]);
            

                    $nomeRgFrente = uniqid('rg_frente_', true) . '.' . $extensaoFrente;
                    $nomeRgVerso = uniqid('rg_verso_', true) . '.' . $extensaoVerso;
                
                // Move os documentos para a pasta de armazenamento.
                $caminhoFrente = $pastaDocumentos . $nomeRgFrente;
                $caminhoVerso = $pastaDocumentos . $nomeRgVerso;

                if (
                    !move_uploaded_file($rgFrente['tmp_name'], $caminhoFrente) ||
                    !move_uploaded_file($rgVerso['tmp_name'], $caminhoVerso)
                ) {
                    throw new RuntimeException('Não foi possível salvar os documentos RG.');
                }
                }
                // Confirma o cadastro.
                $pdo->commit();

                if ($tipoUsuario === 'profissional') {
                    $mensagemSucesso = 'Cadastro realizado! Seus documentos foram enviados e sua conta está aguardando aprovação do administrador.';
                } 
                else {
                    $mensagemSucesso = 'Cadastro realizado com sucesso! Você já pode fazer login.';
                }

                // Limpa os campos após o cadastro.
                $nome = '';
                $email = '';
                $tipoUsuario = 'usuario';
                $whatsapp = '';
                $descricao = '';
                $cidade = '';
            }

        } catch (PDOException $e) {

            // Desfaz a transação em caso de erro.
            if (isset($pdo) && $pdo->inTransaction()) {
                $pdo->rollBack();
            }

            error_log($e->getMessage());

            $mensagemErro = 'Não foi possível realizar o cadastro. Tente novamente.';
        }
    }
}

?>

<!DOCTYPE html>
<html lang="pt-BR">

<head>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Cadastro - MonteMax</title>

    <!-- Bootstrap. -->
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
        rel="stylesheet"
    >

    <!-- CSS do MonteMax. -->
    <link rel="stylesheet" href="css/style.css">
     <!-- botao criar minha conta  -->
    <style>
    .card-cadastro input[type="submit"] {
        background-color: #f28c28;
        color: white;
        border: none;
        cursor: pointer;
    }

    .card-cadastro input[type="submit"]:hover {
        background-color: #e57b17;
    }
</style>
</head>

<body>

<main class="pagina-cadastro">

    <div class="card-cadastro">

        <!-- Logo. -->
        <img
            src="imagens/logo-montemax.jpg"
            alt="Logo MonteMax"
            class="logo-cadastro"
        >

        <h1>Criar minha conta</h1>

        <p>Preencha seus dados para fazer parte do MonteMax.</p>

        <p>
            <small>
                Os campos marcados com <strong>*</strong> são obrigatórios.
            </small>
        </p>

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

        <!-- Formulário de cadastro. recebe rg frente e verso -->
        <form method="POST" action="cadastro.php" enctype="multipart/form-data">

            <!-- Nome. -->
            <label for="nome">
                Nome completo *
            </label>

            <input
                type="text"
                id="nome"
                name="nome"
                value="<?= htmlspecialchars($nome) ?>"
                maxlength="100"
                required
            >

            <!-- E-mail. -->
            <label for="email">
                E-mail *
            </label>

            <input
                type="email"
                id="email"
                name="email"
                value="<?= htmlspecialchars($email) ?>"
                maxlength="150"
                required
            >

            <!-- Tipo de conta. -->
            <label for="tipo_usuario">
                Tipo de conta *
            </label>

            <select
                id="tipo_usuario"
                name="tipo_usuario"
                onchange="mostrarCamposProfissional()"
                required
            >

                <option
                    value="usuario"
                    <?= $tipoUsuario === 'usuario' ? 'selected' : '' ?>
                >
                    Cliente
                </option>

                <option
                    value="profissional"
                    <?= $tipoUsuario === 'profissional' ? 'selected' : '' ?>
                >
                    Profissional
                </option>

            </select>

            <!-- Senha. -->
            <label for="senha">
                Senha *
            </label>

            <input
                type="password"
                id="senha"
                name="senha"
                required
            >

            <!-- Confirmação da senha. -->
            <label for="confirmar_senha">
                Confirmar senha *
            </label>

            <input
                type="password"
                id="confirmar_senha"
                name="confirmar_senha"
                required
            >

            <!-- Campos exclusivos do profissional. -->
            <div id="camposProfissional">

                <hr>

                <h2>Dados profissionais</h2>

                <p>
                    <small>
                        Preencha estas informações para cadastrar seu perfil profissional.
                    </small>
                </p>

                <!-- WhatsApp. -->
                <label for="whatsapp">
                    WhatsApp *
                </label>

                <input
                    type="text"
                    id="whatsapp"
                    name="whatsapp"
                    value="<?= htmlspecialchars($whatsapp) ?>"
                    maxlength="20"
                >

                <!-- Serviço oferecido. -->
                <label for="descricao">
                    Serviço oferecido *
                </label>

                <select id="descricao" name="descricao">

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
                <label for="cidade">
                    Cidade *
                </label>

                <input
                    type="text"
                    id="cidade"
                    name="cidade"
                    value="<?= htmlspecialchars($cidade) ?>"
                    maxlength="100"
                >
                 <!-- RG - Frente. -->
                <label for="rg_frente">
                    RG - Frente *
                </label>

                <input
                    type="file"
                    id="rg_frente"
                    name="rg_frente"
                    accept="image/*"
                    required
                >

                <!-- RG - Verso. -->
                <label for="rg_verso">
                    RG - Verso *
                </label>

                <input
                    type="file"
                    id="rg_verso"
                    name="rg_verso"
                    accept="image/*"
                    required
                >

                

            </div>

            <!-- Botão de cadastro. -->
            <input type="submit" value="Criar minha conta">
        
            </button>

        </form>

        <!-- Link para login. -->
        <a href="login.php" class="voltar-login">
            Já tenho uma conta. Entrar
        </a>

        <!-- Voltar para a página inicial. -->
        <a href="index.php" class="botao-voltar-inicio">
            Voltar para a página inicial
        </a>

    </div>

</main>

<script>

// Mostra ou esconde os campos profissionais.
function mostrarCamposProfissional() {

    const tipoUsuario = document.getElementById('tipo_usuario').value;

    const campos = document.getElementById('camposProfissional');

    const whatsapp = document.getElementById('whatsapp');
    const descricao = document.getElementById('descricao');
    const cidade = document.getElementById('cidade');
    const rgFrente = document.getElementById('rg_frente');
    const rgVerso = document.getElementById('rg_verso');

    // Verifica se a conta é profissional e obriga a introduzir o rg.
    if (tipoUsuario === 'profissional') {

        campos.style.display = 'block';

        whatsapp.required = true;
        descricao.required = true;
        cidade.required = true;
        rgFrente.required = true;
        rgVerso.required = true;

    } else {

        campos.style.display = 'none';

        whatsapp.required = false;
        descricao.required = false;
        cidade.required = false;
        rgFrente.required = false;
        rgVerso.required = false;

    }
}

// Executa a função ao carregar a página.
document.addEventListener('DOMContentLoaded', mostrarCamposProfissional);

</script>

</body>

</html>
