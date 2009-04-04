package bacond.timeslice.web.gwt.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public class HotlistPanel extends Composite
{
	public static final class Mode
	{
		public static final String Edit = "Hotlist [delete-mode]";
		public static final String Hotlist = "Hotlist";
	}

	private static final String CookieNamePrefix = "timeslice.hotlist.";
	
	private final FlowPanel vp = new FlowPanel();
	private final Hyperlink mode = new Hyperlink(Mode.Hotlist, "");
	
	public static interface IHotlistPanelListener
	{
		void hotlistItemClicked(String description);
		void hotlistChanged();
	}
	
	private final List<IHotlistPanelListener> listeners = new ArrayList<IHotlistPanelListener>();
	
	public void addHotlistPanelListener(IHotlistPanelListener listener)
	{
		if (null != listener)
		{
			listeners.add(listener);
		}
	}
	
	public void removeHotlistPanelListener(IHotlistPanelListener listener)
	{
		listeners.remove(listener);
	}
	
	protected void fireHotlistItemClicked(String description)
	{
		for (IHotlistPanelListener listener: listeners)
		{
			listener.hotlistItemClicked(description);
		}
	}

	protected void fireHotlistChanged()
	{
		for (IHotlistPanelListener listener: listeners)
		{
			listener.hotlistChanged();
		}
	}

	public HotlistPanel()
	{
		mode.setTitle("Hotlist panel - click to switch between live and delete mode.");
		mode.addClickListener(new ClickListener()
		{
			public void onClick(Widget arg0)
			{
				if (Mode.Hotlist.equals(mode.getText()) && 1 < vp.getWidgetCount())
				{
					mode.setText(Mode.Edit);
				}
				else if (Mode.Edit.equals(mode.getText()))
				{
					mode.setText(Mode.Hotlist);
				}
				else
				{
					mode.setText(Mode.Hotlist);
				}
			}
		});
		
		repopulate();
		
		DecoratorPanel dp = new DecoratorPanel();
		dp.setWidget(vp);
		dp.setTitle(Mode.Hotlist);
		initWidget(dp);
	}
	
	public void repopulate()
	{
		vp.clear();
		
		vp.add(mode);
		
		int added = 0;
		
		for (final String name: Cookies.getCookieNames())
		{
			if (name.startsWith(CookieNamePrefix))
			{
				final String description = Cookies.getCookie(name);
		
				Button button = new Button(description, new ClickListener()
				{
					public void onClick(Widget arg0)
					{
						if (Mode.Hotlist.equals(mode.getText()))
						{
							fireHotlistItemClicked(description);
						}
						else if (Mode.Edit.equals(mode.getText()))
						{
							Cookies.removeCookie(name);

							repopulate();
							
							fireHotlistChanged();
						}
					}
				});
				
				vp.add(button);
				
				++added;
			}
		}
		
		if (added <= 0)
		{
			mode.setText(Mode.Hotlist);
		}
	}
	
	public int getHotlistItemCount()
	{
		return vp.getWidgetCount() - 1;
	}

	public void addAsHotlistItem(String name, String description)
	{
		Cookies.setCookie(CookieNamePrefix  + name.hashCode(), description);
		repopulate();
		fireHotlistChanged();
	}
}
