package edu.kis.powp.jobs2d.canvas;

import edu.kis.powp.jobs2d.command.CompoundCommand;
import edu.kis.powp.jobs2d.command.DriverCommand;

/**
 * Circular canvas.
 */
public class CircleCanvas implements ICanvas {
    
    private final int radius;
    private final int segments;
    private final String name;
    
    public CircleCanvas(int radius, int segments) {
        this.radius = radius;
        this.segments = segments;
        this.name = "Circle r=" + radius;
    }
    
    public CircleCanvas(int radius) {
        this(radius, 64);
    }
    
    @Override
    public DriverCommand getDriverCommand() {
        CompoundCommand.Builder builder = CompoundCommand.builder().setName(name);

        int startX = (int) (radius * Math.cos(0));
        int startY = (int) (radius * Math.sin(0));
        builder.addSetPosition(startX, startY);
        
        for (int i = 1; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            int x = (int) (radius * Math.cos(angle));
            int y = (int) (radius * Math.sin(angle));
            builder.addOperateTo(x, y);
        }
        
        return builder.build();
    }
    
    @Override
    public boolean containsPoint(int x, int y) {
        return x * x + y * y <= radius * radius;
    }
    
    @Override
    public String getName() {
        return name;
    }
}

