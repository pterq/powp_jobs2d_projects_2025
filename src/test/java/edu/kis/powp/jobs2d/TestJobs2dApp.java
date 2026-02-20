package edu.kis.powp.jobs2d;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.command.CommandFactory;
import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.events.CanvasMouseListener;
import edu.kis.powp.jobs2d.events.SelectCountCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectCountDriverOptionListener;
import edu.kis.powp.jobs2d.events.SelectLoadSecretCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectTestCompoundCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectTestFigure2OptionListener;
import edu.kis.powp.jobs2d.events.SelectTestFigureOptionListener;
import edu.kis.powp.jobs2d.events.SelectValidateCanvasBoundsOptionListener;
import edu.kis.powp.jobs2d.features.CanvasFeature;
import edu.kis.powp.jobs2d.features.CommandsFeature;
import edu.kis.powp.jobs2d.features.DrawerFeature;
import edu.kis.powp.jobs2d.features.DriverExtensionFeature;
import edu.kis.powp.jobs2d.features.DriverFeature;
import edu.kis.powp.jobs2d.features.FeatureManager;
import edu.kis.powp.jobs2d.features.MonitoringFeature;
import edu.kis.powp.jobs2d.features.ViewFeature;

public class TestJobs2dApp {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Setup test concerning preset figures in context.
     *
     * @param application Application context.
     */
    private static void setupPresetTests(Application application) {
        SelectTestFigureOptionListener selectTestFigureOptionListener = new SelectTestFigureOptionListener(
                DriverFeature.getDriverManager());
        SelectTestFigure2OptionListener selectTestFigure2OptionListener = new SelectTestFigure2OptionListener(
                DriverFeature.getDriverManager());
        SelectTestCompoundCommandOptionListener selectTestCompoundCommandOptionListener = new SelectTestCompoundCommandOptionListener();

        CommandManager commandManager = CommandsFeature.getDriverCommandManager();
        SelectCountCommandOptionListener selectCountCommandOptionListener = new SelectCountCommandOptionListener(
                commandManager);

        SelectCountDriverOptionListener selectCountDriverOptionListener = new SelectCountDriverOptionListener();
        SelectValidateCanvasBoundsOptionListener selectValidateCanvasBoundsOptionListener = new SelectValidateCanvasBoundsOptionListener(
                commandManager, logger);

        application.addTest("Figure Joe 1", selectTestFigureOptionListener);
        application.addTest("Figure Joe 2", selectTestFigure2OptionListener);
        application.addTest("Figure House - CompoundCommand", selectTestCompoundCommandOptionListener);
        application.addTest("Count commands - Visitor", selectCountCommandOptionListener);
        application.addTest("Count drivers - Visitor", selectCountDriverOptionListener);
        application.addTest("Validate Canvas Bounds", selectValidateCanvasBoundsOptionListener);
    }

    /**
     * Setup test using driver commands in context.
     *
     * @param application Application context.
     */
    private static void setupCommandTests(Application application) {
        ViewFeature.addMouseListenerToControlPanel(new CanvasMouseListener());
        application.addTest("Load secret command", new SelectLoadSecretCommandOptionListener());
    }


    /**
     * Setup test using command factory in context.
     *
     * @param application Application context.
     */
    private static void setupCommandFactoryTest(Application application) {
        CommandManager commandManager = CommandsFeature.getDriverCommandManager();
        CommandFactory commandFactory = new CommandFactory();

        System.out.println("Available commands: " + commandFactory.getAvailableCommands());

        //createSetPosition test
        commandFactory.registerCommand("Home", () -> commandFactory.createSetPosition(0, 0));
        System.out.println("Available commands: " + commandFactory.getAvailableCommands());

        commandManager.setCurrentCommand(commandFactory.createCommand("Home"));

        //createOperateTo test
        commandFactory.registerCommand("DrawTo10_10", () -> commandFactory.createOperateTo(10, 10));
        System.out.println("Available commands: " + commandFactory.getAvailableCommands());

        commandManager.setCurrentCommand(commandFactory.createCommand("DrawTo10_10"));


        //CompoundCommand test
        List<DriverCommand> compoundCommands = Arrays.asList(
                commandFactory.createSetPosition(0, 0),
                commandFactory.createOperateTo(50, 0),
                commandFactory.createOperateTo(0, 50),
                commandFactory.createOperateTo(0, 0)
        );

        DriverCommand compound = commandFactory.createCompoundCommand(compoundCommands, "Triangle");
        commandFactory.registerCommand("Triangle", () -> compound);

        System.out.println("Available commands: " + commandFactory.getAvailableCommands());

        commandManager.setCurrentCommand(commandFactory.createCommand("Triangle"));

    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                Application app = new Application("Jobs 2D");

                FeatureManager featureManager = new FeatureManager();
                featureManager.setApplication(app);
                featureManager.registerFeature(new ViewFeature());
                featureManager.registerFeature(new DrawerFeature());
                featureManager.registerFeature(new CanvasFeature());
                featureManager.registerFeature(new CommandsFeature());
                featureManager.registerFeature(new DriverFeature(logger));
                featureManager.registerFeature(new DriverExtensionFeature());
                featureManager.registerFeature(new MonitoringFeature(logger));
                featureManager.setupAll();

                setupPresetTests(app);
                setupCommandTests(app);
                setupCommandFactoryTest(app);

                app.setVisibility(true);
            }
        });
    }

}