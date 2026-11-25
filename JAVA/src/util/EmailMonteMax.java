package util;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailMonteMax {

    private static final String EMAIL_REMETENTE = "montemaxadmin@gmail.com";
    private static final String HOST = "smtp.gmail.com";
    private static final String PORTA = "587";
    
   
    private static Properties configurarSMTP() {
    Properties propriedades = new Properties();
    propriedades.put("mail.smtp.auth", "true");
    propriedades.put("mail.smtp.starttls.enable", "true");
    propriedades.put("mail.smtp.host", HOST);
    propriedades.put("mail.smtp.port", PORTA);

    return propriedades;
    
}
    
    private static String obterSenhaAplicativo() {

      Properties configuracao = new Properties();

      try (java.io.FileInputStream arquivo =
              new java.io.FileInputStream(
                  System.getProperty("user.home") + "/montemax-email.properties")) {

          configuracao.load(arquivo);

          String senha = configuracao.getProperty("email.senha");

          if (senha == null || senha.isBlank()) {
              throw new IllegalStateException("Senha de aplicativo não encontrada.");
          }

          return senha.trim();

      } catch (java.io.IOException e) {

          throw new RuntimeException("Não foi possível localizar o arquivo de configuração.", e);
      }
    
}
     /*/criarSessao(): prepara a conexão com o Gmail.
    Session.getInstance(): cria a sessão de comunicação
    Authenticator: realiza a autenticação
    PasswordAuthentication: fornece o e-mail e a senha de aplicativo.*/
     private static Session criarSessao() {

    Properties propriedades = configurarSMTP();

    return Session.getInstance(propriedades, new Authenticator() {

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {

            return new PasswordAuthentication(
                EMAIL_REMETENTE,
                obterSenhaAplicativo()
            );
        }
    });
}
     
     public static void enviarEmail(String destinatario, String assunto, String mensagem) {

    try {

        Session sessao = criarSessao();

        MimeMessage email = new MimeMessage(sessao);

        email.setFrom(new InternetAddress(EMAIL_REMETENTE));

        email.setRecipients(
            Message.RecipientType.TO,
            InternetAddress.parse(destinatario)
        );

        email.setSubject(assunto);

        email.setText(mensagem, "UTF-8");

        Transport.send(email);

    } catch (Exception e) {

        throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage(), e);

    }
}
     
    public static boolean testarConfiguracao() {
    try {
        String senha = obterSenhaAplicativo();
        return senha != null && !senha.isBlank();

    } catch (Exception e) {
        System.out.println("Erro na configuração: " + e.getMessage());
        return false;
    }
}
    public static void main(String[] args) {

    try {

        enviarEmail(
            "max.williams201@gmail.com",
            "Teste de envio - MonteMax",
            "Olá! Este é um e-mail de teste enviado pelo sistema MonteMax através do Java."
        );

        System.out.println("E-mail enviado com sucesso!");

    } catch (Exception e) {

        System.out.println("Falha no envio: " + e.getMessage());
        e.printStackTrace();

    }

}
}