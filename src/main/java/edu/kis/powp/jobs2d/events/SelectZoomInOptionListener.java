package edu.kis.powp.jobs2d.events;

import edu.kis.powp.jobs2d.features.ViewFeature;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener for zoom in action.
 */
public class SelectZoomInOptionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ViewFeature.zoomIn();
    }
}
