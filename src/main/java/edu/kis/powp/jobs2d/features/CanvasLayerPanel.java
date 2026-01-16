package edu.kis.powp.jobs2d.features;

import javax.swing.JPanel;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.ILine;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;

/**
 * Transparent overlay that draws canvas boundary.
 */
public class CanvasLayerPanel extends JPanel {

    private final DrawPanelController controller = new DrawPanelController();
    private Job2dDriver driver;

    public CanvasLayerPanel() {
        setOpaque(false);
        controller.initialize(this);
        ILine line = LineFactory.getSpecialLine();
        driver = new LineDriverAdapter(controller, line, "canvas");
    }

    public CanvasLayerPanel(Job2dDriver driver) {
        setOpaque(false);
        controller.initialize(this);
        this.driver = driver;
    }

    public void setDriver(Job2dDriver driver) {
        this.driver = driver;
    }

    public void setCanvas(ICanvas canvas) {
        controller.clearPanel();
        if (canvas != null) {
            canvas.getDriverCommand().execute(driver);
        }
    }

    public DrawPanelController getController() {
        return controller;
    }
}
