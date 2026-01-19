package edu.kis.powp.jobs2d.visitor;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.ILine;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.jobs2d.Job2dDriver;
import edu.kis.powp.jobs2d.drivers.AnimatedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.DriverComposite;
import edu.kis.powp.jobs2d.drivers.LoggerDriver;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;

import java.util.ArrayList;

public class DeepCopyMainTest {
    public static void main(String[] args) {
        testLoggerDriverDeepCopy();
        testLineDriverAdapterDeepCopy();
        testDriverCompositeDeepCopy();
        testAnimatedDriverDecoratorDeepCopy();
        System.out.println("ALL TESTS PASSED");
    }

    private static void testLoggerDriverDeepCopy() {
        System.out.println("Testing LoggerDriver Deep Copy...");
        LoggerDriver driver = new LoggerDriver();
        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        driver.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        if (!(copy instanceof LoggerDriver)) throw new RuntimeException("Copy is not LoggerDriver");
        if (driver == copy) throw new RuntimeException("Copy is same instance as original");
        System.out.println("LoggerDriver OK");
    }

    private static void testLineDriverAdapterDeepCopy() {
        System.out.println("Testing LineDriverAdapter Deep Copy...");
        DrawPanelController controller = new DrawPanelController();
        ILine line = LineFactory.getBasicLine();
        LineDriverAdapter driver = new LineDriverAdapter(controller, line, "test");
        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        driver.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        if (!(copy instanceof LineDriverAdapter)) throw new RuntimeException("Copy is not LineDriverAdapter");
        if (driver == copy) throw new RuntimeException("Copy is same instance as original");
        
        LineDriverAdapter copyAdapter = (LineDriverAdapter) copy;
        if (driver.getDrawController() != copyAdapter.getDrawController()) throw new RuntimeException("DrawController should be shared");
        if (driver.getLine() == copyAdapter.getLine()) throw new RuntimeException("ILine should be cloned (deep copy)");
        if (!driver.getName().equals(copyAdapter.getName())) throw new RuntimeException("Name mismatch");
        System.out.println("LineDriverAdapter OK");
    }

    private static void testDriverCompositeDeepCopy() {
        System.out.println("Testing DriverComposite Deep Copy...");
        DriverComposite composite = new DriverComposite(new ArrayList<>());
        LoggerDriver child1 = new LoggerDriver();
        composite.add(child1);

        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        composite.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        if (!(copy instanceof DriverComposite)) throw new RuntimeException("Copy is not DriverComposite");
        if (composite == copy) throw new RuntimeException("Copy is same instance as original");
        System.out.println("DriverComposite OK");
    }

    private static void testAnimatedDriverDecoratorDeepCopy() {
        System.out.println("Testing AnimatedDriverDecorator Deep Copy...");
        LoggerDriver target = new LoggerDriver();
        AnimatedDriverDecorator driver = new AnimatedDriverDecorator(target);
        DriverDeepCopyVisitor visitor = new DriverDeepCopyVisitor();
        driver.accept(visitor);
        Job2dDriver copy = visitor.getCopy();

        if (!(copy instanceof AnimatedDriverDecorator)) throw new RuntimeException("Copy is not AnimatedDriverDecorator");
        if (driver == copy) throw new RuntimeException("Copy is same instance as original");
        AnimatedDriverDecorator copyDecorator = (AnimatedDriverDecorator) copy;
        if (driver.getTargetDriver() == copyDecorator.getTargetDriver()) throw new RuntimeException("Target driver should be deep copied");
        if (!(copyDecorator.getTargetDriver() instanceof LoggerDriver)) throw new RuntimeException("Target driver type mismatch");
        System.out.println("AnimatedDriverDecorator OK");
    }
}

