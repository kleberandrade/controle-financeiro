package br.unisal.aula.csv;

import br.unisal.aula.model.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import org.apache.commons.csv.*;

/** UTF-8 RFC4180 CSV. Text has a reversible apostrophe guard against spreadsheet formulas. */
public final class CsvExchange {
  private CsvExchange() {}

  private static final String[] HEADER = {"date", "type", "description", "category", "amount"};

  private static String protect(String text) {
    String trimmed = text.stripLeading();
    return text.startsWith("'")
            || (!trimmed.isEmpty() && "=+-@".indexOf(trimmed.charAt(0)) >= 0)
            || text.startsWith("\t")
            || text.startsWith("\r")
            || text.startsWith("\n")
        ? "'" + text
        : text;
  }

  private static String restore(String text) {
    if (text.startsWith("'") && protect(text.substring(1)).equals(text)) return text.substring(1);
    return text;
  }

  public static void write(Writer writer, List<RegistroFinanceiro> entries) throws IOException {
    var format = CSVFormat.RFC4180.builder().setHeader(HEADER).get();
    try (var csv = new CSVPrinter(writer, format)) {
      for (var e : entries)
        csv.printRecord(
            e.getData(),
            e.getTipo(),
            protect(e.getDescricao()),
            protect(e.getCategoria()),
            e.getValor().toPlainString());
    }
  }

  public static List<RegistroFinanceiro> read(Reader reader) throws IOException {
    var format = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).get();
    try (var parser = format.parse(reader)) {
      if (!parser.getHeaderNames().equals(List.of(HEADER)))
        throw new IllegalArgumentException("CSV: date,type,description,category,amount");
      List<RegistroFinanceiro> entries = new ArrayList<>();
      for (var row : parser) {
        try {
          if (row.size() != HEADER.length || entries.size() >= 10000)
            throw new IllegalArgumentException("5 columns; maximum 10000 entries.");
          entries.add(
              new RegistroFinanceiro(
                  restore(row.get(2)),
                  Tipo.valueOf(row.get(1)),
                  new BigDecimal(row.get(4)),
                  restore(row.get(3)),
                  LocalDate.parse(row.get(0))));
        } catch (RuntimeException e) {
          throw new IllegalArgumentException(
              "CSV linha / row " + (row.getRecordNumber() + 1) + ": " + e.getMessage(), e);
        }
      }
      return entries;
    } catch (UncheckedIOException e) {
      throw new IllegalArgumentException("CSV inválido / Invalid CSV", e);
    }
  }
}
