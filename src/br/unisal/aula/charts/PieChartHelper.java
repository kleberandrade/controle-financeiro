package br.unisal.aula.charts;

import br.unisal.aula.model.RegistroFinanceiro;
import java.awt.BorderLayout;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class PieChartHelper {

    private PieChartHelper() {
    }

    public static JFreeChart criarGrafico(List<RegistroFinanceiro> lista, String[] categorias) {
        // Chave --> Valor
        HashMap<String, Double> mapa = new HashMap<>();
        for (int i = 0; i < categorias.length; i++) {
            mapa.put(categorias[i], 0.0);
        }

        for (int i = 0; i < lista.size(); i++) {
            RegistroFinanceiro item = lista.get(i);
            if (item.getTipo().toUpperCase().equals("DESPESA")) {
                String categoria = item.getCategoria();
                Double valorItem = item.getValor();
                Double valorAtual = mapa.get(categoria);
                valorAtual += valorItem;
                mapa.put(categoria, valorAtual);
            }
        }

        DefaultPieDataset dados = new DefaultPieDataset();
        for (int i = 0; i < categorias.length; i++) {
            String categoria = categorias[i];
            dados.setValue(categoria, mapa.get(categoria));
        }

        JFreeChart grafico = ChartFactory.createPieChart("Despesas x Categorias", dados, true, true, false);

        PieSectionLabelGenerator label = new StandardPieSectionLabelGenerator(
                "{0} : ({2})",
                new DecimalFormat("0"),
                new DecimalFormat("0%"));

        PiePlot desenho = (PiePlot) grafico.getPlot();
        desenho.setLabelGenerator(label);

        return grafico;
    }

    public static void setGraficoNoPainel(JFreeChart grafico, JPanel painel) {
        ChartPanel painelGrafico = new ChartPanel(grafico);
        painel.removeAll();
        painel.setLayout(new BorderLayout());
        painel.add(painelGrafico, BorderLayout.CENTER);
        painel.validate();
    }

    public static void save(String nomeArquivo, ArrayList<RegistroFinanceiro> lista, String[] categorias) {

        JFreeChart grafico = criarGrafico(lista, categorias);

        try {
            OutputStream arquivo = new FileOutputStream(nomeArquivo);
            ChartUtilities.writeChartAsPNG(arquivo, grafico, 500, 350);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
