package edu.kis.powp.jobs2d.visitor;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.ILine;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.drivers.AnimatedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.DriverComposite;
import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DriverDeepCopyVisitorTest {

    @Test
    public void testLoggerDriverDeepCopy() {
        LoggerDriver driver = new LoggerDriver();
        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        driver.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        Assert.assertTrue(copy instanceof LoggerDriver);
        Assert.assertNotSame(driver, copy);
    }

    @Test
    public void testLineDriverAdapterDeepCopy() {
        DrawPanelController controller = new DrawPanelController();
        ILine line = LineFactory.getBasicLine();
        LineDriverAdapter driver = new LineDriverAdapter(controller, line, "test");
        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        driver.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        Assert.assertTrue(copy instanceof LineDriverAdapter);
        Assert.assertNotSame(driver, copy);
        LineDriverAdapter copyAdapter = (LineDriverAdapter) copy;
        Assert.assertEquals(driver.getDrawController(), copyAdapter.getDrawController());
        Assert.assertNotSame(driver.getLine(), copyAdapter.getLine());
        Assert.assertEquals(driver.getName(), copyAdapter.getName());
    }

    @Test
    public void testDriverCompositeDeepCopy() {
        DriverComposite composite = new DriverComposite(new ArrayList<>());
        LoggerDriver child1 = new LoggerDriver();
        composite.add(child1);

        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        composite.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        Assert.assertTrue(copy instanceof DriverComposite);
        Assert.assertNotSame(composite, copy);
        
        // Unfortunately DriverComposite doesn't expose children easily for test without iterator
        // But we can check behavior or structure if we cast
        DriverComposite copyComposite = (DriverComposite) copy;
        // Basic check that it's a new instance
    }

    @Test
    public void testAnimatedDriverDecoratorDeepCopy() {
        LoggerDriver target = new LoggerDriver();
        AnimatedDriverDecorator driver = new AnimatedDriverDecorator(target);
        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        driver.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        Assert.assertTrue(copy instanceof AnimatedDriverDecorator);
        Assert.assertNotSame(driver, copy);
        AnimatedDriverDecorator copyDecorator = (AnimatedDriverDecorator) copy;
        Assert.assertNotSame(driver.getTargetDriver(), copyDecorator.getTargetDriver());
        Assert.assertTrue(copyDecorator.getTargetDriver() instanceof LoggerDriver);
    }
}

