package edu.kis.powp.jobs2d.drivers.extension;

import edu.kis.powp.jobs2d.visitor.VisitableJob2dDriver;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager for applying extensions to drivers.
 */
public class DriverExtensionManager {
    private List<IDriverExtension> extensions = new ArrayList<>();

    public void registerExtension(IDriverExtension extension) {
        extensions.add(extension);
    }

    public VisitableJob2dDriver applyExtensions(VisitableJob2dDriver driver) {
        VisitableJob2dDriver result = driver;
        for (IDriverExtension ext : extensions) {
            if (ext.isEnabled()) {
                result = ext.apply(result);
            }
        }
        return result;
    }

    public List<IDriverExtension> getExtensions() {
        return new ArrayList<>(extensions);
    }
}
