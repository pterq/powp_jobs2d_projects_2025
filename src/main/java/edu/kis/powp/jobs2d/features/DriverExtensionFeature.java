package edu.kis.powp.jobs2d.features;

import edu.kis.powp.appbase.Application;
import edu.kis.powp.jobs2d.drivers.DriverManager;
import edu.kis.powp.jobs2d.drivers.extension.DriverExtensionManager;
import edu.kis.powp.jobs2d.drivers.extension.IDriverExtension;
import edu.kis.powp.jobs2d.drivers.extension.TransformationExtension;

import javax.swing.*;
import java.awt.*;

/**
 * Feature for managing driver extensions through UI.
 */
public class DriverExtensionFeature implements IFeature {

    private JFrame extensionFrame;

    @Override
    public void setup(Application application) {
        setupExtensionsUI(application);
    }

    @Override
    public String getName() {
        return "DriverExtensions";
    }

    private void setupExtensionsUI(Application application) {
        DriverManager driverManager = DriverFeature.getDriverManager();
        DriverExtensionManager extensionManager = driverManager.getExtensionManager();

        // Register default extensions
        extensionManager.registerExtension(TransformationExtension.createRotation(90));
        extensionManager.registerExtension(TransformationExtension.createScale(2.0));
        extensionManager.registerExtension(TransformationExtension.createFlip(true, false));

        // Create UI frame
        extensionFrame = new JFrame("Driver Extensions");
        extensionFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        extensionFrame.setSize(300, 250);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Add checkboxes for each extension
        for (IDriverExtension extension : extensionManager.getExtensions()) {
            final IDriverExtension ext = extension;
            JCheckBox checkBox = new JCheckBox(extension.getName(), extension.isEnabled());
            checkBox.addActionListener(e -> {
                ext.setEnabled(checkBox.isSelected());
                driverManager.updateExtensionsApplied();
            });
            panel.add(checkBox);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        extensionFrame.add(scrollPane);

        // Add menu item
        application.addComponentMenu(DriverExtensionFeature.class, "Driver Extensions");
        application.addComponentMenuElement(DriverExtensionFeature.class, "Manage Extensions",
                e -> extensionFrame.setVisible(true));
    }
}
