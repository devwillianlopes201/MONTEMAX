<?php

declare(strict_types=1);

// Inicia a sessão do usuário.
session_start();

// Verifica se existe um usuário logado.
if (!isset($_SESSION['id_usuario'])) {
    header('Location: login.php');
    exit;
}

// Carrega as configurações do banco.
$config = require_once __DIR__ . '/config/conexao.php';

// Cria um token para proteger o formulário contra CSRF.
if (empty($_SESSION['token_csrf'])) {
    $_SESSION['token_csrf'] = bin2hex(random_bytes(32));
}

// Inicializa a mensagem de erro.
$erro = '';

// Verifica se o formulário foi enviado.
if ($_SERVER['REQUEST_METHOD'] === 'POST') {

    $senha = $_POST['senha'] ?? '';
    $token = $_POST['token_csrf'] ?? '';
    $confirmacao = $_POST['confirmacao'] ?? '';

    // Confere o token de segurança.
    if (
        !is_string($token) ||
        !hash_equals($_SESSION['token_csrf'], $token)
    ) {
        $erro = 'Solicitação inválida. Atualize a página e tente novamente.';

    } elseif ($confirmacao !== 'SIM') {

        $erro = 'Confirme que deseja excluir definitivamente sua conta.';

    } elseif (!is_string($senha) || $senha === '') {

        $erro = 'Digite sua senha atual para continuar.';

    } else {

        try {

            // Cria a conexão com o banco.
            $pdo = new PDO(
                "mysql:host={$config['host']};port={$config['port']};dbname={$config['name']};charset=utf8mb4",
                $config['user'],
                $config['pass']
            );

            $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

            // Busca a senha e o tipo da conta do usuário logado.
            $stmt = $pdo->prepare(
                "SELECT senha, tipo_usuario
                 FROM usuario
                 WHERE id_usuario = :id_usuario"
            );

            $stmt->execute([
                ':id_usuario' => $_SESSION['id_usuario']
            ]);

            $usuario = $stmt->fetch(PDO::FETCH_ASSOC);

            // Confere se a conta existe e se a senha está correta.
            if (!$usuario || !password_verify($senha, $usuario['senha'])) {

                $erro = 'Senha incorreta. A conta não foi excluída.';

            } else {

                $idUsuario = (int) $_SESSION['id_usuario'];

                // Busca o ID profissional, caso a conta seja profissional.
                $idProfissional = null;

                if ($usuario['tipo_usuario'] === 'profissional') {

                    $stmt = $pdo->prepare(
                        "SELECT id_profissional
                         FROM profissional
                         WHERE id_usuario = :id_usuario"
                    );

                    $stmt->execute([
                        ':id_usuario' => $idUsuario
                    ]);

                    $resultado = $stmt->fetch(PDO::FETCH_ASSOC);

                    if ($resultado) {
                        $idProfissional = (int) $resultado['id_profissional'];
                    }
                }

                // Inicia a transação para garantir a exclusão completa.
                $pdo->beginTransaction();

                // Remove os orçamentos das solicitações relacionadas à conta.
                $sql = "
                    DELETE FROM orcamento
                    WHERE id_solicitacao IN (
                        SELECT idsolicitacao_servico
                        FROM solicitacao_servico
                        WHERE id_usuario = :id_usuario
                        OR (
                            :id_profissional IS NOT NULL
                            AND id_profissional = :id_profissional
                        )
                    )
                ";

                $stmt = $pdo->prepare($sql);
                $stmt->execute([
                    ':id_usuario' => $idUsuario,
                    ':id_profissional' => $idProfissional,
                ]);

                // Remove as solicitações feitas ou recebidas pelo usuário.
                $sql = "
                    DELETE FROM solicitacao_servico
                    WHERE id_usuario = :id_usuario
                    OR (
                        :id_profissional IS NOT NULL
                        AND id_profissional = :id_profissional
                    )
                ";

                $stmt = $pdo->prepare($sql);
                $stmt->execute([
                    ':id_usuario' => $idUsuario,
                    ':id_profissional' => $idProfissional,
                ]);

                // Remove avaliações escritas pelo usuário
                // ou recebidas pelo perfil profissional.
                $sql = "
                    DELETE FROM avaliacao
                    WHERE id_usuario = :id_usuario
                    OR (
                        :id_profissional IS NOT NULL
                        AND id_profissional = :id_profissional
                    )
                ";

                $stmt = $pdo->prepare($sql);
                $stmt->execute([
                    ':id_usuario' => $idUsuario,
                    ':id_profissional' => $idProfissional,
                ]);

                // Remove comentários escritos pelo usuário.
                $stmt = $pdo->prepare(
                    "DELETE FROM comentario
                     WHERE id_usuario = :id_usuario"
                );

                $stmt->execute([
                    ':id_usuario' => $idUsuario
                ]);

                // Remove as fotos das publicações do usuário.
                $stmt = $pdo->prepare(
                    "DELETE FROM foto_publicacao
                     WHERE id_publicacao IN (
                         SELECT id_publicacao
                         FROM publicacao
                         WHERE id_usuario = :id_usuario
                     )"
                );

                $stmt->execute([
                    ':id_usuario' => $idUsuario
                ]);

                // Remove comentários feitos por outras pessoas
                // nas publicações do usuário.
                $stmt = $pdo->prepare(
                    "DELETE FROM comentario
                     WHERE id_publicacao IN (
                         SELECT id_publicacao
                         FROM publicacao
                         WHERE id_usuario = :id_usuario
                     )"
                );

                $stmt->execute([
                    ':id_usuario' => $idUsuario
                ]);

                // Remove as publicações do usuário.
                $stmt = $pdo->prepare(
                    "DELETE FROM publicacao
                     WHERE id_usuario = :id_usuario"
                );

                $stmt->execute([
                    ':id_usuario' => $idUsuario
                ]);

                // Remove as etapas dos tutoriais do usuário.
                $stmt = $pdo->prepare(
                    "DELETE FROM etapa_tutorial
                     WHERE id_tutorial IN (
                         SELECT id_tutorial
                         FROM tutorial
                         WHERE id_usuario = :id_usuario
                     )"
                );

                $stmt->execute([
                    ':id_usuario' => $idUsuario
                ]);

                // Remove os tutoriais do usuário.
                $stmt = $pdo->prepare(
                    "DELETE FROM tutorial
                     WHERE id_usuario = :id_usuario"
                );

                $stmt->execute([
                    ':id_usuario' => $idUsuario
                ]);

                // Remove os vínculos entre profissional e serviços.
                if ($idProfissional !== null) {

                    $stmt = $pdo->prepare(
                        "DELETE FROM profissional_servico
                         WHERE id_profissional = :id_profissional"
                    );

                    $stmt->execute([
                        ':id_profissional' => $idProfissional
                    ]);
                }

                // Remove o registro de administrador, caso exista.
                $stmt = $pdo->prepare(
                    "DELETE FROM administrador
                     WHERE id_usuario = :id_usuario"
                );

                $stmt->execute([
                    ':id_usuario' => $idUsuario
                ]);

                // Remove o cadastro profissional, se existir.
                $stmt = $pdo->prepare(
                    "DELETE FROM profissional
                     WHERE id_usuario = :id_usuario"
                );

                $stmt->execute([
                    ':id_usuario' => $idUsuario
                ]);

                // Por último, remove a conta principal.
                $stmt = $pdo->prepare(
                    "DELETE FROM usuario
                     WHERE id_usuario = :id_usuario"
                );

                $stmt->execute([
                    ':id_usuario' => $idUsuario
                ]);

                // Confirma todas as exclusões.
                $pdo->commit();

                // Encerra a sessão após a exclusão.
                session_unset();
                session_destroy();

                // Redireciona para a página inicial.
                header('Location: index.php');
                exit;
            }

        } catch (PDOException $e) {

            // Desfaz as exclusões caso aconteça algum erro.
            if (isset($pdo) && $pdo->inTransaction()) {
                $pdo->rollBack();
            }

            // Registra o erro para análise no ambiente de desenvolvimento.
            error_log($e->getMessage());

            $erro = 'Não foi possível excluir a conta. Nenhum dado foi removido parcialmente.';
        }
    }
}

