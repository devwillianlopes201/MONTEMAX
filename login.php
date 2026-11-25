<?php

declare(strict_types=1);

// Inicia a sessão para armazenar os dados do usuário após o login.
session_start();

// Carrega as configurações do banco de dados.
$config = require_once __DIR__ . '/config/conexao.php';

// Guarda o e-mail digitado para reutilizá-lo no formulário.
$email = "";

// Inicializa a mensagem de erro.
$erro = "";

// Verifica se o formulário foi enviado pelo método POST.
if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    // Recebe os dados enviados pelo formulário.
    $email = $_POST['email'] ?? '';
    $senha = $_POST['senha'] ?? '';

    // Verifica se os campos foram preenchidos.
    if ($email === '' || $senha === '') {

        // Informa que os campos são obrigatórios.
        $erro = "Preencha o e-mail e a senha.";

    // Verifica se o e-mail possui um formato válido.
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {

        // Informa que o e-mail não é válido.
        $erro = "Informe um e-mail válido.";
    }

    // Continua o login somente se não houver erro.
    if ($erro === '') {

        try {

            // Cria a conexão com o banco de dados.
            $pdo = new PDO(
                "mysql:host={$config['host']};port={$config['port']};dbname={$config['name']};charset=utf8mb4",
                $config['user'],
                $config['pass']
            );

            // Faz o PDO lançar exceções quando ocorrer algum erro.
            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            // Busca o usuário/profissional pelo e-mail informado.
                $sql = "SELECT
                    u.id_usuario,
                    u.nome,
                    u.email,
                    u.senha,
                    u.tipo_usuario,
                    p.status_cadastro
                FROM usuario u
                LEFT JOIN profissional p
                    ON u.id_usuario = p.id_usuario
                WHERE u.email = :email";

            // Prepara o comando SQL.
            $stmt = $pdo->prepare($sql);

            // Executa a busca utilizando o e-mail informado.
            $stmt->execute([
                ':email' => $email
            ]);

            // Recupera os dados encontrados.
            $usuario = $stmt->fetch(PDO::FETCH_ASSOC);

            // Verifica se o usuário existe e se a senha está correta.
            if ($usuario && password_verify($senha, $usuario['senha'])) {
                // Verifica se o profissional já foi aprovado.
            if (
                $usuario['tipo_usuario'] === 'profissional' &&
                $usuario['status_cadastro'] !== 'ATIVO'
            ) {
                $erro = 'Seu cadastro ainda está aguardando aprovação do administrador.';
            }

                // Gera um novo ID de sessão por segurança.
                session_regenerate_id(true);

                // Guarda o ID do usuário na sessão.
                $_SESSION['id_usuario'] = $usuario['id_usuario'];

                // Guarda o nome do usuário na sessão.
                $_SESSION['nome'] = $usuario['nome'];

                // Guarda o tipo de usuário na sessão.
                $_SESSION['tipo_usuario'] = $usuario['tipo_usuario'];

                // Redireciona o usuário para a página inicial.
                header('Location: index.php');
                exit;

            } else {

                // Informa que os dados de acesso estão incorretos.
                $erro = "E-mail ou senha incorretos.";
            }

        } catch (PDOException $e) {

            // Mensagem apresentada caso ocorra um problema com o banco.
            $erro = "Não foi possível realizar o login.";
        }
    }
}

?>

<!DOCTYPE html>
<html lang="pt-BR">

<head>

    <!-- Define a codificação da página. -->
    <meta charset="UTF-8">

    <!-- Permite que a página seja adaptada para celulares. -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Define o título da página. -->
    <title>Entrar - MonteMax</title>

    <!-- Bootstrap para ajudar na responsividade. -->
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
        rel="stylesheet"
    >

    <!-- CSS principal do MonteMax. -->
    <link rel="stylesheet" href="css/style.css">

    <!-- JavaScript principal do MonteMax. -->
    <script src="javaScript/javascript.js"></script>


</head>

<body>

    <!-- Área principal da página de login. -->
    <main class="pagina-cadastro">

        <!-- Logo do MonteMax. -->
        <div class="logo-cadastro">

            <a href="index.php">

                <img
                    src="imagens/logo-montemax.jpg"
                    alt="Logo MonteMax"
                >

            </a>

        </div>

        <!-- Card do formulário de login. -->
        <div class="card-cadastro">

            <!-- Título principal. -->
            <h1>Entrar</h1>

            <!-- Mostra a mensagem de erro quando existir. -->
            <?php if ($erro !== ''): ?>

                <div class="mensagem-erro">

                    <?= htmlspecialchars($erro) ?>

                </div>

            <?php endif; ?>

            <!-- Formulário de login. -->
            <form method="POST">

                <!-- Campo para o e-mail. -->
                <label for="email">E-mail:</label>

                <input
                    type="email"
                    id="email"
                    name="email"
                    value="<?= htmlspecialchars($email) ?>"
                    placeholder="Digite seu e-mail"
                    required
                >

                <!-- Campo para a senha. -->
                <label for="senha">Senha:</label>

                <input
                    type="password"
                    id="senha"
                    name="senha"
                    placeholder="Digite sua senha"
                    required
                >

                <!-- Botão para entrar. -->
                <button
                    type="submit"
                    class="botao-cadastro-form"
                >
                    Entrar
                </button>

            </form>

            <!-- Link para cadastro de usuário. -->
            <a
                href="cadastroUsuario.php"
                class="voltar-login"
            >
                Ainda não possui uma conta? Cadastre-se
            </a>

        </div>

    </main>

</body>

</html>