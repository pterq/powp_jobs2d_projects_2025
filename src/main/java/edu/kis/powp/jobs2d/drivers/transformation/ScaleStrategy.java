package edu.kis.powp.jobs2d.drivers.transformation;

public class ScaleStrategy implements TransformStrategy {
    private final double scaleX;
    private final double scaleY;

    public ScaleStrategy(double scaleX, double scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public ScaleStrategy(double scale) {
        this(scale, scale);
    }

    @Override
    public TransformCords transform(TransformCords cords) {
        return new TransformCords((int) (cords.x * scaleX), (int) (cords.y * scaleY));
    }
}