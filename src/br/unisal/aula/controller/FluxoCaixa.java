package br.unisal.aula.controller;

import br.unisal.aula.model.RegistroFinanceiro;
import java.util.List;

public class FluxoCaixa {
    
    private double despesa;
    private double receita;
    private static final String DESPESA = "DESPESA";
    private static final String RECEITA = "RECEITA";
    private static final int INSERIR = 1;
    private static final int REMOVER = -1;
    
    public FluxoCaixa(List<RegistroFinanceiro> registros) {
        atualizarRegistrosFinanceiro(registros);
    }
    
    public void atualizarRegistrosFinanceiro(List<RegistroFinanceiro> registros) {
        receita = despesa = 0;
        for (RegistroFinanceiro registroFinanceiro : registros) {
            inserirRegistroFinanceiro(registroFinanceiro);
        }
    }
    
    /**
     * Inserir novo registro na tabela
     * Exemplo: fluxoCaixa.inserirRemoverResgistro(registro, -1);
     * @param registro é um registro financeiro
     * @param sinal despesa (-1) e receita (+1)
     */
    public void inserirRemoverResgistro(RegistroFinanceiro registro, int sinal) {
        if (DESPESA.equals(registro.getTipo().toUpperCase())) {
            despesa += registro.getValor() * sinal;
        } else {
            receita += registro.getValor() * sinal;
        }
    }
    
    public void inserirRegistroFinanceiro(RegistroFinanceiro registro) {
        inserirRemoverResgistro(registro, INSERIR);
    }
    
    public void removerRegistroFinanceiro(RegistroFinanceiro registro) {
        inserirRemoverResgistro(registro, REMOVER);
    }
    
    public double getDespesa() {
        return despesa;
    }
    
    public double getReceita() {
        return receita;
    }
    
    public double getSaldo() {
        return receita - despesa;
    }
}
