package edu.kis.powp.jobs2d.drivers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.SwingUtilities;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;
import edu.kis.powp.jobs2d.visitor.DriverVisitor;

public class AnimatedDriverDecorator implements VisitableJob2dDriver {
    private static final int DEFAULT_DELAY_MS = 100;
    
    private final VisitableJob2dDriver targetDriver;
    private volatile int delayMs;
    private final BlockingQueue<Runnable> operationQueue;
    private final Thread executionThread;
    private volatile boolean running = true;

    public AnimatedDriverDecorator(VisitableJob2dDriver targetDriver) {
        this(targetDriver, DEFAULT_DELAY_MS);
    }

    public AnimatedDriverDecorator(VisitableJob2dDriver targetDriver, int delayMs) {
        this.targetDriver = targetDriver;
        this.delayMs = delayMs;
        this.operationQueue = new LinkedBlockingQueue<>();
        this.executionThread = new Thread(this::processOperations);
        this.executionThread.setDaemon(true);
        this.executionThread.start();
    }

    @Override
    public void setPosition(int x, int y) {
        queueOperation(() -> targetDriver.setPosition(x, y));
    }

    @Override
    public void operateTo(int x, int y) {
        queueOperation(() -> targetDriver.operateTo(x, y));
    }

    private void queueOperation(Runnable operation) {
        try {
            operationQueue.put(operation);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processOperations() {
        while (running) {
            try {
                Runnable operation = operationQueue.take();
                Thread.sleep(delayMs);
                SwingUtilities.invokeLater(operation);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void setSpeedFast() {
        this.delayMs = 50;
    }

    public void setSpeedMedium() {
        this.delayMs = 200;
    }

    public void setSpeedSlow() {
        this.delayMs = 400;
    }

    @Override
    public void accept(DriverVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Animated " + targetDriver.toString();
    }
}