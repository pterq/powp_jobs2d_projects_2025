package edu.kis.powp.jobs2d.features;

import java.util.logging.Logger;

import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.drivers.AnimatedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.CanvasLimitedDriverDecorator;
import edu.kis.powp.jobs2d.drivers.DriverManager;
import edu.kis.powp.jobs2d.drivers.RecordingDriverDecorator;
import edu.kis.powp.jobs2d.drivers.SelectDriverMenuOptionListener;
import edu.kis.powp.jobs2d.drivers.adapter.LineDriverAdapter;
import edu.kis.powp.jobs2d.drivers.strategy.OnCanvasExceededLogWarning;
import edu.kis.powp.jobs2d.drivers.strategy.OnCanvasExceededStrategy;
import edu.kis.powp.jobs2d.events.SelectLoadRecordedCommandOptionListener;
import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;

public class DriverFeature implements IFeature {

    private static DriverManager driverManager = new DriverManager();
    private static Application app;
    private Logger logger;

    private DriverFeature() {
    }

    public DriverFeature(Logger logger) {
        this.logger = logger;
    }

    public static DriverManager getDriverManager() {
        return driverManager;
    }

    private static DriverConfigurationStrategy configStrategy = (name, driver) -> driver;

    @Override
    public void setup(Application application) {
        app = application;
        setupDriverPlugin(application);
        setupDefaultDrivers(app, logger);
    }

    /**
     * Setup jobs2d drivers Plugin and add to application.
     *
     * @param application Application context.
     */
    public static void setupDriverPlugin(Application application) {
        app = application;
        app.addComponentMenu(DriverFeature.class, "Drivers");
        driverManager.getChangePublisher().addSubscriber(DriverFeature::updateDriverInfo);
    }

    /**
     * Add driver to context, create button in driver menu.
     *
     * @param name   Button name.
     * @param driver VisitableJob2dDriver object.
     */
    public static void addDriver(String name, VisitableJob2dDriver driver) {
        VisitableJob2dDriver finalDriver = configStrategy.configure(name, driver);

        SelectDriverMenuOptionListener listener = new SelectDriverMenuOptionListener(finalDriver, driverManager);
        app.addComponentMenuElement(DriverFeature.class, name, listener);
    }

    /**
     * Update driver info with current driver name and enabled extensions.
     */
    public static void updateDriverInfo() {
        String driverName = driverManager.getCurrentDriver().toString();

        app.updateInfo(driverName);
    }

    public static void setConfigurationStrategy(DriverConfigurationStrategy strategy) {
        configStrategy = strategy;
    }

    public static DriverConfigurationStrategy getConfigurationStrategy() {
        return configStrategy;
    }

    @Override
    public String getName() {
        return "Driver";
    }

    /**
     * Setup driver manager, and set default VisitableJob2dDriver for application.
     *
     * @param application Application context.
     */
    public static void setupDefaultDrivers(Application application, Logger logger) {
        DriverFeature.setConfigurationStrategy(new MonitoringDriverConfigurationStrategy());

        DrawPanelController drawerController = DrawerFeature.getDrawerController();
        VisitableJob2dDriver basicLineDriver = new LineDriverAdapter(drawerController, LineFactory.getBasicLine(),
                "basic");
        DriverFeature.addDriver("Basic line Simulator", basicLineDriver);

        AnimatedDriverDecorator slowAnimatedDriverDecorator = new AnimatedDriverDecorator(basicLineDriver);
        slowAnimatedDriverDecorator.setSpeedSlow();
        DriverFeature.addDriver("Animated Line - slow", slowAnimatedDriverDecorator);

        AnimatedDriverDecorator mediumAnimatedDriverDecorator = new AnimatedDriverDecorator(basicLineDriver);
        mediumAnimatedDriverDecorator.setSpeedMedium();
        DriverFeature.addDriver("Animated Line - medium speed", mediumAnimatedDriverDecorator);

        AnimatedDriverDecorator fastAnimatedDriverDecorator = new AnimatedDriverDecorator(basicLineDriver);
        fastAnimatedDriverDecorator.setSpeedFast();
        DriverFeature.addDriver("Animated Line - fast", fastAnimatedDriverDecorator);

        VisitableJob2dDriver specialLineDriver = new LineDriverAdapter(drawerController, LineFactory.getSpecialLine(),
                "special");
        DriverFeature.addDriver("Special line Simulator", specialLineDriver);

        RecordingDriverDecorator recordingDriver = new RecordingDriverDecorator(basicLineDriver);
        SelectLoadRecordedCommandOptionListener selectLoadRecordedCommandOptionListener = new SelectLoadRecordedCommandOptionListener(
                recordingDriver);
        application.addTest("Stop recording & Load recorded command", selectLoadRecordedCommandOptionListener);
        DriverFeature.addDriver("Recording Driver", recordingDriver);

        // Set default driver
        DriverFeature.getDriverManager().setCurrentDriver(basicLineDriver);

        OnCanvasExceededStrategy onCanvasExceededStrategy = new OnCanvasExceededLogWarning();
        CanvasLimitedDriverDecorator canvasLimitedDriver = new CanvasLimitedDriverDecorator(basicLineDriver,
                onCanvasExceededStrategy);
        DriverFeature.addDriver("Canvas-Limited Driver", canvasLimitedDriver);

        DriverFeature.updateDriverInfo();
    }

}
