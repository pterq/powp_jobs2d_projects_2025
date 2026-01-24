package edu.kis.powp.jobs2d.drivers.maintenance;

import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.visitor.DriverVisitor;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

import java.util.ArrayList;
import java.util.List;

public class InkUsageDriverDecorator implements VisitableJob2dDriver {
    private final Job2dDriver driver;

    private double inkLevel;
    private final double maxInkLevel;

    private int lastX = 0;
    private int lastY = 0;

    private final List<InkUsageObserver> observers = new ArrayList<>();

    public InkUsageDriverDecorator(Job2dDriver driver, double maxInkLevel) {
        this.driver = driver;
        this.maxInkLevel = maxInkLevel;
        this.inkLevel = maxInkLevel;
    }

    @Override
    public void setPosition(int x, int y) {
        this.lastX = x;
        this.lastY = y;
        this.driver.setPosition(x, y);
    }

    @Override
    public void operateTo(int x, int y) {
        double distance = Math.sqrt(Math.pow(x - lastX, 2) + Math.pow(y - lastY, 2));

        if (distance == 0) {
            this.driver.operateTo(x, y);
            return;
        }

        if (inkLevel >= distance) {
            this.driver.operateTo(x, y);
            this.inkLevel -= distance;
        } else if (inkLevel > 0) {
            // running out of ink during drawing

            // part of line we can draw
            double ratio = inkLevel / distance;

            // coords where the ink will run out
            int stopX = (int) (lastX + (x - lastX) * ratio);
            int stopY = (int) (lastY + (y - lastY) * ratio);

            // draw until ink runs out
            this.driver.operateTo(stopX, stopY);

            this.driver.setPosition(x, y);
            this.inkLevel = 0;
        } else {
            // if no ink left - move the head without drawing
            this.driver.setPosition(x, y);
        }

        this.lastX = x;
        this.lastY = y;

        notifyObservers();
    }

    public void refill() {
        this.inkLevel = maxInkLevel;
        notifyObservers();
    }

    public void addObserver(InkUsageObserver observer) {
        this.observers.add(observer);
        observer.updateInkLevel(this.inkLevel, this.maxInkLevel);
    }

    private void notifyObservers() {
        for (InkUsageObserver observer : observers) {
            observer.updateInkLevel(this.inkLevel, this.maxInkLevel);
        }
    }

    @Override
    public String toString() {
        return "Ink depletion simulation";
    }

    @Override
    public void accept(DriverVisitor visitor) {
        if (driver instanceof VisitableJob2dDriver) {
            ((VisitableJob2dDriver) driver).accept(visitor);
        }
    }
}