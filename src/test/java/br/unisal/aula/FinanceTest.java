package br.unisal.aula;

import static org.junit.jupiter.api.Assertions.*;

import br.unisal.aula.controller.FluxoCaixa;
import br.unisal.aula.csv.CsvExchange;
import br.unisal.aula.dao.LocalDatabase;
import br.unisal.aula.model.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

class FinanceTest {
  @TempDir Path temp;

  RegistroFinanceiro entry(String text, String amount, Tipo type) {
    return new RegistroFinanceiro(
        text, type, new BigDecimal(amount), "Casa", LocalDate.of(2026, 11, 10));
  }

  @Test
  void moneyAndDatesAreExact() {
    var flow =
        FluxoCaixa.of(
            List.of(
                entry("A", "0.10", Tipo.RECEITA),
                entry("B", "0.20", Tipo.RECEITA),
                entry("C", "0.05", Tipo.DESPESA)));
    assertEquals(new BigDecimal("0.25"), flow.saldo());
    assertThrows(IllegalArgumentException.class, () -> entry(" ", "10", Tipo.RECEITA));
    for (String value : List.of("-1", "0", "1.001", "1000000000000"))
      assertThrows(IllegalArgumentException.class, () -> entry("X", value, Tipo.RECEITA));
    assertThrows(
        java.time.format.DateTimeParseException.class, () -> LocalDate.parse("2026-02-30"));
  }

  @Test
  void persistsEditsDeletesBudgetAndCategoriesAcrossRestart() {
    String url = "jdbc:h2:" + temp.resolve("ledger");
    long id;
    try (var db = new LocalDatabase(url)) {
      db.addCategory("Casa");
      id = db.save(entry("Aluguel", "750.00", Tipo.DESPESA));
      db.setBudget(YearMonth.of(2026, 11), new BigDecimal("1000.00"));
      assertThrows(IllegalArgumentException.class, () -> db.removeCategory("Casa"));
    }
    try (var db = new LocalDatabase(url)) {
      assertEquals(new BigDecimal("1000.00"), db.budget(YearMonth.of(2026, 11)));
      var updated = entry("Aluguel atualizado", "800.00", Tipo.DESPESA);
      updated.setId(id);
      db.save(updated);
      assertEquals("Aluguel atualizado", db.entries().getFirst().getDescricao());
      db.delete(id);
      assertTrue(db.entries().isEmpty());
      db.removeCategory("Casa");
      assertFalse(db.categories().contains("Casa"));
    }
  }

  @Test
  void csvRoundTripEscapesFormulasAndQuotes() throws Exception {
    var input = List.of(entry("=HYPERLINK(\"x\"); café\nnovo", "42.10", Tipo.DESPESA));
    var out = new StringWriter();
    CsvExchange.write(out, input);
    assertTrue(out.toString().contains("'=HYPERLINK"));
    assertEquals(
        input.getFirst().getDescricao(),
        CsvExchange.read(new StringReader(out.toString())).getFirst().getDescricao());
    for (String text : List.of("+x", "-x", "@x", "  =x", "\tx", "'normal")) {
      out = new StringWriter();
      CsvExchange.write(out, List.of(entry(text, "1", Tipo.RECEITA)));
      assertEquals(
          text.strip(),
          CsvExchange.read(new StringReader(out.toString())).getFirst().getDescricao());
    }
  }

  @Test
  void invalidImportLeavesDatabaseUnchanged() throws Exception {
    try (var db = new LocalDatabase("jdbc:h2:mem:" + UUID.randomUUID())) {
      db.addCategory("Casa");
      db.save(entry("Original", "1", Tipo.RECEITA));
      String header = "date,type,description,category,amount\n";
      for (String row :
          List.of(
              "2026-02-30,DESPESA,X,Casa,10",
              "2026-11-10,INVALID,X,Casa,10",
              "2026-11-10,DESPESA,X,Casa,-1",
              "2026-11-10,DESPESA,X,Casa,1.001",
              "2026-11-10,DESPESA,X,Casa,1,extra")) {
        assertThrows(
            IllegalArgumentException.class,
            () ->
                db.importEntries(
                    CsvExchange.read(
                        new StringReader(header + "2026-11-10,RECEITA,Good,Casa,10\n" + row))));
        assertEquals(1, db.entries().size());
      }
      assertThrows(
          IllegalArgumentException.class,
          () ->
              db.importEntries(
                  List.of(
                      entry("Unknown", "2", Tipo.RECEITA),
                      new RegistroFinanceiro(
                          "X", Tipo.RECEITA, BigDecimal.ONE, "Missing", LocalDate.now()))));
      assertEquals(1, db.entries().size());
    }
  }

