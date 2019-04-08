package br.unisal.aula.helpers;

import javax.swing.JOptionPane;

public final class MensagemHelper {

    public static void exibir(String texto) {
        JOptionPane.showMessageDialog(null, texto);
    }

    public static void exibirWarning(String titulo, String texto) {
        JOptionPane.showMessageDialog(null,
                texto, titulo, JOptionPane.WARNING_MESSAGE);
    }

    public static void exibirError(String titulo, String texto) {
        JOptionPane.showMessageDialog(null,
                texto, titulo, JOptionPane.ERROR);
    }
}
