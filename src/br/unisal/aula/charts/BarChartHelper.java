package br.unisal.aula.charts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChartHelper {
    
    private BarChartHelper() {
    }

    public static JFreeChart criarGrafico(String[] legendas, double[] valores) {

        DefaultCategoryDataset dados = new DefaultCategoryDataset();
        for (int i = 0; i < valores.length; i++) {
            dados.addValue(valores[i], legendas[i], "");
        }

        JFreeChart grafico = ChartFactory.createBarChart(
                null, null, null, // titulo, legenda X, legenda Y
                dados, PlotOrientation.VERTICAL,
                true, true, false); // legenda, dicas, urls

        CategoryPlot desenho = grafico.getCategoryPlot();
        desenho.setBackgroundPaint(new Color(236, 240, 241));

        Paint[] colors = {new Color(39, 174, 96), new Color(192, 57, 43), new Color(41, 128, 185)};
        for (int i = 0; i < colors.length; i++) {
            desenho.getRenderer().setSeriesPaint(i, colors[i]);
        }

        return grafico;
    }
    
    public static void setGraficoNoPainel(JFreeChart grafico, JPanel painel){
        ChartPanel painelGrafico = new ChartPanel(grafico);

        painel.removeAll();
        painel.setLayout(new BorderLayout());
        painel.add(painelGrafico, BorderLayout.CENTER);
        painel.validate();
    }
    
    public static void save(String nomeArquivo, String[] legendas, double[] valores){
        
        JFreeChart grafico = criarGrafico(legendas, valores);
        
        try {
            OutputStream arquivo = new FileOutputStream(nomeArquivo);
            ChartUtilities.writeChartAsPNG(arquivo, grafico, 500, 350);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
