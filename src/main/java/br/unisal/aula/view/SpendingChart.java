package br.unisal.aula.view;

import br.unisal.aula.controller.FluxoCaixa;
import br.unisal.aula.model.RegistroFinanceiro;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import javax.swing.*;

final class SpendingChart extends JPanel {
  private Map<String, BigDecimal> values = Map.of();
  private boolean english;

  SpendingChart() {
    setPreferredSize(new Dimension(300, 170));
    setOpaque(false);
  }

  void update(java.util.List<RegistroFinanceiro> entries, boolean english) {
    this.values = FluxoCaixa.expensesByCategory(entries);
    this.english = english;
    getAccessibleContext()
        .setAccessibleName(english ? "Expenses by category" : "Despesas por categoria");
    getAccessibleContext().setAccessibleDescription(values.toString());
    repaint();
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    var g = (Graphics2D) graphics.create();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(new Color(0x223C36));
    g.setFont(getFont().deriveFont(Font.BOLD, 15));
    g.drawString(english ? "Where your money goes" : "Para onde vai seu dinheiro", 0, 20);
    if (values.isEmpty()) {
      g.setFont(getFont());
      g.drawString(english ? "No expenses in this month." : "Nenhuma despesa neste mês.", 0, 60);
      g.dispose();
      return;
    }
    var sorted =
        values.entrySet().stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
            .toList();
    BigDecimal max = sorted.getFirst().getValue();
    var format =
        NumberFormat.getCurrencyInstance(english ? Locale.UK : Locale.forLanguageTag("pt-BR"));
    format.setCurrency(Currency.getInstance("BRL"));
    int y = 47;
    for (var e : sorted.stream().limit(4).toList()) {
      g.setFont(getFont().deriveFont(12f));
      g.setColor(new Color(0x52645F));
      String label = e.getKey().length() > 24 ? e.getKey().substring(0, 23) + "…" : e.getKey();
      g.drawString(label, 0, y);
      String amount = format.format(e.getValue());
      g.drawString(amount, Math.max(170, getWidth() - g.getFontMetrics().stringWidth(amount)), y);
      g.setColor(new Color(0xE7EFEB));
      g.fillRoundRect(0, y + 7, getWidth(), 7, 7, 7);
      g.setColor(new Color(0x267566));
      g.fillRoundRect(
          0,
          y + 7,
          Math.max(
              4,
              e.getValue()
                  .divide(max, 6, java.math.RoundingMode.HALF_UP)
                  .multiply(BigDecimal.valueOf(getWidth()))
                  .intValue()),
          7,
          7,
          7);
      y += 34;
    }
    g.dispose();
  }
}
