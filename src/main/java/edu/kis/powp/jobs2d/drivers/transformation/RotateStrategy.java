package edu.kis.powp.jobs2d.drivers.transformation;

public class RotateStrategy implements TransformStrategy {
    private final double angleRad;

    public RotateStrategy(double angleDegrees) {
        this.angleRad = Math.toRadians(angleDegrees);
    }

    @Override
    public TransformCords transform(TransformCords cords) {
        int newX = (int) (cords.x * Math.cos(angleRad) - cords.y * Math.sin(angleRad));
        int newY = (int) (cords.x * Math.sin(angleRad) + cords.y * Math.cos(angleRad));
        return new TransformCords(newX, newY);
    }
}