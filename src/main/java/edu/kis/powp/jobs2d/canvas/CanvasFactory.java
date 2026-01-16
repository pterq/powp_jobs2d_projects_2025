package edu.kis.powp.jobs2d.canvas;

public class CanvasFactory {

    public static ICanvas createA4() {
        return new RectangularCanvas(210, 297, "A4");
    }

    public static ICanvas createA3() {
        return new RectangularCanvas(297, 420, "A3");
    }

    public static ICanvas createB4() {
        return new RectangularCanvas(250, 353, "B4");
    }

    public static ICanvas createRectangle(int width, int height) {
        return new RectangularCanvas(width, height, "Rectangle " + width + "x" + height);
    }

    public static ICanvas createCircle(int radius) {
        return new CircleCanvas(radius);
    }

    public static ICanvas createCircle(int radius, int segments) {
        return new CircleCanvas(radius, segments);
    }
}

