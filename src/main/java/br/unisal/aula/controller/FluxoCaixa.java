package br.unisal.aula.controller;

import br.unisal.aula.model.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

public record FluxoCaixa(BigDecimal receita, BigDecimal despesa) {
  public BigDecimal saldo() {
    return receita.subtract(despesa);
  }

  public static FluxoCaixa of(List<RegistroFinanceiro> entries) {
    BigDecimal income = new BigDecimal("0.00"), expense = income;
    for (var e : entries) {
      if (e.getTipo() == Tipo.RECEITA) income = income.add(e.getValor());
      else expense = expense.add(e.getValor());
    }
    return new FluxoCaixa(income, expense);
  }

  public static List<RegistroFinanceiro> month(List<RegistroFinanceiro> entries, YearMonth month) {
    return entries.stream().filter(e -> YearMonth.from(e.getData()).equals(month)).toList();
  }

  public static Map<String, BigDecimal> expensesByCategory(List<RegistroFinanceiro> entries) {
    Map<String, BigDecimal> result = new TreeMap<>();
    entries.stream()
        .filter(e -> e.getTipo() == Tipo.DESPESA)
        .forEach(e -> result.merge(e.getCategoria(), e.getValor(), BigDecimal::add));
    return result;
  }
}
