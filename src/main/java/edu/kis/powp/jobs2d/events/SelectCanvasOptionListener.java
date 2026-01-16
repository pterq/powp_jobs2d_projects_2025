package edu.kis.powp.jobs2d.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.kis.powp.jobs2d.canvas.ICanvas;
import edu.kis.powp.jobs2d.features.CanvasFeature;

public class SelectCanvasOptionListener implements ActionListener {

    private final ICanvas canvas;

    public SelectCanvasOptionListener(ICanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CanvasFeature.getCanvasManager().setCurrentCanvas(canvas);
    }
}

