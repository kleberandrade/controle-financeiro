package br.unisal.aula.view;

import br.unisal.aula.controller.FluxoCaixa;
import br.unisal.aula.csv.CsvExchange;
import br.unisal.aula.dao.FinanceRepository;
import br.unisal.aula.model.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

/** Swing keeps the original editor + ledger + totals + chart workflow. */
public final class PrincipalFrame extends JFrame {
  private static final Color INK = new Color(0x193D34),
      MUTED = new Color(0x60746B),
      BACK = new Color(0xF2F5F1),
      GREEN = new Color(0x176C55);
  private final FinanceRepository repository;
  private final List<Runnable> translations = new ArrayList<>();
  private final List<JButton> actions = new ArrayList<>();
  private boolean english, busy;
  private YearMonth month = YearMonth.now();
  private Long editing;
  private List<RegistroFinanceiro> all = List.of(), visible = List.of();
  private BigDecimal budget = new BigDecimal("0.00");
  private final JTextField description = field("description"),
      amount = field("amount"),
      date = field("date"),
      budgetField = field("budget");
  private final JComboBox<Tipo> type = new JComboBox<>(Tipo.values());
  private final JComboBox<String> category = new JComboBox<>();
  private final JLabel period = new JLabel(),
      status = new JLabel(" "),
      incomeValue = new JLabel(),
      expenseValue = new JLabel(),
      balanceValue = new JLabel(),
      budgetState = new JLabel();
  private final JProgressBar progress = new JProgressBar(0, 100);
  private final SpendingChart chart = new SpendingChart();
  private final LedgerModel model = new LedgerModel();
  private final JTable table =
      new JTable(model) {
        @Override
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);
          if (getRowCount() == 0) {
            g.setColor(MUTED);
            g.drawString(
                t(
                    "Seu mês começa aqui. Adicione um lançamento.",
                    "Your month starts here. Add an entry."),
                24,
                60);
          }
        }
      };
  private final JPanel editor = panel(new GridBagLayout());
  private final JPanel workspace = new JPanel(new BorderLayout(20, 20));

  public PrincipalFrame(FinanceRepository repository) {
    this.repository = repository;
    setTitle("Controle Financeiro · Local");
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setMinimumSize(new Dimension(820, 640));
    setSize(1260, 850);
    setLocationRelativeTo(null);
    var root = new JPanel(new BorderLayout(0, 20));
    root.setBackground(BACK);
    root.setBorder(new EmptyBorder(24, 28, 16, 28));
    setContentPane(root);
    var top = new JPanel(new BorderLayout());
    top.setOpaque(false);
    var brand = new JPanel(new GridLayout(0, 1, 0, 3));
    brand.setOpaque(false);
    JLabel title = new JLabel("◈  Controle Financeiro");
    title.setFont(title.getFont().deriveFont(Font.BOLD, 25));
    title.setForeground(INK);
    brand.add(title);
    brand.add(
        label(
            "Clareza para cada escolha. • Banco local de demonstração",
            "Clarity for every choice. • Local demonstration database"));
    top.add(brand, BorderLayout.WEST);
    var languages = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
    languages.setOpaque(false);
    languages.add(language(false));
    languages.add(language(true));
    top.add(languages, BorderLayout.EAST);
    var heading = new JPanel(new BorderLayout(0, 20));
    heading.setOpaque(false);
    heading.add(top, BorderLayout.NORTH);
    var navigation = new JPanel(new BorderLayout());
    navigation.setOpaque(false);
    var months = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    months.setOpaque(false);
    months.add(button("‹", "‹", "previousMonth", () -> moveMonth(-1)));
    period.setFont(period.getFont().deriveFont(Font.BOLD, 17));
    months.add(period);
    months.add(button("›", "›", "nextMonth", () -> moveMonth(1)));
    navigation.add(months, BorderLayout.WEST);
    var csv = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    csv.setOpaque(false);
    csv.add(button("Importar CSV", "Import CSV", "import", this::importCsv));
    csv.add(button("Exportar mês", "Export month", "export", this::exportCsv));
    navigation.add(csv, BorderLayout.EAST);
    var overview = new JPanel(new GridLayout(1, 3, 14, 0));
    overview.setOpaque(false);
    overview.add(card("Receitas", "Income", incomeValue, GREEN));
    overview.add(card("Despesas", "Expenses", expenseValue, new Color(0xA44532)));
    overview.add(card("Saldo do mês", "Month balance", balanceValue, INK));
    var summary = new JPanel(new BorderLayout(0, 16));
    summary.setOpaque(false);
    summary.add(navigation, BorderLayout.NORTH);
    summary.add(overview, BorderLayout.CENTER);
    heading.add(summary, BorderLayout.CENTER);
    root.add(heading, BorderLayout.NORTH);
    makeEditor();
    var ledger = panel(new BorderLayout(0, 12));
    ledger.add(label("Lançamentos do mês", "Monthly entries"), BorderLayout.NORTH);
    table.setName("ledger");
    var textRenderer = new javax.swing.table.DefaultTableCellRenderer();
    textRenderer.putClientProperty("html.disable", true);
    table.setDefaultRenderer(String.class, textRenderer);
    var categoryRenderer = new DefaultListCellRenderer();
    categoryRenderer.putClientProperty("html.disable", true);
    category.setRenderer(categoryRenderer);
    table.setRowHeight(34);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setAutoCreateRowSorter(true);
    table.getAccessibleContext().setAccessibleName("Lançamentos / Entries");
    table
        .getSelectionModel()
        .addListSelectionListener(e -> {
          if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0)
            loadEditor(visible.get(table.convertRowIndexToModel(table.getSelectedRow())));
        });
    var scroll = new JScrollPane(table);
    scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE4EBE5)));
    scroll.setPreferredSize(new Dimension(600, 200));
    ledger.add(scroll, BorderLayout.CENTER);
    var reports = panel(new GridLayout(1, 2, 28, 0));
    reports.add(chart);
    reports.add(makeBudget());
    var right = new JPanel(new BorderLayout(0, 18));
    right.setOpaque(false);
    right.add(ledger, BorderLayout.CENTER);
    right.add(reports, BorderLayout.SOUTH);
    workspace.setOpaque(false);
    workspace.add(editor, BorderLayout.WEST);
    workspace.add(right, BorderLayout.CENTER);
    var contentScroll = new JScrollPane(workspace);
    contentScroll.setBorder(null);
    contentScroll.getVerticalScrollBar().setUnitIncrement(20);
    root.add(contentScroll, BorderLayout.CENTER);
    status.setForeground(MUTED);
    status.getAccessibleContext().setAccessibleName("Status");
    root.add(status, BorderLayout.SOUTH);
    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            reflow();
          }
        });
    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowOpened(WindowEvent e) {
            description.requestFocusInWindow();
          }

          @Override
          public void windowClosing(WindowEvent e) {
            if (!busy) {
              repository.close();
              dispose();
            }
          }
        });
    clearEditor();
    relabel();
    reload();
  }

  private void reflow() {
    if (getWidth() < 1080) {
      workspace.remove(editor);
      workspace.add(editor, BorderLayout.NORTH);
      editor.setPreferredSize(new Dimension(300, 450));
    } else {
      workspace.remove(editor);
      workspace.add(editor, BorderLayout.WEST);
      editor.setPreferredSize(new Dimension(300, 450));
    }
    workspace.revalidate();
  }

  private static JTextField field(String name) {
    var f = new JTextField();
    f.setName(name);
    f.setColumns(16);
    return f;
  }

  private String t(String pt, String en) {
    return english ? en : pt;
  }

  private JPanel panel(LayoutManager layout) {
    var p = new JPanel(layout);
    p.setBackground(Color.WHITE);
    p.setBorder(new EmptyBorder(18, 18, 18, 18));
    return p;
  }

  private JLabel label(String pt, String en) {
    var l = new JLabel();
    l.setForeground(MUTED);
    translations.add(() -> l.setText(t(pt, en)));
    return l;
  }

  private JButton button(String pt, String en, String name, Runnable action) {
    var b = new JButton();
    b.setName(name);
    b.setPreferredSize(
        new Dimension(
            Math.max(
                44,
                Math.max(
                        b.getFontMetrics(b.getFont()).stringWidth(pt),
                        b.getFontMetrics(b.getFont()).stringWidth(en))
                    + 30),
            38));
    translations.add(() -> {
      b.setText(t(pt, en));
      b.getAccessibleContext().setAccessibleName(t(pt, en));
    });
    b.addActionListener(e -> action.run());
    actions.add(b);
    return b;
  }

  private JButton language(boolean en) {
    var b = new JButton(new FlagIcon(!en));
    b.setPreferredSize(new Dimension(44, 44));
    b.setName(en ? "english" : "portuguese");
    b.setToolTipText(en ? "English (United Kingdom)" : "Português (Brasil)");
    b.getAccessibleContext().setAccessibleName(b.getToolTipText());
    b.addActionListener(e -> {
      english = en;
      relabel();
    });
    return b;
  }

  private JPanel card(String pt, String en, JLabel value, Color color) {
    var p = panel(new BorderLayout(0, 8));
    p.add(label(pt, en), BorderLayout.NORTH);
    value.setFont(value.getFont().deriveFont(Font.BOLD, 27));
    value.setForeground(color);
    p.add(value, BorderLayout.CENTER);
    return p;
  }

  private void addField(int row, String pt, String en, JComponent component) {
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = row * 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(6, 0, 5, 0);
    var l = label(pt, en);
    l.setLabelFor(component);
    editor.add(l, c);
    c.gridy++;
    c.insets = new Insets(0, 0, 4, 0);
    editor.add(component, c);
  }

  private void makeEditor() {
    editor.setPreferredSize(new Dimension(300, 450));
    addField(0, "Descrição", "Description", description);
    type.setName("type");
    type.setRenderer(
        new DefaultListCellRenderer() {
          @Override
          public Component getListCellRendererComponent(
              JList<?> l, Object v, int i, boolean s, boolean f) {
            return super.getListCellRendererComponent(
                l,
                v instanceof Tipo kind
                    ? (kind == Tipo.RECEITA ? t("Receita", "Income") : t("Despesa", "Expense"))
                    : v,
                i,
                s,
                f);
          }
        });
    addField(1, "Tipo", "Type", type);
    addField(2, "Valor em reais · ex.: 1250.50", "Amount in BRL · e.g. 1250.50", amount);
    category.setName("category");
    addField(3, "Categoria", "Category", category);
    addField(4, "Data · AAAA-MM-DD", "Date · YYYY-MM-DD", date);
    var controls = new JPanel(new GridLayout(0, 2, 8, 8));
    controls.setOpaque(false);
    var save = button("Salvar", "Save", "save", this::save);
    save.setBackground(GREEN);
    save.setForeground(Color.WHITE);
    controls.add(save);
    controls.add(button("Novo / limpar", "New / clear", "clear", this::clearEditor));
    controls.add(button("Excluir", "Delete", "delete", this::delete));
    controls.add(button("Categorias", "Categories", "categories", this::categories));
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 10;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(12, 0, 0, 0);
    editor.add(controls, c);
    c.gridy = 11;
    c.weighty = 1;
    editor.add(Box.createVerticalGlue(), c);
    getRootPane().setDefaultButton(save);
  }

  private JPanel makeBudget() {
    var p = new JPanel(new BorderLayout(0, 10));
    p.setOpaque(false);
    p.add(label("Orçamento de despesas", "Expense budget"), BorderLayout.NORTH);
    var middle = new JPanel();
    middle.setLayout(new BoxLayout(middle, BoxLayout.Y_AXIS));
    middle.setOpaque(false);
    middle.add(budgetState);
    middle.add(Box.createVerticalStrut(9));
    progress.setStringPainted(true);
    progress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
    middle.add(progress);
    middle.add(Box.createVerticalStrut(9));
    var controls = new JPanel(new BorderLayout(8, 0));
    controls.setOpaque(false);
    controls.add(budgetField, BorderLayout.CENTER);
    budgetField
        .getAccessibleContext()
        .setAccessibleName("Orçamento mensal em reais / Monthly budget in BRL");
    controls.add(button("Definir", "Set", "setBudget", this::setBudget), BorderLayout.EAST);
    controls.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
    middle.add(controls);
    middle.add(Box.createVerticalStrut(9));
    middle.add(label("0 = sem limite. Valor em reais.", "0 = no limit. Amount in BRL."));
    for (Component child : middle.getComponents()) {
      if (child instanceof JComponent component) component.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    p.add(middle, BorderLayout.CENTER);
    return p;
  }

  private NumberFormat currency() {
    var f = NumberFormat.getCurrencyInstance(english ? Locale.UK : Locale.forLanguageTag("pt-BR"));
    f.setCurrency(Currency.getInstance("BRL"));
    return f;
  }

  private void relabel() {
    translations.forEach(Runnable::run);
    type.repaint();
    model.fireTableStructureChanged();
    render();
  }

  private void render() {
    period.setText(
        month.format(
            DateTimeFormatter.ofPattern(
                "MMMM yyyy", english ? Locale.UK : Locale.forLanguageTag("pt-BR"))));
    var flow = FluxoCaixa.of(visible);
    var f = currency();
    incomeValue.setText(f.format(flow.receita()));
    expenseValue.setText(f.format(flow.despesa()));
    balanceValue.setText(f.format(flow.saldo()));
    chart.update(visible, english);
    table
        .getAccessibleContext()
        .setAccessibleDescription(
            visible.size() + t(" lançamentos neste mês", " entries this month"));
    if (budget.signum() == 0) {
      budgetState.setText(t("Sem limite definido", "No budget set"));
      progress.setValue(0);
      progress.setString("—");
    } else {
      var remaining = budget.subtract(flow.despesa());
      budgetState.setText(t("Disponível: ", "Remaining: ") + f.format(remaining));
      int percent =
          flow.despesa()
              .multiply(BigDecimal.valueOf(100))
              .divide(budget, 0, java.math.RoundingMode.DOWN)
              .min(BigDecimal.valueOf(Integer.MAX_VALUE))
              .intValue();
      progress.setValue(Math.min(100, percent));
      progress.setString(percent + "%");
      progress.setForeground(remaining.signum() < 0 ? new Color(0xA44532) : GREEN);
    }
  }

  private record Snapshot(
      List<RegistroFinanceiro> entries, List<String> categories, BigDecimal budget) {}

  private Snapshot snapshot() {
    return new Snapshot(repository.entries(), repository.categories(), repository.budget(month));
  }

  private void apply(Snapshot s) {
    all = s.entries();
    visible = FluxoCaixa.month(all, month);
    budget = s.budget();
    Object selected = category.getSelectedItem();
    category.setModel(new DefaultComboBoxModel<>(s.categories().toArray(String[]::new)));
    if (selected != null && s.categories().contains(selected)) category.setSelectedItem(selected);
    model.fireTableDataChanged();
    budgetField.setText(budget.toPlainString());
    render();
  }

  private void reload() {
    run(this::snapshot, this::apply);
  }

  private <T> void run(Callable<T> work, Consumer<T> done) {
    if (busy) return;
    busy = true;
    actions.forEach(b -> b.setEnabled(false));
    status.setText(t("Processando…", "Working…"));
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    new SwingWorker<T, Void>() {
      @Override
      protected T doInBackground() throws Exception {
        return work.call();
      }

      @Override
      protected void done() {
        busy = false;
        actions.forEach(b -> b.setEnabled(true));
        setCursor(Cursor.getDefaultCursor());
        try {
          T result = get();
          status.setText(
              t(
                  "Salvo neste computador • BRL • Sem conexão bancária",
                  "Saved on this computer • BRL • No bank connection"));
          done.accept(result);
        } catch (Exception e) {
          Throwable cause = e instanceof java.util.concurrent.ExecutionException ? e.getCause() : e;
          error(cause);
        }
      }
    }.execute();
  }

  private void error(Throwable e) {
    String message =
        e instanceof IllegalArgumentException
            ? e.getMessage()
            : t(
                "Não foi possível concluir. Verifique o arquivo ou banco local e tente novamente.",
                "Could not complete. Check the file or local database and try again.");
    if (!(e instanceof IllegalArgumentException))
      java.util.logging.Logger.getLogger(getClass().getName())
          .log(java.util.logging.Level.SEVERE, "Local operation failed", e);
    status.setText(t("Operação não concluída", "Operation failed"));
    JOptionPane.showMessageDialog(
        this, message, t("Confira os dados", "Check your input"), JOptionPane.ERROR_MESSAGE);
  }

  private void clearEditor() {
    editing = null;
    table.clearSelection();
    description.setText("");
    amount.setText("");
    date.setText(LocalDate.now().toString());
    type.setSelectedItem(Tipo.DESPESA);
    description.requestFocusInWindow();
  }

  private void loadEditor(RegistroFinanceiro e) {
    editing = e.getId();
    description.setText(e.getDescricao());
    amount.setText(e.getValor().toPlainString());
    date.setText(e.getData().toString());
    type.setSelectedItem(e.getTipo());
    category.setSelectedItem(e.getCategoria());
  }

  private BigDecimal parseMoney(String input) {
    try {
      return new BigDecimal(input.strip().replace(',', '.'));
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          t(
              "Informe um valor como 1250.50, sem separador de milhar.",
              "Enter an amount like 1250.50, without thousands separators."),
          e);
    }
  }

  private void save() {
    try {
      LocalDate parsed;
      try {
        parsed = LocalDate.parse(date.getText().strip());
      } catch (java.time.format.DateTimeParseException e) {
        throw new IllegalArgumentException(
            t("Data inválida. Use AAAA-MM-DD.", "Invalid date. Use YYYY-MM-DD."), e);
      }
      var entry =
          new RegistroFinanceiro(
              description.getText(),
              (Tipo) type.getSelectedItem(),
              parseMoney(amount.getText()),
              (String) category.getSelectedItem(),
              parsed);
      entry.setId(editing);
      run(
          () -> {
            repository.save(entry);
            return snapshot();
          },
          s -> {
            apply(s);
            clearEditor();
          });
    } catch (IllegalArgumentException e) {
      error(e);
    }
  }

  private void delete() {
    if (editing == null) {
      error(new IllegalArgumentException(t("Selecione um lançamento.", "Select an entry.")));
      return;
    }
    if (JOptionPane.showConfirmDialog(
            this,
            t("Excluir o lançamento selecionado?", "Delete the selected entry?"),
            t("Confirmar exclusão", "Confirm deletion"),
            JOptionPane.OK_CANCEL_OPTION)
        != JOptionPane.OK_OPTION) return;
    long id = editing;
    run(
        () -> {
          repository.delete(id);
          return snapshot();
        },
        s -> {
          apply(s);
          clearEditor();
        });
  }

  private void moveMonth(int offset) {
    month = month.plusMonths(offset);
    reload();
  }

  private void setBudget() {
    try {
      var value = RegistroFinanceiro.money(parseMoney(budgetField.getText()), true);
      run(
          () -> {
            repository.setBudget(month, value);
            return snapshot();
          },
          this::apply);
    } catch (IllegalArgumentException e) {
      error(e);
    }
  }

  private void categories() {
    Object[] options = {
      t("Adicionar", "Add"), t("Remover selecionada", "Remove selected"), t("Cancelar", "Cancel")
    };
    int choice =
        JOptionPane.showOptionDialog(
            this,
            t(
                "Categorias são compartilhadas entre todos os meses.",
                "Categories are shared across all months."),
            t("Categorias", "Categories"),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]);
    if (choice == 0) {
      String name =
          JOptionPane.showInputDialog(this, t("Nome da nova categoria", "New category name"));
      if (name != null)
        run(
            () -> {
              repository.addCategory(name);
              return snapshot();
            },
            this::apply);
    } else if (choice == 1 && category.getSelectedItem() != null) {
      String name = (String) category.getSelectedItem();
      run(
          () -> {
            repository.removeCategory(name);
            return snapshot();
          },
          this::apply);
    }
  }

  private void importCsv() {
    var chooser = new JFileChooser();
    if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
    Path path = chooser.getSelectedFile().toPath();
    run(
        () -> {
          if (Files.size(path) > 5_000_000)
            throw new IllegalArgumentException(t("Limite de 5 MB.", "5 MB limit."));
          try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return CsvExchange.read(reader);
          }
        },
        entries -> confirmImport(entries));
  }

  private void confirmImport(List<RegistroFinanceiro> entries) {
    if (JOptionPane.showConfirmDialog(
            this,
            entries.size()
                + t(
                    " lançamentos serão adicionados. Categorias devem existir. Reimportar duplica dados. Continuar?",
                    " entries will be added. Categories must exist. Reimporting duplicates data. Continue?"),
            t("Revisar importação", "Review import"),
            JOptionPane.OK_CANCEL_OPTION)
        != JOptionPane.OK_OPTION) return;
    run(
        () -> {
          repository.importEntries(entries);
          return snapshot();
        },
        this::apply);
  }

  private void exportCsv() {
    var chooser = new JFileChooser();
    chooser.setSelectedFile(new java.io.File("controle-" + month + ".csv"));
    if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
    Path path = chooser.getSelectedFile().toPath();
    if (Files.exists(path)
        && JOptionPane.showConfirmDialog(
                this,
                t("Substituir arquivo existente?", "Replace existing file?"),
                t("Confirmar", "Confirm"),
                JOptionPane.OK_CANCEL_OPTION)
            != JOptionPane.OK_OPTION) return;
    List<RegistroFinanceiro> data = List.copyOf(visible);
    run(
        () -> {
          Path temp = Files.createTempFile(path.toAbsolutePath().getParent(), ".ledger-", ".csv");
          try {
            try (var writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
              CsvExchange.write(writer, data);
            }
            Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
          } finally {
            Files.deleteIfExists(temp);
          }
          return true;
        },
        ignored -> {});
  }

  private final class LedgerModel extends AbstractTableModel {
    public int getRowCount() {
      return visible.size();
    }

    public int getColumnCount() {
      return 5;
    }

    public String getColumnName(int col) {
      return (english
              ? new String[] {"Date", "Description", "Category", "Type", "Amount (BRL)"}
              : new String[] {"Data", "Descrição", "Categoria", "Tipo", "Valor (BRL)"})
          [col];
    }

    public Object getValueAt(int row, int col) {
      var e = visible.get(row);
      return switch (col) {
        case 0 -> e.getData();
        case 1 -> e.getDescricao();
        case 2 -> e.getCategoria();
        case 3 -> e.getTipo() == Tipo.RECEITA ? t("Receita", "Income") : t("Despesa", "Expense");
        default -> e.getValor();
      };
    }

    public Class<?> getColumnClass(int col) {
      return col == 0 ? LocalDate.class : col == 4 ? BigDecimal.class : String.class;
    }
  }
}
