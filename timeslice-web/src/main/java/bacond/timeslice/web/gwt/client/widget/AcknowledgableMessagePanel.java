package bacond.timeslice.web.gwt.client.widget;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AcknowledgableMessagePanel extends Composite
{
	public AcknowledgableMessagePanel(String html)
	{
		HorizontalPanel panel = new HorizontalPanel();
		VerticalPanel w = new VerticalPanel();
		Hyperlink link = new Hyperlink("X", true, "");
		link.addClickListener(new ClickListener()
		{
			public void onClick(Widget sender)
			{
				AcknowledgableMessagePanel.this.removeFromParent();
			}
		});
		w.add(link);
		panel.add(w);
		HTMLPanel msgPanel = new HTMLPanel(html);
		w.add(msgPanel);
		initWidget(panel);
	}
}