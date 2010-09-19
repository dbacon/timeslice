package com.enokinomi.timeslice.launcher;

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

        public static final String DATABASEBASEPATH = "timeslice.databasebasepath";
        public static final String ACL = "timeslice.acl";
        public static final String PORT = "timeslice.port";
        public static final String ResourceUrlOrFilename = "timeslice.resourcedir";

    }

    public static class Default
    {
        public static final String DATABASEBASEPATH = "ts-data";
        public static final String ACL = "timeslice.acl";
        public static final Integer PORT = 9080;
        public static final String ResourceUrlOrFilename = "./war";


    }

    public TsHostSettingsManager(Properties defaults)
    {
        super(defaults);
    }

    public Integer getPort()
    {
        return getOrDefaultInt(Key.PORT, Default.PORT);
    }

    public String getDatabaseBasePath()
    {
        return getOrDefault(Key.DATABASEBASEPATH, Default.DATABASEBASEPATH);
    }

    public String getAclFilename()
    {
        return getOrDefault(Key.ACL, Default.ACL);
    }

    public String getResourceUrlOrFilename()
    {
        return getOrDefault(Key.ResourceUrlOrFilename, Default.ResourceUrlOrFilename);
    }

}
