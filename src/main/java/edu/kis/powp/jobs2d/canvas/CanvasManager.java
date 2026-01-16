package edu.kis.powp.jobs2d.canvas;

import edu.kis.powp.observer.Publisher;

/**
 * Manages the current canvas.
 */
public class CanvasManager {
    
    private ICanvas currentCanvas = null;
    private final Publisher changePublisher = new Publisher();
    
    public void setCurrentCanvas(ICanvas canvas) {
        this.currentCanvas = canvas;
        changePublisher.notifyObservers();
    }
    
    public ICanvas getCurrentCanvas() {
        return currentCanvas;
    }
    
    public Publisher getChangePublisher() {
        return changePublisher;
    }
}
