package edu.kis.powp.jobs2d;

import edu.kis.powp.jobs2d.visitor.DriverVisitor;

class MockDriver implements Job2dDriver {

    int x = 0;
    int y = 0;

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void operateTo(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
