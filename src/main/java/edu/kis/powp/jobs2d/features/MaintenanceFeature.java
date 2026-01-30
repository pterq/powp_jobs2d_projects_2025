package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.drivers.maintenance.DeviceMaintenancePanel;
import edu.kis.powp.jobs2d.drivers.maintenance.DeviceUsageMonitor;
import edu.kis.powp.jobs2d.drivers.maintenance.MaintenanceDriverConfigurationStrategy;
import edu.kis.powp.jobs2d.drivers.maintenance.UsageTrackingDriverDecorator;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

public class MaintenanceFeature {
    /**
    * Facade method to simplify adding a maintenance-enabled driver .
    */

    public static void setup(Application application, VisitableJob2dDriver job2dDriver, double maxInkLevel, int maxOperations, int inkThresholdWarning) {
        // Temporary default strategy change to maintenance strategy
        DriverConfigurationStrategy defaultStrategy = DriverFeature.getConfigurationStrategy();
        MaintenanceDriverConfigurationStrategy maintenanceStrategy = new MaintenanceDriverConfigurationStrategy(maxInkLevel, maxOperations);

        DriverFeature.setConfigurationStrategy(maintenanceStrategy);

        String driverName = job2dDriver.toString();
        DriverFeature.addDriver("Device Maintenance simulation (driver: " + driverName + ")", job2dDriver);

        UsageTrackingDriverDecorator maintenanceDriver = maintenanceStrategy.getCreatedDecorator();
        DeviceMaintenancePanel devicePanel = new DeviceMaintenancePanel(
                e -> maintenanceDriver.refillInk(),
                e -> maintenanceDriver.performMaintenance()
        );

        application.addWindowComponent("Device Maintenance", devicePanel);

        DeviceUsageMonitor monitor = new DeviceUsageMonitor(devicePanel, inkThresholdWarning);
        maintenanceDriver.addObserver(monitor);

        // Bring back default strategy
        DriverFeature.setConfigurationStrategy(defaultStrategy);
    }
}
