package bacond.launcher;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Specialized version of {@link SettingsManager} to provide settings for
 * creating a {@link TsHost}.
 *
 * @author dbacon
 *
 */
public class TsHostSettingsManager extends SettingsManager<TsHostSettingsManager>
{
    public static class Key
    {
        // Used by TsHost

        public static final String PORT = "timeslice.port";
        public static final String WAR = "timeslice.war";
        public static final String CONTEXTPATH = "timeslice.contextpath";


        // Passed through to the servlet context if specified

        public static final String ACL = "timeslice.acl";
        public static final String SAFEDIR = "timeslice.safedir";
        public static final String TZOFFSET = "timeslice.tzoffset";
        public static final String DATADIR = "timeslice.datadir";
    }

    public static class Default
    {
        public static final Integer PORT = 9080;
        public static final String WAR = "timeslice.war";
        public static final String CONTEXTPATH = "/";
//        public static final String GWT_SERVLET_PATH = "/timeslice.App/timeslice";
    }

    public TsHostSettingsManager(Properties defaults)
    {
        super(defaults);
    }

    public String getWarFileName()
    {
        return getOrDefault(Key.WAR, Default.WAR);
    }

    public Integer getPort()
    {
        return getOrDefaultInt(Key.PORT, Default.PORT);
    }

    public String getContextPath()
    {
        return getOrDefault(Key.CONTEXTPATH, Default.CONTEXTPATH);
    }

    public Map<String, String> createInitParams()
    {
        Map<String, String> initParams = new LinkedHashMap<String, String>();
        Util.copyPrefixed(getSettings(), "timeslice.", initParams);
        return initParams;
    }
}
