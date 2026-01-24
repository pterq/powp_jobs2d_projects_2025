package edu.kis.powp.jobs2d.drivers.maintenance;

public interface ServiceObserver {
    void updateServiceState(int usageCount, int maxUsage);
}