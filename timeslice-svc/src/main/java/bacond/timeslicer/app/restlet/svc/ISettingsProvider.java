package bacond.timeslicer.app.restlet.svc;

import java.util.Properties;

public interface ISettingsProvider
{
	Properties readSettings(Properties settings);
	void writeSettings(Properties settings);
}