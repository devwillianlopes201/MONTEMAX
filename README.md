# 🪑 MonteMax — Plataforma de Montagem e Desmontagem de Móveis

O **MonteMax** é uma plataforma desenvolvida como projeto acadêmico com a proposta de conectar pessoas que precisam de serviços de montagem e desmontagem de móveis a profissionais especializados nesse segmento.

A solução reúne um sistema web, um aplicativo Android e um painel administrativo desktop, integrados a um banco de dados MySQL.

O projeto busca facilitar a contratação de profissionais, aproximar clientes e prestadores de serviços e oferecer um ambiente digital para solicitação e acompanhamento de serviços.

---

## 🎯 Objetivo do projeto

O objetivo do MonteMax é simplificar o processo de encontrar e contratar profissionais especializados em montagem e desmontagem de móveis.

A plataforma foi idealizada para atender diferentes necessidades dos usuários:

- **Montar sozinho:** acesso a conteúdos informativos e orientações para quem deseja realizar a montagem por conta própria.
- **Preciso de ajuda:** alternativa para quem encontra dificuldades durante a montagem e procura orientação.
- **Contratar um profissional:** possibilidade de solicitar um serviço e encontrar profissionais especializados.

---

## 💡 Proposta da plataforma

O MonteMax busca aproximar clientes e profissionais em um único ambiente digital.

Entre as propostas do sistema estão:

- Facilitar a busca por profissionais.
- Permitir que clientes descrevam suas necessidades.
- Possibilitar o envio de fotos para explicar o serviço solicitado.
- Aproximar clientes de profissionais da região.
- Permitir que profissionais apresentem propostas de atendimento.
- Incentivar a interação entre usuários por meio de uma comunidade.
- Disponibilizar conteúdos informativos sobre montagem de móveis.

---

## 🧩 Módulos do sistema

### 1. Sistema Web

Desenvolvido com PHP, HTML, CSS e JavaScript.

Principais áreas do sistema:

- Página inicial e apresentação do MonteMax.
- Cadastro e autenticação de usuários.
- Perfis de clientes e profissionais.
- Edição de perfil e gerenciamento de conta.
- Área de profissionais.
- Solicitação de serviços.
- Comunidade para publicações e interação.
- Conteúdos informativos sobre montagem de móveis.

### 2. Aplicativo Android

Aplicativo desenvolvido utilizando Kotlin.

O projeto contempla funcionalidades relacionadas a:

- Cadastro e login de usuários.
- Visualização e gerenciamento de perfis.
- Consulta de profissionais.
- Solicitação de serviços.
- Avaliações.
- Publicações e comunidade.
- Recursos relacionados à localização.
- Comunicação com o servidor PHP por meio de integração HTTP.

A comunicação com o servidor utiliza Retrofit e Gson.

### 3. Painel Administrativo Desktop

Desenvolvido em Java utilizando Java Swing.

Sua finalidade é permitir o gerenciamento administrativo da plataforma.

Entre as funcionalidades previstas estão:

- Acesso restrito a administradores.
- Consulta de usuários cadastrados.
- Análise dos cadastros de profissionais.
- Ativação ou desativação de contas.
- Gerenciamento da situação dos profissionais.

Uma das regras definidas no projeto é que o cadastro de clientes pode ser ativado automaticamente, enquanto os profissionais permanecem em análise até a aprovação administrativa.

### 4. Banco de Dados

O banco de dados utiliza MySQL e foi modelado para armazenar as informações necessárias ao funcionamento da plataforma.

Entre as principais entidades estão:

- `usuario`
- `profissional`
- `administrador`
- `categoria`
- `servico`
- `profissional_servico`
- `profissional_categoria`
- `solicitacao_servico`
- `avaliacao`
- `publicacao`
- `foto_publicacao`
- `comentario`

A modelagem contempla relacionamentos entre usuários, profissionais, serviços, avaliações e publicações.

Os arquivos de modelagem e estrutura SQL estão disponíveis na pasta `banco/`.

---

## 🔄 Fluxo de utilização proposto

### Cliente

1. Realiza o cadastro na plataforma.
2. Acessa as opções disponíveis no MonteMax.
3. Escolhe entre montar sozinho, buscar ajuda ou contratar um profissional.
4. Quando necessário, descreve o serviço desejado e pode enviar fotos.
5. Consulta profissionais e propostas de atendimento.
6. Pode avaliar e interagir com a comunidade.

### Profissional

1. Realiza o cadastro como profissional.
2. Aguarda a análise administrativa do cadastro.
3. Após a aprovação, pode utilizar as funcionalidades destinadas aos profissionais.
4. Consulta oportunidades de serviço.
5. Pode apresentar propostas aos clientes.
6. Mantém seu perfil e informações profissionais na plataforma.

### Administrador

1. Acessa o painel administrativo.
2. Consulta os usuários cadastrados.
3. Analisa os cadastros de profissionais.
4. Gerencia a situação das contas.

---

## 🛠️ Tecnologias utilizadas

| Tecnologia | Aplicação |
|---|---|
| PHP | Desenvolvimento web e integração com o servidor |
| HTML5 | Estrutura das páginas |
| CSS3 | Estilização e layout |
| JavaScript | Interatividade das páginas |
| Java | Painel administrativo desktop |
| Java Swing | Interface gráfica administrativa |
| Kotlin | Aplicativo Android |
| Retrofit | Comunicação HTTP |
| Gson | Conversão de dados JSON |
| MySQL | Banco de dados |
| MySQL Workbench | Modelagem e gerenciamento do banco |
| Git e GitHub | Controle de versão |

---

## 📁 Organização do repositório

- `JAVA/` — painel administrativo desktop.
- `pdm/` — projeto Android.
- `banco/` — arquivos de modelagem e estrutura do banco de dados.
- Arquivos PHP — sistema web.
- Arquivos CSS — estilização das páginas.
- Arquivos JavaScript — interatividade do sistema.

---

## ⚙️ Ambiente de desenvolvimento

**Sistema Web:**
- PHP
- Apache
- MySQL
- XAMPP

**Aplicativo Android:**
- Android Studio
- SDK Android
- Configuração do endereço do servidor PHP.

**Painel administrativo:**
- JDK
- Apache NetBeans
- MySQL Connector/J
- Biblioteca jBCrypt

**Banco de dados:**
- MySQL Server
- MySQL Workbench

### Observação

Para executar o projeto em outro computador, é necessário configurar o ambiente local, o banco de dados e o endereço do servidor utilizado na comunicação entre os módulos.

As credenciais e configurações privadas não devem ser compartilhadas no repositório público.

---

## 🚧 Status do projeto

**Em desenvolvimento.**

O MonteMax continua em evolução, com foco em:

- Melhorias no layout e na experiência visual.
- Ajustes nas funcionalidades existentes.
- Aprimoramento da integração entre os módulos.
- Revisão dos fluxos de utilização.
- Evolução dos recursos destinados a clientes e profissionais.

---

## 👨‍💻 Desenvolvedor

**Willian Lopes da Silva**

Projeto acadêmico desenvolvido durante a formação em Análise e Desenvolvimento de Sistemas.

**GitHub:** [devwillianlopes201](https://github.com/devwillianlopes201)

---

## 📌 Repositório

[https://github.com/devwillianlopes201/MONTEMAX](https://github.com/devwillianlopes201/MONTEMAX)

**MonteMax — Conectando pessoas e profissionais para facilitar a montagem e desmontagem de móveis.**