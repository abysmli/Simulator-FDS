package simulator.guis.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Flowmeter extends SimulationComponent {

    private static final long serialVersionUID = 1L;
    private double rate;
    private final String name;

    public Flowmeter(int x, int y, int width, int height, double pipeWidthPorcentage, String name) {
        super(x, y, width, height, pipeWidthPorcentage);
        this.rate = 0.0;
        this.name = name;
    }

    @Override
    protected void paintComponent(Graphics _g) {
        Graphics2D g = (Graphics2D) _g;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g);
        g.setColor(new Color(40, 68, 255));
        g.fillOval(0, 0, this.getWidthComponent() + 1, this.getWidthComponent() + 1);

        g.setColor(Color.black);
        g.drawOval(0, 0, this.getWidthComponent(), this.getWidthComponent());

        g.drawArc((int) (0.25 * this.getWidthComponent()), (int) (-0.25 * this.getWidthComponent()),
                (int) (0.5 * this.getWidthComponent()), (int) (0.5 * this.getWidthComponent()),
                200, 145);
        g.drawArc((int) (0.25 * this.getWidthComponent()), (int) (0.75 * this.getWidthComponent()),
                (int) (0.5 * this.getWidthComponent()), (int) (0.5 * this.getWidthComponent()),
                160, -145);
        // label
        g.setFont(new Font("Ubuntu", Font.PLAIN, 12));
        g.setColor(new Color(51, 51, 51));
        g.drawString(this.name, (int) (this.getWidthComponent() / 2 - g.getFontMetrics().stringWidth(this.name) / 2), 15 + this.getWidthComponent());
        g.drawString(String.valueOf(this.rate), (int) (this.getWidthComponent() / 2 - g.getFontMetrics().stringWidth(String.valueOf(this.rate)) / 2), 30 + this.getWidthComponent());
    }

    public void setFlowRate(double rate) {
        this.rate = rate;
        repaint();
    }

    public double getFlowRate() {
        return this.rate;
    }
}
