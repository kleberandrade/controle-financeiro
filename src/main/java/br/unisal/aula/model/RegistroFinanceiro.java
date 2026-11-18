package br.unisal.aula.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "entries")
public class RegistroFinanceiro {
  @Id
  @SequenceGenerator(name = "entry_ids", sequenceName = "entries_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "entry_ids")
  private Long id;

  @Column(name = "description", nullable = false, length = 160)
  private String descricao;

  @Enumerated(EnumType.STRING)
  @Column(name = "kind", nullable = false, length = 7)
  private Tipo tipo;

  @Column(name = "amount", nullable = false, precision = 14, scale = 2)
  private BigDecimal valor;

  @Column(name = "category", nullable = false, length = 60)
  private String categoria;

  @Column(name = "entry_date", nullable = false)
  private LocalDate data;

  protected RegistroFinanceiro() {}

  public RegistroFinanceiro(
      String descricao, Tipo tipo, BigDecimal valor, String categoria, LocalDate data) {
    this.descricao = text(descricao, 160, "Descrição / Description");
    this.categoria = text(categoria, 60, "Categoria / Category");
    this.tipo = Objects.requireNonNull(tipo, "Tipo / Type");
    this.data = Objects.requireNonNull(data, "Data / Date");
    if (data.getYear() < 1 || data.getYear() > 9999)
      throw new IllegalArgumentException("Ano / Year: 0001–9999");
    this.valor = money(valor, false);
  }

  public static String text(String value, int max, String label) {
    if (value == null || value.isBlank() || value.strip().length() > max || value.indexOf(0) >= 0)
      throw new IllegalArgumentException(label + ": 1–" + max + " caracteres / characters.");
    return value.strip();
  }

  public static BigDecimal money(BigDecimal value, boolean zeroAllowed) {
    if (value == null
        || value.signum() < 0
        || (!zeroAllowed && value.signum() == 0)
        || value.compareTo(new BigDecimal("999999999999.99")) > 0)
      throw new IllegalArgumentException(
          "Valor / Amount: " + (zeroAllowed ? "0" : "0.01") + " – 999999999999.99");
    try {
      return value.setScale(2, RoundingMode.UNNECESSARY);
    } catch (ArithmeticException e) {
      throw new IllegalArgumentException(
          "Use até 2 casas decimais / Use at most 2 decimal places.", e);
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescricao() {
    return descricao;
  }

  public Tipo getTipo() {
    return tipo;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public String getCategoria() {
    return categoria;
  }

  public LocalDate getData() {
    return data;
  }
}
