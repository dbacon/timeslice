package com.enokinomi.timeslice.launcher;


public class Driver
{
    /**
     * Any argument present is taken as the name of settings to be loaded,
     * each having higher precedence than the previous.  System properties
     * are loaded and have the lowest precedence.
     *
     * <p>
     * Properties queried by TsHost are:<ul>
     *   <li><code>timeslice.port</code> - port on which the HTTP listener should listen. </li>
     *   <li><code>timeslice.war</code> - WAR file which the container should expand and deploy/host.</li>
     * </ul>
     * </p>
     *
     * <p>
     * Any property with the name beginning with <code>timeslice.</code> will be passed
     * as init-params to the container context.
     * </p>
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)
    {
        TsHostSettingsManager sm = new TsHostSettingsManager(System.getProperties()).pushSettings(args, true);

        new TsHost(sm.getPort())
            .createWarHandler(
                sm.getWarFileName(),
                sm.getContextPath(),
                sm.createInitParams())
            .run();
    }
}
