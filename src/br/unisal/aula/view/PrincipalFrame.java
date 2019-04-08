package br.unisal.aula.view;

import br.unisal.aula.charts.BarChartHelper;
import br.unisal.aula.charts.PieChartHelper;
import br.unisal.aula.controller.FluxoCaixa;
import br.unisal.aula.dao.RegistroFinanceiroDAO;
import br.unisal.aula.helpers.MensagemHelper;
import br.unisal.aula.helpers.ValidarRegistroHelper;
import br.unisal.aula.model.RegistroFinanceiro;
import br.unisal.aula.tables.RegistroFinanceiroTableModel;
import java.awt.Frame;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JFrame;
import org.jfree.chart.JFreeChart;

public class PrincipalFrame extends javax.swing.JFrame {

    private List<RegistroFinanceiro> registros;
    private RegistroFinanceiroTableModel modelo;
    private FluxoCaixa fluxoCaixa;

    public PrincipalFrame() {
        initComponents();
        inicializar();
        atualizarGraficoBarras();
        atualizarGraficoPizza();
        atualizarFluxoCaixaDaTela();
        
        super.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void atualizarFluxoCaixaDaTela() {
        Locale local = new Locale("pt", "BR");
        NumberFormat formatador = NumberFormat.getCurrencyInstance(local);

        double receita = fluxoCaixa.getReceita();
        double despesa = fluxoCaixa.getDespesa();
        double saldo = fluxoCaixa.getSaldo();

        receitaLabel.setText(formatador.format(receita));
        despesaLabel.setText(formatador.format(despesa));
        saldoLabel.setText(formatador.format(saldo));
    }

    private void atualizarGraficoBarras() {
        String[] legendas = {"Receita", "Despesa", "Saldo"};
        double[] valores = {fluxoCaixa.getReceita(),
            fluxoCaixa.getDespesa(), fluxoCaixa.getSaldo()};
        JFreeChart grafico = BarChartHelper.criarGrafico(legendas, valores);
        BarChartHelper.setGraficoNoPainel(grafico, graficoBarrasPanel);
    }
    
    private void atualizarGraficoPizza(){
        int quantidade = categoriaComboBox.getItemCount();
        String[] categorias = new String[quantidade];
        for (int i = 0; i < quantidade; i++) {
            categorias[i] = (String)categoriaComboBox.getItemAt(i);
        }
        
        JFreeChart grafico = PieChartHelper.criarGrafico(registros, categorias);
        PieChartHelper.setGraficoNoPainel(grafico, graficoPizzaPanel);
    }

    private List<RegistroFinanceiro> getRegistrosFinanceiros() {
        List<RegistroFinanceiro> lista = new ArrayList<>();

        RegistroFinanceiroDAO dao = new RegistroFinanceiroDAO();
        lista = dao.findAll();
        
        return lista;
    }

    private void inicializar() {
        registros = getRegistrosFinanceiros();
        modelo = new RegistroFinanceiroTableModel(registros);
        jTable1.setModel(modelo);
        fluxoCaixa = new FluxoCaixa(registros);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        receitaLabel = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        despesaLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        saldoLabel = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        descricaoTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tipoComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        valorTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        categoriaComboBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        excluirButton = new javax.swing.JButton();
        limparButton = new javax.swing.JButton();
        atualizarButton = new javax.swing.JButton();
        salvarButton = new javax.swing.JButton();
        dataDateChooser = new com.toedter.calendar.JDateChooser();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        graficoPizzaPanel = new javax.swing.JPanel();
        graficoBarrasPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema Financeiro - v0.1");

        jPanel1.setBackground(new java.awt.Color(236, 240, 241));

        jPanel2.setBackground(new java.awt.Color(236, 240, 241));
        jPanel2.setLayout(new java.awt.GridLayout(1, 3, 10, 0));

        jPanel3.setBackground(new java.awt.Color(39, 174, 96));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Receita");

        receitaLabel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        receitaLabel.setForeground(new java.awt.Color(255, 255, 255));
        receitaLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        receitaLabel.setText("R$ 10000,00");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(receitaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receitaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(jPanel3);

        jPanel4.setBackground(new java.awt.Color(192, 57, 43));

        despesaLabel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        despesaLabel.setForeground(new java.awt.Color(255, 255, 255));
        despesaLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        despesaLabel.setText("R$ 10000,00");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Despesa");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(despesaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(despesaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(jPanel4);

        jPanel5.setBackground(new java.awt.Color(41, 128, 185));

        saldoLabel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        saldoLabel.setForeground(new java.awt.Color(255, 255, 255));
        saldoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        saldoLabel.setText("R$ 10000,00");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Saldo");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saldoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saldoLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(jPanel5);

        jPanel6.setBackground(new java.awt.Color(236, 240, 241));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 199), 2));

