package edu.kis.powp.jobs2d.visitor;

import edu.kis.powp.jobs2d.Job2dDriver;

/**
 * Interface for drivers that can be visited using the Visitor pattern.
 */
public interface VisitableJob2dDriver extends Job2dDriver {
    /**
     * Accepts a visitor and calls the appropriate visit method.
     * @param visitor the visitor to accept
     */
    void accept(DriverVisitor visitor);
}