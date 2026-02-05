package edu.kis.powp.jobs2d.drivers.extension;

import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;
import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import java.util.logging.Logger;

/**
 * Extension that wraps driver with a LoggerDriver decorator.
 */
public class LoggerExtension implements IDriverExtension {
    private boolean enabled = false;
    private Logger logger;

    public LoggerExtension(Logger logger) {
        this.logger = logger;
    }

    @Override
    public VisitableJob2dDriver apply(VisitableJob2dDriver driver) {
        if (!enabled) {
            return driver;
        }
        // Create a LoggerDriver that wraps the given driver by delegating to it
        return new LoggerDriverDecorator(logger, driver);
    }

    @Override
    public String getName() {
        return "logged";
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Inner class: LoggerDriver decorator that logs operations and delegates to wrapped driver.
     */
    private static class LoggerDriverDecorator extends LoggerDriver {
        private final VisitableJob2dDriver targetDriver;

        public LoggerDriverDecorator(Logger logger, VisitableJob2dDriver targetDriver) {
            super(logger);
            this.targetDriver = targetDriver;
        }

        @Override
        public void operateTo(int x, int y) {
            super.operateTo(x, y);
            targetDriver.operateTo(x, y);
        }

        @Override
        public void setPosition(int x, int y) {
            super.setPosition(x, y);
            targetDriver.setPosition(x, y);
        }
    }
}

