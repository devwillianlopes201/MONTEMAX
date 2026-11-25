
package view;
import connection.Conexao;
import java.sql.*;
import javax.swing.JOptionPane;
public class JFrmTelaPrincipal extends javax.swing.JFrame {
   
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JFrmTelaPrincipal.class.getName());
    //construtor chamando os métodos que foram criados no final da tela 
    public JFrmTelaPrincipal() {
    initComponents();

    carregarTotalClientes();
    carregarTotalProfissionais();
    carregarTotalProfissionaisAtivos();
    carregarTotalAguardandoAprovacao();
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PainelMenu = new javax.swing.JPanel();
        jLbLogo = new javax.swing.JLabel();
        jLbPainelAdmin = new javax.swing.JLabel();
        PailnelConteudo = new javax.swing.JPanel();
        jLbTituloPagina = new javax.swing.JLabel();
        jLbDescricaoPagina = new javax.swing.JLabel();
        jPnlCardClientesCad = new javax.swing.JPanel();
        jLbClientesCadastrados = new javax.swing.JLabel();
        jLbTotalUsuariosCadast = new javax.swing.JLabel();
        jPnlcardProfissionais = new javax.swing.JPanel();
        jLbCardProfissionais = new javax.swing.JLabel();
        jLbTotalProfissionais = new javax.swing.JLabel();
        jPnlcardSolicitacoes = new javax.swing.JPanel();
        jLbCardProfAtivos = new javax.swing.JLabel();
        jLbTotalSolicitacoes = new javax.swing.JLabel();
        jPnlcardConcluidos = new javax.swing.JPanel();
        jLbCardAguardandoAprovacao = new javax.swing.JLabel();
        jLbTotalConcluidos = new javax.swing.JLabel();
        jBtnAtualizar = new javax.swing.JButton();
        jBtnPailnelAdmin = new javax.swing.JButton();
        jBtnCliente = new javax.swing.JButton();
        jBtnProfissionais = new javax.swing.JButton();
        jBtnSolicitacoes = new javax.swing.JButton();
        jBtnSair = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MonteMax-Painel Administrativo");
        setSize(new java.awt.Dimension(1100, 700));

        PainelMenu.setBackground(new java.awt.Color(23, 54, 93));

        jLbLogo.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLbLogo.setForeground(new java.awt.Color(255, 140, 0));
        jLbLogo.setText("MonteMax");

        jLbPainelAdmin.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jLbPainelAdmin.setForeground(new java.awt.Color(204, 204, 204));
        jLbPainelAdmin.setText("PAINEL ADMINISTRATIVO");

        jLbTituloPagina.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLbTituloPagina.setForeground(new java.awt.Color(23, 54, 93));
        jLbTituloPagina.setText("PAINEL ADMINISTRATIVO");

        jLbDescricaoPagina.setForeground(new java.awt.Color(102, 102, 102));
        jLbDescricaoPagina.setText("Visão geral do sistema MonteMax");

        jPnlCardClientesCad.setBackground(new java.awt.Color(232, 240, 254));

        jLbClientesCadastrados.setText("Clientes cadastrados");

        jLbTotalUsuariosCadast.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLbTotalUsuariosCadast.setText("0");

        javax.swing.GroupLayout jPnlCardClientesCadLayout = new javax.swing.GroupLayout(jPnlCardClientesCad);
        jPnlCardClientesCad.setLayout(jPnlCardClientesCadLayout);
        jPnlCardClientesCadLayout.setHorizontalGroup(
            jPnlCardClientesCadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlCardClientesCadLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPnlCardClientesCadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLbClientesCadastrados)
                    .addComponent(jLbTotalUsuariosCadast))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPnlCardClientesCadLayout.setVerticalGroup(
            jPnlCardClientesCadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlCardClientesCadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLbClientesCadastrados)
                .addGap(18, 18, 18)
                .addComponent(jLbTotalUsuariosCadast)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        jPnlcardProfissionais.setBackground(new java.awt.Color(228, 245, 234));

        jLbCardProfissionais.setText("Profissionais cadastrados");

        jLbTotalProfissionais.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLbTotalProfissionais.setText("0");

        javax.swing.GroupLayout jPnlcardProfissionaisLayout = new javax.swing.GroupLayout(jPnlcardProfissionais);
        jPnlcardProfissionais.setLayout(jPnlcardProfissionaisLayout);
        jPnlcardProfissionaisLayout.setHorizontalGroup(
            jPnlcardProfissionaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlcardProfissionaisLayout.createSequentialGroup()
                .addComponent(jLbCardProfissionais)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPnlcardProfissionaisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLbTotalProfissionais)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPnlcardProfissionaisLayout.setVerticalGroup(
            jPnlcardProfissionaisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlcardProfissionaisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLbCardProfissionais)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLbTotalProfissionais)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        jPnlcardSolicitacoes.setBackground(new java.awt.Color(255, 240, 217));

        jLbCardProfAtivos.setText("Profissionais ativos");

        jLbTotalSolicitacoes.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLbTotalSolicitacoes.setText("0");

        javax.swing.GroupLayout jPnlcardSolicitacoesLayout = new javax.swing.GroupLayout(jPnlcardSolicitacoes);
        jPnlcardSolicitacoes.setLayout(jPnlcardSolicitacoesLayout);
        jPnlcardSolicitacoesLayout.setHorizontalGroup(
            jPnlcardSolicitacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlcardSolicitacoesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPnlcardSolicitacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLbCardProfAtivos)
                    .addComponent(jLbTotalSolicitacoes))
                .addContainerGap(271, Short.MAX_VALUE))
        );
        jPnlcardSolicitacoesLayout.setVerticalGroup(
            jPnlcardSolicitacoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlcardSolicitacoesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLbCardProfAtivos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLbTotalSolicitacoes)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPnlcardConcluidos.setBackground(new java.awt.Color(240, 231, 250));

        jLbCardAguardandoAprovacao.setText("Aguardando aprovação");

        jLbTotalConcluidos.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLbTotalConcluidos.setText("0");

        javax.swing.GroupLayout jPnlcardConcluidosLayout = new javax.swing.GroupLayout(jPnlcardConcluidos);
        jPnlcardConcluidos.setLayout(jPnlcardConcluidosLayout);
        jPnlcardConcluidosLayout.setHorizontalGroup(
            jPnlcardConcluidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlcardConcluidosLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPnlcardConcluidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLbCardAguardandoAprovacao)
                    .addComponent(jLbTotalConcluidos))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPnlcardConcluidosLayout.setVerticalGroup(
            jPnlcardConcluidosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnlcardConcluidosLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLbCardAguardandoAprovacao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLbTotalConcluidos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jBtnAtualizar.setBackground(new java.awt.Color(45, 79, 121));
        jBtnAtualizar.setForeground(new java.awt.Color(255, 255, 255));
        jBtnAtualizar.setText("Atualizar");
        jBtnAtualizar.setBorderPainted(false);
        jBtnAtualizar.setFocusPainted(false);
        jBtnAtualizar.addActionListener(this::jBtnAtualizarActionPerformed);

        javax.swing.GroupLayout PailnelConteudoLayout = new javax.swing.GroupLayout(PailnelConteudo);
        PailnelConteudo.setLayout(PailnelConteudoLayout);
        PailnelConteudoLayout.setHorizontalGroup(
            PailnelConteudoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PailnelConteudoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PailnelConteudoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLbTituloPagina)
                    .addGroup(PailnelConteudoLayout.createSequentialGroup()
                        .addComponent(jLbDescricaoPagina)
                        .addGap(46, 46, 46)
                        .addComponent(jBtnAtualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPnlCardClientesCad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPnlcardProfissionais, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PailnelConteudoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPnlcardConcluidos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPnlcardSolicitacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PailnelConteudoLayout.setVerticalGroup(
            PailnelConteudoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PailnelConteudoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLbTituloPagina)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PailnelConteudoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLbDescricaoPagina)
                    .addComponent(jBtnAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(PailnelConteudoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPnlcardSolicitacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPnlCardClientesCad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PailnelConteudoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPnlcardConcluidos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PailnelConteudoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPnlcardProfissionais, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(118, 118, 118))
        );

        jBtnPailnelAdmin.setBackground(new java.awt.Color(45, 79, 121));
        jBtnPailnelAdmin.setForeground(new java.awt.Color(255, 255, 255));
        jBtnPailnelAdmin.setText("PAINEL ADMINISTRATIVO");
        jBtnPailnelAdmin.setBorderPainted(false);
        jBtnPailnelAdmin.setFocusPainted(false);
        jBtnPailnelAdmin.addActionListener(this::jBtnPailnelAdminActionPerformed);

        jBtnCliente.setBackground(new java.awt.Color(45, 79, 121));
        jBtnCliente.setForeground(new java.awt.Color(255, 255, 255));
        jBtnCliente.setText("Clientes");
        jBtnCliente.setBorderPainted(false);
        jBtnCliente.setFocusPainted(false);
        jBtnCliente.addActionListener(this::jBtnClienteActionPerformed);

        jBtnProfissionais.setBackground(new java.awt.Color(45, 79, 121));
        jBtnProfissionais.setForeground(new java.awt.Color(255, 255, 255));
        jBtnProfissionais.setText("Profissionais");
        jBtnProfissionais.setBorderPainted(false);
        jBtnProfissionais.setFocusPainted(false);
        jBtnProfissionais.addActionListener(this::jBtnProfissionaisActionPerformed);

        jBtnSolicitacoes.setBackground(new java.awt.Color(45, 79, 121));
        jBtnSolicitacoes.setForeground(new java.awt.Color(255, 255, 255));
        jBtnSolicitacoes.setText("Análise de profissionais");
        jBtnSolicitacoes.setBorderPainted(false);
        jBtnSolicitacoes.setFocusPainted(false);
        jBtnSolicitacoes.addActionListener(this::jBtnSolicitacoesActionPerformed);

        jBtnSair.setBackground(new java.awt.Color(45, 79, 121));
        jBtnSair.setForeground(new java.awt.Color(255, 255, 255));
        jBtnSair.setText("Sair");
        jBtnSair.setBorderPainted(false);
        jBtnSair.setFocusPainted(false);
        jBtnSair.addActionListener(this::jBtnSairActionPerformed);

        javax.swing.GroupLayout PainelMenuLayout = new javax.swing.GroupLayout(PainelMenu);
        PainelMenu.setLayout(PainelMenuLayout);
        PainelMenuLayout.setHorizontalGroup(
            PainelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PainelMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PainelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLbLogo)
                    .addComponent(jLbPainelAdmin)
                    .addComponent(jBtnProfissionais, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnSolicitacoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnPailnelAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jBtnSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(PailnelConteudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        PainelMenuLayout.setVerticalGroup(
            PainelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PainelMenuLayout.createSequentialGroup()
                .addGroup(PainelMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PainelMenuLayout.createSequentialGroup()
                        .addComponent(jLbLogo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLbPainelAdmin)
                        .addGap(29, 29, 29)
                        .addComponent(jBtnPailnelAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jBtnCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jBtnProfissionais, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jBtnSolicitacoes, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jBtnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(PailnelConteudo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(PainelMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PainelMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBtnSolicitacoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnSolicitacoesActionPerformed
        // botao + cod para ir para o forms analise de profissionais.
        JFrmTelaAnaliseProfissionais tela = new JFrmTelaAnaliseProfissionais();
        tela.setLocationRelativeTo(null);
        tela.setVisible(true); //
        this.setVisible(false);
    }//GEN-LAST:event_jBtnSolicitacoesActionPerformed

    private void jBtnClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnClienteActionPerformed
  // botao + cod para ir para o forms analise de profissionais.
        jFrmCliente tela = new jFrmCliente();
        tela.setLocationRelativeTo(null);
        tela.setVisible(true); //
        this.setVisible(false);      
    }//GEN-LAST:event_jBtnClienteActionPerformed

    private void jBtnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnSairActionPerformed
        JFrmTelaLogin sair = new JFrmTelaLogin(); //chamando tela login
        sair.setLocationRelativeTo(null);
        sair.setVisible(true);  //abrindo tela login
        this.setVisible(false); //fechando telaprincipal
    }//GEN-LAST:event_jBtnSairActionPerformed

    private void jBtnPailnelAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnPailnelAdminActionPerformed
        PailnelConteudo.removeAll();
        PailnelConteudo.add(PainelMenu);
        PailnelConteudo.revalidate();
        PailnelConteudo.repaint();
    }//GEN-LAST:event_jBtnPailnelAdminActionPerformed

    private void jBtnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAtualizarActionPerformed
       carregarTotalClientes(); //chamando o método pra atualizar a lista 
       carregarTotalProfissionais();
       carregarTotalProfissionaisAtivos();
       carregarTotalAguardandoAprovacao();
    }//GEN-LAST:event_jBtnAtualizarActionPerformed

    private void jBtnProfissionaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnProfissionaisActionPerformed
  // botao + cod para ir para o forms analise de profissionais.
        JFrmProfissionais tela = new JFrmProfissionais();
        tela.setLocationRelativeTo(null);
        tela.setVisible(true); //
        this.setVisible(false);        
    }//GEN-LAST:event_jBtnProfissionaisActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new JFrmTelaPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PailnelConteudo;
    private javax.swing.JPanel PainelMenu;
    private javax.swing.JButton jBtnAtualizar;
    private javax.swing.JButton jBtnCliente;
    private javax.swing.JButton jBtnPailnelAdmin;
    private javax.swing.JButton jBtnProfissionais;
    private javax.swing.JButton jBtnSair;
    private javax.swing.JButton jBtnSolicitacoes;
    private javax.swing.JLabel jLbCardAguardandoAprovacao;
    private javax.swing.JLabel jLbCardProfAtivos;
    private javax.swing.JLabel jLbCardProfissionais;
    private javax.swing.JLabel jLbClientesCadastrados;
    private javax.swing.JLabel jLbDescricaoPagina;
    private javax.swing.JLabel jLbLogo;
    private javax.swing.JLabel jLbPainelAdmin;
    private javax.swing.JLabel jLbTituloPagina;
    private javax.swing.JLabel jLbTotalConcluidos;
    private javax.swing.JLabel jLbTotalProfissionais;
    private javax.swing.JLabel jLbTotalSolicitacoes;
    private javax.swing.JLabel jLbTotalUsuariosCadast;
    private javax.swing.JPanel jPnlCardClientesCad;
    private javax.swing.JPanel jPnlcardConcluidos;
    private javax.swing.JPanel jPnlcardProfissionais;
    private javax.swing.JPanel jPnlcardSolicitacoes;
    // End of variables declaration//GEN-END:variables

//carregar o total de clientes cadastrados 
    private void carregarTotalClientes() {
    String sql = "SELECT COUNT(*) FROM usuario WHERE tipo_usuario = 'CLIENTE'"; //pegando no slq os dados do cliente na tabela usuario

    try (Connection conexao = Conexao.abrirConexao(); //xonectanto testanto   e exibindo uma menssagem caso tenha algum erro de conexão.
         PreparedStatement stmt = conexao.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            jLbTotalUsuariosCadast.setText(String.valueOf(rs.getInt(1)));
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Erro ao carregar total de clientes: " + e.getMessage());
    }
}
    
        private void carregarTotalProfissionais() {
    String sql = "SELECT COUNT(*) FROM usuario WHERE tipo_usuario = 'PROFISSIONAL'"; //pegando no slq os dados do cliente na tabela usuario

    try (Connection conexao = Conexao.abrirConexao(); //conectando testanto   e exibindo uma menssagem caso tenha algum erro de conexão.
         PreparedStatement stmt = conexao.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            jLbTotalProfissionais.setText(String.valueOf(rs.getInt(1)));
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Erro ao carregar total de profissionias: " + e.getMessage());
    }
}
    private void carregarTotalProfissionaisAtivos() {

        String sql = "SELECT COUNT(*) FROM profissional "
                   + "WHERE status_cadastro = 'APROVADO'";

        try (Connection conexao = Conexao.abrirConexao();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                jLbTotalSolicitacoes.setText(String.valueOf(rs.getInt(1)));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar profissionais ativos: " + e.getMessage());
        }
}  //metodos dos cards pra contar o total e aparecer no painel
    private void carregarTotalAguardandoAprovacao() {
// metodos dos cards pra contar o total e aparecer no painel
    String sql = "SELECT COUNT(*) FROM profissional "
               + "WHERE status_cadastro = 'PENDENTE'";

    try (Connection conexao = Conexao.abrirConexao();
         PreparedStatement stmt = conexao.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            jLbTotalConcluidos.setText(String.valueOf(rs.getInt(1)));
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this,
                "Erro ao carregar profissionais pendentes: " + e.getMessage());
    }
}
}
