package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.command.gui.CommandHistoryWindow;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindow;
import edu.kis.powp.jobs2d.command.gui.CommandManagerWindowCommandChangeObserver;
import edu.kis.powp.jobs2d.command.gui.CommandPreviewWindow;
import edu.kis.powp.jobs2d.command.gui.CommandPreviewWindowObserver;
import edu.kis.powp.jobs2d.command.gui.SelectImportCommandOptionListener;
import edu.kis.powp.jobs2d.command.gui.SelectImportCommandFromTextOptionListener;
import edu.kis.powp.jobs2d.command.gui.SelectSaveCommandToFileOptionListener;
import edu.kis.powp.jobs2d.command.importer.CommandExportFormatter;
import edu.kis.powp.jobs2d.command.importer.CommandImportParserSelector;
import edu.kis.powp.jobs2d.command.manager.CommandHistory;
import edu.kis.powp.jobs2d.command.manager.CommandHistorySubscriber;
import edu.kis.powp.jobs2d.command.manager.CommandManager;
import edu.kis.powp.jobs2d.command.manager.DefaultCommandManager;
import edu.kis.powp.jobs2d.command.manager.LoggerCommandChangeObserver;
import edu.kis.powp.jobs2d.drivers.transformation.FlipStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.RotateStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.ScaleStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.ShearStrategy;
import edu.kis.powp.jobs2d.drivers.transformation.ShiftStrategy;
import edu.kis.powp.jobs2d.events.SelectCommandTransformationOptionListener;
import edu.kis.powp.jobs2d.events.SelectDuplicateCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectRunCurrentCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectRunCurrentFlippedCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectRunCurrentRotatedCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectRunCurrentScaledDownCommandOptionListener;
import edu.kis.powp.jobs2d.events.SelectRunCurrentScaledUpCommandOptionListener;

public class CommandsFeature implements IFeature {

    private static CommandManager commandManager;
    private static CommandHistory commandHistory;
    private static CommandHistorySubscriber commandHistorySubscriber;

    public CommandsFeature() {
    }

    @Override
    public void setup(Application app) {
        setupCommandManager();
        setupCommandsMenu(app);
        setupWindows(app);
    }

    private static void setupCommandManager() {
        commandManager = new DefaultCommandManager();

        LoggerCommandChangeObserver loggerObserver = new LoggerCommandChangeObserver();
        commandManager.getChangePublisher().addSubscriber(loggerObserver);

        // Setup command history tracking
        commandHistory = new CommandHistory();
        commandHistorySubscriber = new CommandHistorySubscriber(commandHistory);
        commandManager.getChangePublisher().addSubscriber(commandHistorySubscriber);
    }

    /**
     * Set up the "Commands" menu in the application GUI.
     */
    private static void setupCommandsMenu(Application application) {
        application.addComponentMenu(CommandsFeature.class, "Commands");

        application.addComponentMenuElement(CommandsFeature.class, "Run command",
            new SelectRunCurrentCommandOptionListener(DriverFeature.getDriverManager()));

        application.addComponentMenuElement(CommandsFeature.class, "Flip command",
            new SelectRunCurrentFlippedCommandOptionListener());

        application.addComponentMenuElement(CommandsFeature.class, "Rotate 90 command",
            new SelectRunCurrentRotatedCommandOptionListener());

        application.addComponentMenuElement(CommandsFeature.class, "Scale 2.0 command",
            new SelectRunCurrentScaledUpCommandOptionListener());

        application.addComponentMenuElement(CommandsFeature.class, "Scale 0.5 command",
            new SelectRunCurrentScaledDownCommandOptionListener());

        CommandManager manager = CommandsFeature.getDriverCommandManager();
        application.addComponentMenuElement(CommandsFeature.class, "Duplicate command (deep copy)",
            new SelectDuplicateCommandOptionListener(manager));
        application.addComponentMenuElement(CommandsFeature.class, "Scale x2", new SelectCommandTransformationOptionListener(manager, new ScaleStrategy(2)));
        application.addComponentMenuElement(CommandsFeature.class,"Rotate 90 degrees",
            new SelectCommandTransformationOptionListener(manager, new RotateStrategy(90)));
        application.addComponentMenuElement(CommandsFeature.class,"Flip",
            new SelectCommandTransformationOptionListener(manager, new FlipStrategy(true, false)));
        application.addComponentMenuElement(CommandsFeature.class,"Shift (right: 15)",
            new SelectCommandTransformationOptionListener(manager, new ShiftStrategy(15, 0)));
        application.addComponentMenuElement(CommandsFeature.class,"Shift (down: 15)",
            new SelectCommandTransformationOptionListener(manager, new ShiftStrategy(0, 15)));
        application.addComponentMenuElement(CommandsFeature.class,"Shear (X: 0.5)",
            new SelectCommandTransformationOptionListener(manager, new ShearStrategy(0.5, 0)));
        application.addComponentMenuElement(CommandsFeature.class,"Shear (Y: 0.5)",
            new SelectCommandTransformationOptionListener(manager, new ShearStrategy(0, 0.5)));
    }

