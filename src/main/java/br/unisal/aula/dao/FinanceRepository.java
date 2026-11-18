package br.unisal.aula.dao;

import br.unisal.aula.model.RegistroFinanceiro;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public interface FinanceRepository extends AutoCloseable {
  List<RegistroFinanceiro> entries();

  long save(RegistroFinanceiro entry);

  void delete(long id);

  List<String> categories();

  void addCategory(String name);

  void removeCategory(String name);

  BigDecimal budget(YearMonth month);

  void setBudget(YearMonth month, BigDecimal amount);

  void importEntries(List<RegistroFinanceiro> entries);

  @Override
  void close();
}
