package br.unisal.aula.dao;

import br.unisal.aula.model.RegistroFinanceiro;
import jakarta.persistence.*;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.YearMonth;
import java.util.*;
import java.util.function.Function;

/** One local H2 database. All writes own a transaction; entity managers never escape. */
public final class LocalDatabase implements FinanceRepository {
  private final EntityManagerFactory factory;
  private final Connection keeper;

  public LocalDatabase(String url) {
    if (!url.startsWith("jdbc:h2:") || url.contains(";"))
      throw new IllegalArgumentException("Only an embedded H2 path is supported.");
    try {
      keeper = DriverManager.getConnection(url, "ledger", "");
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot open embedded database", e);
    }
    try {
      migrate(url);
      factory =
          Persistence.createEntityManagerFactory(
              "ControleFinanceiroPU",
              Map.of(
                  "jakarta.persistence.jdbc.url", url,
                  "jakarta.persistence.jdbc.driver", "org.h2.Driver",
                  "jakarta.persistence.jdbc.user", "ledger",
                  "jakarta.persistence.jdbc.password", ""));
    } catch (RuntimeException e) {
      try {
        keeper.close();
      } catch (SQLException close) {
        e.addSuppressed(close);
      }
      throw e;
    }
  }

  private static void migrate(String url) {
    try (var c = DriverManager.getConnection(url, "ledger", "");
        var s = c.createStatement()) {
      s.execute("CREATE TABLE IF NOT EXISTS schema_version (version INTEGER PRIMARY KEY)");
      try (var r = s.executeQuery("SELECT COALESCE(MAX(version),0) FROM schema_version")) {
        r.next();
        int version = r.getInt(1);
        if (version > 1)
          throw new IllegalStateException("Database is newer than this application.");
        if (version == 1) return;
      }
      try (var stream = LocalDatabase.class.getResourceAsStream("/db/V1__ledger.sql")) {
        if (stream == null) throw new IOException("Missing schema migration");
        for (String sql : new String(stream.readAllBytes(), StandardCharsets.UTF_8).split(";"))
          if (!sql.isBlank()) s.execute(sql);
      }
      s.executeUpdate("INSERT INTO schema_version VALUES (1)");
    } catch (SQLException | IOException e) {
      throw new IllegalStateException(
          "Cannot open local database. Check the path, permissions and other running instances.",
          e);
    }
  }

  private <T> T transaction(Function<EntityManager, T> work) {
    try (var em = factory.createEntityManager()) {
      var tx = em.getTransaction();
      tx.begin();
      try {
        T result = work.apply(em);
        tx.commit();
        return result;
      } catch (RuntimeException e) {
        if (tx.isActive()) tx.rollback();
        throw e;
      }
    }
  }

  @Override
  public List<RegistroFinanceiro> entries() {
    try (var em = factory.createEntityManager()) {
      return em.createQuery(
              "SELECT e FROM RegistroFinanceiro e ORDER BY e.data DESC, e.id DESC",
              RegistroFinanceiro.class)
          .getResultList();
    }
  }

  private void checkCategory(EntityManager em, String category) {
    if (((Number)
                em.createNativeQuery("SELECT COUNT(*) FROM categories WHERE name=?")
                    .setParameter(1, category)
                    .getSingleResult())
            .intValue()
        == 0)
      throw new IllegalArgumentException("Categoria inexistente / Unknown category: " + category);
  }

  @Override
  public long save(RegistroFinanceiro entry) {
    return transaction(em -> {
      checkCategory(em, entry.getCategoria());
      if (entry.getId() == null) {
        em.persist(entry);
        em.flush();
        return entry.getId();
      }
      if (em.find(RegistroFinanceiro.class, entry.getId()) == null)
        throw new IllegalArgumentException("Registro removido / Entry no longer exists.");
      em.merge(entry);
      return entry.getId();
    });
  }

  @Override
  public void delete(long id) {
    transaction(em -> {
      var entry = em.find(RegistroFinanceiro.class, id);
      if (entry == null)
        throw new IllegalArgumentException("Registro removido / Entry no longer exists.");
      em.remove(entry);
      return null;
    });
  }

  @Override
  public List<String> categories() {
    try (var em = factory.createEntityManager()) {
      List<?> rows =
          em.createNativeQuery("SELECT name FROM categories ORDER BY name").getResultList();
      return rows.stream().map(String.class::cast).toList();
    }
  }

  @Override
  public void addCategory(String name) {
    String checked = RegistroFinanceiro.text(name, 60, "Categoria / Category");
    transaction(em -> {
      em.createNativeQuery("MERGE INTO categories (name) KEY(name) VALUES (?)")
          .setParameter(1, checked)
          .executeUpdate();
      return null;
    });
  }

  @Override
  public void removeCategory(String name) {
    transaction(em -> {
      long count =
          em.createQuery(
                  "SELECT COUNT(e) FROM RegistroFinanceiro e WHERE e.categoria=:category",
                  Long.class)
              .setParameter("category", name)
              .getSingleResult();
      if (count > 0) throw new IllegalArgumentException("Categoria em uso / Category is in use.");
      em.createNativeQuery("DELETE FROM categories WHERE name=?")
          .setParameter(1, name)
          .executeUpdate();
      return null;
    });
  }

  @Override
  public BigDecimal budget(YearMonth month) {
    try (var em = factory.createEntityManager()) {
      var values =
          em.createNativeQuery("SELECT amount FROM budgets WHERE month_key=?")
              .setParameter(1, month.toString())
              .getResultList();
      return values.isEmpty() ? new BigDecimal("0.00") : (BigDecimal) values.getFirst();
    }
  }

  @Override
  public void setBudget(YearMonth month, BigDecimal amount) {
    var checked = RegistroFinanceiro.money(amount, true);
    transaction(em -> {
      em.createNativeQuery("MERGE INTO budgets (month_key,amount) KEY(month_key) VALUES (?,?)")
          .setParameter(1, month.toString())
          .setParameter(2, checked)
          .executeUpdate();
      return null;
    });
  }

  @Override
  public void importEntries(List<RegistroFinanceiro> entries) {
    transaction(em -> {
      for (var entry : entries) {
        if (entry.getId() != null)
          throw new IllegalArgumentException("Only new entries can be imported.");
        checkCategory(em, entry.getCategoria());
        em.persist(entry);
      }
      return null;
    });
  }

  @Override
  public void close() {
    try {
      factory.close();
    } finally {
      try {
        keeper.close();
      } catch (SQLException e) {
        throw new IllegalStateException("Cannot close local database", e);
      }
    }
  }
}
