<?php

declare(strict_types=1);

// Inicia a sessão para poder encerrar os dados do usuário logado.
session_start();

// Remove todos os dados armazenados na sessão.
session_unset();

// Encerra a sessão atual.
session_destroy();

// Volta para a página inicial do MonteMax.
header('Location: index.php');
exit;

?>