        jLabel1.setText("Descrição");

        descricaoTextField.setToolTipText("");

        jLabel2.setText("Tipo");

        tipoComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Despesa", "Receita" }));

        jLabel3.setText("Valor");

        jLabel4.setText("Categoria");

        categoriaComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Alimentação", "Casa", "Educação", "Lazer", "Transporte", "Tecnologia", "Saúde", "Outros" }));

        jLabel5.setText("Data");

        jPanel8.setBackground(new java.awt.Color(236, 240, 241));
        jPanel8.setLayout(new java.awt.GridLayout(1, 4, 10, 0));

        excluirButton.setText("EXCLUIR");
        excluirButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excluirButtonActionPerformed(evt);
            }
        });
        jPanel8.add(excluirButton);

        limparButton.setText("LIMPAR");
        limparButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limparButtonActionPerformed(evt);
            }
        });
        jPanel8.add(limparButton);

        atualizarButton.setText("ATUALIZAR");
        atualizarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atualizarButtonActionPerformed(evt);
            }
        });
        jPanel8.add(atualizarButton);

        salvarButton.setText("SALVAR");
        salvarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salvarButtonActionPerformed(evt);
            }
        });
        jPanel8.add(salvarButton);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(descricaoTextField)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(categoriaComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tipoComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel1))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(valorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(dataDateChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descricaoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(valorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tipoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dataDateChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(categoriaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(236, 240, 241));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 199), 2));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Descrição", "Tipo", "Valor", "Categoria", "Data"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel9.setBackground(new java.awt.Color(236, 240, 241));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(189, 195, 199), 2));

        graficoPizzaPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout graficoPizzaPanelLayout = new javax.swing.GroupLayout(graficoPizzaPanel);
        graficoPizzaPanel.setLayout(graficoPizzaPanelLayout);
        graficoPizzaPanelLayout.setHorizontalGroup(
            graficoPizzaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graficoPizzaPanelLayout.setVerticalGroup(
            graficoPizzaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        graficoBarrasPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout graficoBarrasPanelLayout = new javax.swing.GroupLayout(graficoBarrasPanel);
        graficoBarrasPanel.setLayout(graficoBarrasPanelLayout);
        graficoBarrasPanelLayout.setHorizontalGroup(
            graficoBarrasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graficoBarrasPanelLayout.setVerticalGroup(
            graficoBarrasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(graficoPizzaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graficoBarrasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(graficoBarrasPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graficoPizzaPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );

        setSize(new java.awt.Dimension(1052, 612));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void limparButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limparButtonActionPerformed
        descricaoTextField.setText("");
        valorTextField.setText("");
        dataDateChooser.setDate(new Date());
        tipoComboBox.setSelectedIndex(0);
        categoriaComboBox.setSelectedIndex(0);

        jTable1.clearSelection();
        atualizarGraficoBarras();
        atualizarGraficoPizza();
        atualizarFluxoCaixaDaTela();
    }//GEN-LAST:event_limparButtonActionPerformed

    public RegistroFinanceiro pegarUmRegistroFinanceiroDaTela() {
        // 1. Pegar todas as informcação da tela
        String descricao = descricaoTextField.getText();
        String tipo = String.valueOf(tipoComboBox.getSelectedItem());
        String categoria = String.valueOf(categoriaComboBox.getSelectedItem());
        String valorStr = valorTextField.getText();
        Date data = dataDateChooser.getDate();

        try {
            return ValidarRegistroHelper.criarRegistroFinanceiro(descricao, 
                    tipo, valorStr, categoria, data);
        } catch (Exception ex) {
            MensagemHelper.exibir(ex.getMessage());
        }

        return null;
    }

    private void salvarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salvarButtonActionPerformed
        RegistroFinanceiro registro = pegarUmRegistroFinanceiroDaTela();
        if (registro == null) {
            return;
        }

        modelo.adicionar(registro);
        fluxoCaixa.inserirRegistroFinanceiro(registro);
        
        RegistroFinanceiroDAO dao = new RegistroFinanceiroDAO();
        dao.create(registro);
        
        limparButton.doClick();
    }//GEN-LAST:event_salvarButtonActionPerformed

    private void excluirButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excluirButtonActionPerformed
        int linhaSelecionada = jTable1.getSelectedRow();
        if (linhaSelecionada < 0) {
            return;
        }

        RegistroFinanceiro registro = modelo.excluir(linhaSelecionada);
        fluxoCaixa.removerRegistroFinanceiro(registro);
        
        RegistroFinanceiroDAO dao = new RegistroFinanceiroDAO();
        dao.delete(registro);
        
        limparButton.doClick();
    }//GEN-LAST:event_excluirButtonActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // 1. Pegar a linha selecionada
        int linha = jTable1.getSelectedRow();

        // 2. Pegar o item (registro financeiro) da linha da tabela
        RegistroFinanceiro registro = modelo.getRegistroFinanceiro(linha);

        // 3. Fazer conversões necessárias
        String valorStr = String.valueOf(registro.getValor());

        // 4. Colocar as informações na tela
        descricaoTextField.setText(registro.getDescricao());
        categoriaComboBox.setSelectedItem(registro.getCategoria());
        tipoComboBox.setSelectedItem(registro.getTipo());
        dataDateChooser.setDate(registro.getData());
        valorTextField.setText(valorStr);
    }//GEN-LAST:event_jTable1MouseClicked

    private void atualizarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atualizarButtonActionPerformed
        RegistroFinanceiro registro = pegarUmRegistroFinanceiroDaTela();
        if (registro == null) {
            return;
        }
        
        int linha = jTable1.getSelectedRow(); 
        int id = modelo.getRegistroFinanceiro(linha).getId();
        registro.setId(id);
        
        modelo.atualizar(registro, linha);
        fluxoCaixa.atualizarRegistrosFinanceiro(registros);
        
        RegistroFinanceiroDAO dao = new RegistroFinanceiroDAO();
        dao.update(registro);
        
        limparButton.doClick();
    }//GEN-LAST:event_atualizarButtonActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrincipalFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton atualizarButton;
    private javax.swing.JComboBox categoriaComboBox;
    private com.toedter.calendar.JDateChooser dataDateChooser;
    private javax.swing.JTextField descricaoTextField;
    private javax.swing.JLabel despesaLabel;
    private javax.swing.JButton excluirButton;
    private javax.swing.JPanel graficoBarrasPanel;
    private javax.swing.JPanel graficoPizzaPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton limparButton;
    private javax.swing.JLabel receitaLabel;
    private javax.swing.JLabel saldoLabel;
    private javax.swing.JButton salvarButton;
    private javax.swing.JComboBox tipoComboBox;
    private javax.swing.JTextField valorTextField;
    // End of variables declaration//GEN-END:variables
}
