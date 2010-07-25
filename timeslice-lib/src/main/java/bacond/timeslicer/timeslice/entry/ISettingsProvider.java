package bacond.timeslicer.timeslice.entry;

import java.util.Properties;

public interface ISettingsProvider
{
    Properties readSettings(Properties settings);
    void writeSettings(Properties settings);
}