  @Test
  void monthFilterDoesNotMixYears() {
    var a = entry("X", "10", Tipo.DESPESA);
    assertEquals(1, FluxoCaixa.month(List.of(a), YearMonth.of(2026, 11)).size());
    assertTrue(FluxoCaixa.month(List.of(a), YearMonth.of(2025, 11)).isEmpty());
  }

  @Test
  void categoryDeletionAndBudgetRemainAfterAnotherRestart() {
    String url = "jdbc:h2:" + temp.resolve("restart");
    try (var db = new LocalDatabase(url)) {
      assertTrue(db.categories().contains("Casa"));
      db.removeCategory("Casa");
      db.setBudget(YearMonth.of(2026, 11), new BigDecimal("0.00"));
      assertThrows(
          IllegalArgumentException.class,
          () -> db.setBudget(YearMonth.now(), new BigDecimal("-1")));
      assertThrows(
          IllegalArgumentException.class,
          () -> db.setBudget(YearMonth.now(), new BigDecimal("1.001")));
    }
    try (var db = new LocalDatabase(url)) {
      assertFalse(db.categories().contains("Casa"));
      assertEquals(new BigDecimal("0.00"), db.budget(YearMonth.of(2026, 11)));
    }
  }

  @Test
  void rejectsFutureSchemaRatherThanDestroyingIt() throws Exception {
    String url = "jdbc:h2:" + temp.resolve("future");
    try (var db = new LocalDatabase(url)) {
      db.save(entry("Keep", "1", Tipo.RECEITA));
    }
    try (var c = java.sql.DriverManager.getConnection(url, "ledger", "");
        var st = c.createStatement()) {
      st.execute("INSERT INTO schema_version VALUES(2)");
    }
    assertThrows(IllegalStateException.class, () -> new LocalDatabase(url));
    try (var c = java.sql.DriverManager.getConnection(url, "ledger", "");
        var st = c.createStatement();
        var rs = st.executeQuery("SELECT COUNT(*) FROM entries")) {
      rs.next();
      assertEquals(1, rs.getInt(1));
    }
  }

  @Test
  void csvRejectsMalformedQuotesHeadersAndTooManyRows() throws Exception {
    for (String value :
        List.of(
            "",
            "amount,date",
            "date,type,description,category,amount\n2026-11-10,DESPESA,\"broken,Casa,1")) {
      assertThrows(IllegalArgumentException.class, () -> CsvExchange.read(new StringReader(value)));
    }
    String oversized =
        "date,type,description,category,amount\n" + "2026-11-10,DESPESA,X,Casa,1\n".repeat(10001);
    assertThrows(
        IllegalArgumentException.class, () -> CsvExchange.read(new StringReader(oversized)));
  }

  @Test
  void rejectsStaleEditsAndMissingDeletes() {
    try (var db = new LocalDatabase("jdbc:h2:mem:" + UUID.randomUUID())) {
      var e = entry("X", "2", Tipo.DESPESA);
      long id = db.save(e);
      db.delete(id);
      assertThrows(IllegalArgumentException.class, () -> db.save(e));
      assertThrows(IllegalArgumentException.class, () -> db.delete(id));
      assertTrue(db.entries().isEmpty());
    }
  }

  @Test
  void categoryTotalsExcludeIncomeAndPreserveCents() {
    var expenses =
        FluxoCaixa.expensesByCategory(
            List.of(
                entry("A", "0.10", Tipo.DESPESA),
                entry("B", "0.20", Tipo.DESPESA),
                entry("C", "20", Tipo.RECEITA)));
    assertEquals(Map.of("Casa", new BigDecimal("0.30")), expenses);
    assertEquals(new BigDecimal("0.00"), FluxoCaixa.of(List.of()).saldo());
  }

  @Test
  void realCsvExampleImportsAndExportsAcrossRestart() throws Exception {
    String url = "jdbc:h2:" + temp.resolve("example");
    try (var db = new LocalDatabase(url);
        var reader = Files.newBufferedReader(Path.of("examples/transactions.csv"))) {
      db.importEntries(CsvExchange.read(reader));
      assertEquals(new BigDecimal("4400.00"), FluxoCaixa.of(db.entries()).saldo());
    }
    try (var db = new LocalDatabase(url)) {
      var out = new StringWriter();
      CsvExchange.write(out, db.entries());
      assertEquals(2, CsvExchange.read(new StringReader(out.toString())).size());
      assertEquals(new BigDecimal("4400.00"), FluxoCaixa.of(db.entries()).saldo());
    }
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new RegistroFinanceiro(
                "X", Tipo.RECEITA, BigDecimal.ONE, "Casa", LocalDate.of(10000, 1, 1)));
  }
}
