/**
 *
 */
package bacond.timeslice.web.gwt.client.controller;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;

public class ErrorBox extends DialogBox
{
    public ErrorBox(String system, String msg)
    {
        super(true, true);

        this.setGlassEnabled(true);
        this.setAnimationEnabled(true);

        this.setText("Error: " + system);
        this.add(new Label((msg == null || msg.isEmpty()) ? "(No message available)" : msg));

        this.center();
    }
}
