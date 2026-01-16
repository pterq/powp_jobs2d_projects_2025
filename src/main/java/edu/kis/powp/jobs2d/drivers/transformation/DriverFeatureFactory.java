package edu.kis.powp.jobs2d.drivers.transformation;

import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

public class DriverFeatureFactory {

    public static VisitableJob2dDriver createRotateDriver(VisitableJob2dDriver baseDriver, double angle) {
        return new TransformerDriverDecorator(baseDriver, new RotateStrategy(angle));
    }

    public static VisitableJob2dDriver createScaleDriver(VisitableJob2dDriver baseDriver, double scale) {
        return new TransformerDriverDecorator(baseDriver, new ScaleStrategy(scale));
    }

    public static VisitableJob2dDriver createFlipDriver(VisitableJob2dDriver baseDriver, boolean flipX, boolean flipY) {
        return new TransformerDriverDecorator(baseDriver, new FlipStrategy(flipX, flipY));
    }
}