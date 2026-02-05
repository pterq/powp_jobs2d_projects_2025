package edu.kis.powp.jobs2d.drivers.extension;

import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;
import edu.kis.powp.jobs2d.drivers.transformation.DriverFeatureFactory;

/**
 * Transformation extension - applies rotation, scale, or flip.
 */
public class TransformationExtension implements IDriverExtension {
    private String name;
    private int rotationDegrees;
    private double scale;
    private boolean flipH, flipV;
    private boolean enabled = true;
    private Type type;

    public enum Type {
        ROTATE, SCALE, FLIP
    }

    private TransformationExtension(String name) {
        this.name = name;
    }

    public static TransformationExtension createRotation(int degrees) {
        TransformationExtension ext = new TransformationExtension("Rotate " + degrees + "°");
        ext.rotationDegrees = degrees;
        ext.type = Type.ROTATE;
        return ext;
    }

    public static TransformationExtension createScale(double factor) {
        TransformationExtension ext = new TransformationExtension("Scale " + factor + "x");
        ext.scale = factor;
        ext.type = Type.SCALE;
        return ext;
    }

    public static TransformationExtension createFlip(boolean horizontal, boolean vertical) {
        String dir = horizontal ? "H" : "V";
        TransformationExtension ext = new TransformationExtension("Flip " + dir);
        ext.flipH = horizontal;
        ext.flipV = vertical;
        ext.type = Type.FLIP;
        return ext;
    }

    @Override
    public VisitableJob2dDriver apply(VisitableJob2dDriver driver) {
        if (!enabled) return driver;
        
        if (type == Type.ROTATE) {
            return DriverFeatureFactory.createRotateDriver(driver, rotationDegrees);
        } else if (type == Type.SCALE) {
            return DriverFeatureFactory.createScaleDriver(driver, scale);
        } else if (type == Type.FLIP) {
            return DriverFeatureFactory.createFlipDriver(driver, flipH, flipV);
        }
        return driver;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
