package br.unisal.aula.tables;

import br.unisal.aula.model.RegistroFinanceiro;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class RegistroFinanceiroTableModel extends AbstractTableModel {

    public static final int COL_ID = 0;
    public static final int COL_DESCRICAO = 1;
    public static final int COL_TIPO = 2;
    public static final int COL_VALOR = 3;
    public static final int COL_CATEGORIA = 4;
    public static final int COL_DATA = 5;
    
    private static final String[] NOME_COLUNAS = {
        "Id", "Descrição", "Tipo", "Valor (R$)", "Categoria", "Data"
    };
    
    private List<RegistroFinanceiro> lista;

    public RegistroFinanceiroTableModel(List<RegistroFinanceiro> lista) {
        this.lista = lista;
    }

    @Override
    public int getRowCount() {
        return this.lista.size();
    }

    @Override
    public int getColumnCount() {
        return NOME_COLUNAS.length;
    }
    
    @Override
    public Object getValueAt(int linha, int coluna) {
        RegistroFinanceiro registro = lista.get(linha);
        switch(coluna){
            case COL_ID:
                return registro.getId();
            case COL_DATA:
                return registro.getData();
            case COL_DESCRICAO:
                return registro.getDescricao();
            case COL_CATEGORIA:
                return registro.getCategoria();
            case COL_VALOR:
                return registro.getValor();
            case COL_TIPO:
                return registro.getTipo();
            default:
                break;
        }
        return null;
    }
    
    public void adicionar(RegistroFinanceiro registro){
        lista.add(registro);
        int ultimaLinha = getRowCount() - 1;
        fireTableRowsInserted(ultimaLinha, ultimaLinha);
    }
    
    public void atualizar(RegistroFinanceiro registro, int linha){
        lista.set(linha, registro);
        fireTableRowsUpdated(linha, linha);
    }
    
    public RegistroFinanceiro excluir(int linha){
        RegistroFinanceiro registro = lista.remove(linha);
        fireTableRowsDeleted(linha, linha);
        return registro;
    }
    
    public RegistroFinanceiro getRegistroFinanceiro(int linha){
        return lista.get(linha);
    }

    @Override
    public String getColumnName(int i) {
        return NOME_COLUNAS[i];
    }
    
    
}