    private static void setupWindows(Application application) {

        CommandManagerWindow commandManagerWindow = new CommandManagerWindow(CommandsFeature.getDriverCommandManager());
        CommandImportParserSelector parserSelector = new CommandImportParserSelector();
        SelectImportCommandOptionListener importListener = new SelectImportCommandOptionListener(
            CommandsFeature.getDriverCommandManager(),
            parserSelector,
            commandManagerWindow);
        SelectImportCommandFromTextOptionListener importFromTextListener = new SelectImportCommandFromTextOptionListener(
            CommandsFeature.getDriverCommandManager(),
            parserSelector,
            commandManagerWindow);
        SelectSaveCommandToFileOptionListener saveToFileListener = new SelectSaveCommandToFileOptionListener(
            CommandsFeature.getDriverCommandManager(),
            commandManagerWindow,
            parserSelector,
            new CommandExportFormatter());
        SelectDuplicateCommandOptionListener duplicateListener = new SelectDuplicateCommandOptionListener(
            CommandsFeature.getDriverCommandManager());
        commandManagerWindow.setImportActionListener(importListener);
        commandManagerWindow.setApplyTextActionListener(importFromTextListener);
        commandManagerWindow.setSaveTextActionListener(saveToFileListener);
        commandManagerWindow.setRunCommandActionListener(new SelectRunCurrentCommandOptionListener(DriverFeature.getDriverManager()));
        commandManagerWindow.setDuplicateCommandActionListener(duplicateListener);
        application.addWindowComponent("Command Manager", commandManagerWindow);

        CommandManagerWindowCommandChangeObserver windowObserver = new CommandManagerWindowCommandChangeObserver(
            commandManagerWindow);
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(windowObserver);

        CommandPreviewWindow commandPreviewWindow = new CommandPreviewWindow();
        commandManagerWindow.setPreviewWindow(commandPreviewWindow);
        application.addWindowComponent("Command Preview", commandPreviewWindow);
        CommandPreviewWindowObserver previewObserver = new CommandPreviewWindowObserver(
            commandPreviewWindow,
            CommandsFeature.getDriverCommandManager()
        );
        CommandsFeature.getDriverCommandManager().getChangePublisher().addSubscriber(previewObserver);
        CommandHistoryWindow historyWindow = new CommandHistoryWindow(CommandsFeature.getCommandHistory(),
            CommandsFeature.getDriverCommandManager());
        application.addWindowComponent("Command History", historyWindow);
    }

    /**
     * Get manager of application driver command.
     *
     * @return plotterCommandManager.
     */
    public static CommandManager getDriverCommandManager() {
        return commandManager;
    }

    /**
     * Get the command history instance.
     *
     * @return the CommandHistory tracking all set commands.
     */
    public static CommandHistory getCommandHistory() {
        return commandHistory;
    }

    @Override
    public String getName() {
        return "Commands";
    }
}
