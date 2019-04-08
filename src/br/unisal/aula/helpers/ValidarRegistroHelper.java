package br.unisal.aula.helpers;

import br.unisal.aula.model.RegistroFinanceiro;
import java.util.Date;

public final class ValidarRegistroHelper {

    public static RegistroFinanceiro criarRegistroFinanceiro(
            String descricao,
            String tipo,
            String valorStr,
            String categoria,
            Date data) throws Exception {

        descricao = ValidarHelper.validarString(descricao, "Descrição");
        tipo = ValidarHelper.validarString(tipo, "Tipo");
        double valor = ValidarHelper.validarDouble(valorStr, "Valor");
        categoria = ValidarHelper.validarString(categoria, "Categoria");
        
        return new RegistroFinanceiro(0, descricao, tipo, valor, categoria, data);
    }
}
