package br.unisal.aula;

import br.unisal.aula.dao.LocalDatabase;
import br.unisal.aula.model.*;
import br.unisal.aula.view.PrincipalFrame;
import com.formdev.flatlaf.FlatLightLaf;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import javax.swing.*;

public final class App {
  private App() {}

  public static void main(String[] args) {
    System.setProperty("apple.awt.application.name", "Controle Financeiro");
    FlatLightLaf.setup();
    UIManager.put("Component.arc", 12);
    UIManager.put("Button.arc", 12);
    UIManager.put("TextComponent.arc", 8);
    UIManager.put("defaultFont", new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
    SwingUtilities.invokeLater(() -> {
      var loading = new JDialog((java.awt.Frame) null, "Controle Financeiro");
      loading.add(new JLabel("  Abrindo banco local / Opening local database…  "));
      loading.pack();
      loading.setLocationRelativeTo(null);
      loading.setVisible(true);
      new SwingWorker<LocalDatabase, Void>() {
        protected LocalDatabase doInBackground() throws Exception {
          Path directory =
              Path.of(
                      System.getProperty(
                          "finance.dataDir",
                          Path.of(System.getProperty("user.home"), ".controle-financeiro")
                              .toString()))
                  .toAbsolutePath();
          if (directory.toString().contains(";"))
            throw new IllegalArgumentException("Invalid database directory");
          Files.createDirectories(directory);
          var db = new LocalDatabase("jdbc:h2:" + directory.resolve("ledger"));
          try {
            if (Arrays.asList(args).contains("--demo") && db.entries().isEmpty()) seed(db);
            return db;
          } catch (RuntimeException e) {
            db.close();
            throw e;
          }
        }

        protected void done() {
          loading.dispose();
          try {
            new PrincipalFrame(get()).setVisible(true);
          } catch (Exception e) {
            java.util.logging.Logger.getLogger(App.class.getName())
                .log(java.util.logging.Level.SEVERE, "Startup failed", e);
            JOptionPane.showMessageDialog(
                null,
                "Não foi possível abrir o banco local. Verifique permissões e outras instâncias.\nCould not open local database. Check permissions and other instances.",
                "Controle Financeiro",
                JOptionPane.ERROR_MESSAGE);
          }
        }
      }.execute();
    });
  }

  private static void seed(LocalDatabase db) {
    LocalDate date = YearMonth.now().atDay(1);
    db.importEntries(
        List.of(
            new RegistroFinanceiro(
                "Salário · exemplo", Tipo.RECEITA, new BigDecimal("6200.00"), "Salário", date),
            new RegistroFinanceiro(
                "Aluguel · exemplo",
                Tipo.DESPESA,
                new BigDecimal("1800.00"),
                "Casa",
                date.plusDays(2)),
            new RegistroFinanceiro(
                "Feira da semana · exemplo",
                Tipo.DESPESA,
                new BigDecimal("286.40"),
                "Alimentação",
                date.plusDays(4)),
            new RegistroFinanceiro(
                "Transporte · exemplo",
                Tipo.DESPESA,
                new BigDecimal("160.00"),
                "Transporte",
                date.plusDays(5)),
            new RegistroFinanceiro(
                "Cinema · exemplo",
                Tipo.DESPESA,
                new BigDecimal("68.00"),
                "Lazer",
                date.plusDays(6))));
    db.setBudget(YearMonth.now(), new BigDecimal("3500.00"));
  }
}
