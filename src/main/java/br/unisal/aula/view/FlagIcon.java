package br.unisal.aula.view;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.Icon;

/** Locally drawn circular flags: no network assets. */
public record FlagIcon(boolean brazil) implements Icon {
  public int getIconWidth() {
    return 24;
  }

  public int getIconHeight() {
    return 24;
  }

  public void paintIcon(Component c, Graphics graphics, int x, int y) {
    var g = (Graphics2D) graphics.create();
    g.translate(x, y);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.clip(new Ellipse2D.Double(0, 0, 24, 24));
    g.setColor(new Color(brazil ? 0x168447 : 0x17336B));
    g.fillRect(0, 0, 24, 24);
    if (brazil) {
      g.setColor(new Color(0xFFDA44));
      g.fillPolygon(new int[] {2, 12, 22, 12}, new int[] {12, 4, 12, 20}, 4);
      g.setColor(new Color(0x194A9A));
      g.fillOval(7, 7, 10, 10);
      g.setColor(Color.WHITE);
      g.drawArc(7, 10, 10, 7, 30, 130);
    } else {
      g.setStroke(new BasicStroke(5));
      g.setColor(Color.WHITE);
      g.drawLine(0, 0, 24, 24);
      g.drawLine(24, 0, 0, 24);
      g.setStroke(new BasicStroke(2));
      g.setColor(new Color(0xC82039));
      g.drawLine(0, 0, 24, 24);
      g.drawLine(24, 0, 0, 24);
      g.setColor(Color.WHITE);
      g.fillRect(8, 0, 8, 24);
      g.fillRect(0, 8, 24, 8);
      g.setColor(new Color(0xC82039));
      g.fillRect(10, 0, 4, 24);
      g.fillRect(0, 10, 24, 4);
    }
    g.dispose();
  }
}