?>

<!DOCTYPE html>
<html lang="pt-BR">

<head>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Excluir conta - MonteMax</title>

    <!-- Bootstrap. -->
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
        rel="stylesheet"
    >

    <!-- CSS do MonteMax. -->
    <link rel="stylesheet" href="css/style.css">

</head>

<body>

    <main class="pagina-cadastro">

        <div class="logo-cadastro">

            <a href="perfil.php">

                <img
                    src="imagens/logo-montemax.jpg"
                    alt="Logo MonteMax"
                >

            </a>

        </div>

        <div class="card-cadastro">

            <h1>Excluir minha conta</h1>

            <p>
                Esta ação é permanente e não poderá ser desfeita.
            </p>

            <p>
                Seus dados e conteúdos vinculados à conta serão excluídos
                do MonteMax.
            </p>

            <!-- Mensagem de erro. -->
            <?php if ($erro !== ''): ?>

                <div class="mensagem-erro">
                    <?= htmlspecialchars($erro) ?>
                </div>

            <?php endif; ?>

            <!-- Formulário de exclusão. -->
            <form method="POST">

                <!-- Token de segurança. -->
                <input
                    type="hidden"
                    name="token_csrf"
                    value="<?= htmlspecialchars($_SESSION['token_csrf']) ?>"
                >

                <!-- Senha atual. -->
                <label for="senha">
                    Digite sua senha atual:
                </label>

                <input
                    type="password"
                    id="senha"
                    name="senha"
                    placeholder="Sua senha"
                    required
                >

                <!-- Confirmação da exclusão. -->
                <div class="form-check my-3">

                    <input
                        class="form-check-input"
                        type="checkbox"
                        id="confirmacao"
                        name="confirmacao"
                        value="SIM"
                        required
                    >

                    <label class="form-check-label" for="confirmacao">
                        Entendo que minha conta será excluída definitivamente.
                    </label>

                </div>

                <!-- Botão para excluir a conta. -->
                <button
                    type="submit"
                    class="botao-cadastro-form"
                    onclick="return confirm('Tem certeza de que deseja excluir definitivamente sua conta?');"
                >
                    Excluir minha conta definitivamente
                </button>

            </form>

            <!-- Cancelar e voltar ao perfil. -->
            <a href="perfil.php" class="voltar-login">
                Cancelar e voltar ao perfil
            </a>

            <!-- Voltar para a página inicial. -->
            <a href="index.php" class="botao-voltar-inicio">
                ← Voltar para a página inicial
            </a>

        </div>

    </main>

</body>

</html>