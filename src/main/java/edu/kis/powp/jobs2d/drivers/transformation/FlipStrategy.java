package edu.kis.powp.jobs2d.drivers.transformation;

public class FlipStrategy implements TransformStrategy {
    private final boolean flipHorizontal;
    private final boolean flipVertical;

    public FlipStrategy(boolean flipHorizontal, boolean flipVertical) {
        this.flipHorizontal = flipHorizontal;
        this.flipVertical = flipVertical;
    }

    @Override
    public TransformCords transform(TransformCords cords) {
        int newX = flipHorizontal ? -cords.x : cords.x;
        int newY = flipVertical ? -cords.y : cords.y;
        return new TransformCords(newX, newY);
    }
}