package edu.kis.powp.jobs2d.drivers.maintenance;

public interface InkUsageObserver {
    void updateInkLevel(double currentLevel, double maxLevel);
}