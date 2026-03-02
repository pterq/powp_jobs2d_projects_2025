package edu.kis.powp.jobs2d.drivers;

import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;
import edu.kis.powp.jobs2d.drivers.extension.DriverExtensionManager;
import edu.kis.powp.observer.Publisher;

/**
 * Driver manager provides means to setup the driver. It also enables other
 * components and features of the application to react on configuration changes.
 * Extensions can be applied independently of the selected driver.
 */
public class DriverManager {

    private VisitableJob2dDriver baseDriver = new LoggerDriver();
    private VisitableJob2dDriver currentDriver = new LoggerDriver();
    private final Publisher changePublisher = new Publisher();
    private final DriverExtensionManager extensionManager = new DriverExtensionManager();

    /**
     * @param driver Set the driver as current (base driver without extensions).
     */
    public synchronized void setCurrentDriver(VisitableJob2dDriver driver) {
        baseDriver = driver;
        updateCurrentDriver();
    }

    /**
     * @return Current driver with extensions applied.
     */
    public synchronized VisitableJob2dDriver getCurrentDriver() {
        return currentDriver;
    }

    /**
     * @return Base driver without extensions.
     */
    public synchronized VisitableJob2dDriver getBaseDriver() {
        return baseDriver;
    }

    /**
     * Get the extension manager.
     * @return DriverExtensionManager instance.
     */
    public DriverExtensionManager getExtensionManager() {
        return extensionManager;
    }

    /**
     * Update current driver by applying extensions to base driver.
     */
    public synchronized void updateExtensionsApplied() {
        updateCurrentDriver();
    }

    private synchronized void updateCurrentDriver() {
        currentDriver = extensionManager.applyExtensions(baseDriver);
        changePublisher.notifyObservers();
    }

    public Publisher getChangePublisher() {
        return changePublisher;
    }
}

