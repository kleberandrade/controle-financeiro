package br.unisal.aula.helpers;

public final class ValidarHelper {

    public static String validarString(String texto, String campo) throws Exception {
        if (texto == null) {
            throw new Exception(campo + " nulo.");
        }

        if (texto.isEmpty()) {
            throw new Exception(campo + " vazio.");
        }

        return texto;
    }

    public static double validarDouble(String texto, String campo) throws Exception {
        double valor = 0.0;
        try {
            texto = texto.replace(",", ".");
            valor = Double.parseDouble(texto);
        } catch (NumberFormatException ex) {
            throw new Exception(campo + " com formato inválido.");
        }
        return valor;
    }
}
