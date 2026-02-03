package edu.kis.powp.jobs2d.drivers.extension;

import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

/**
 * Simple interface for driver extensions.
 */
public interface IDriverExtension {
    VisitableJob2dDriver apply(VisitableJob2dDriver driver);
    String getName();
    void setEnabled(boolean enabled);
    boolean isEnabled();
}
