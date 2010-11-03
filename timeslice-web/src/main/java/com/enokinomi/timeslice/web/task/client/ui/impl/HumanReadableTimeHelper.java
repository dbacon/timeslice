package com.enokinomi.timeslice.web.task.client.ui.impl;

import com.google.gwt.i18n.client.NumberFormat;

public class HumanReadableTimeHelper
{
    public static String formatDuration(long millis)
    {
        long seconds = millis / 1000;
        long secondsRem = seconds % 60;
        long minutes = seconds / 60;
        long minutesRem = minutes % 60;
        long hours = minutes / 60;

        NumberFormat fmt = NumberFormat.getFormat("00");
        NumberFormat hfmt = NumberFormat.getFormat("##0");

        String hhmm = "" + hfmt.format(hours) + ":" + fmt.format(minutesRem);
        String ss = ":" + fmt.format(secondsRem);

        String msg = hhmm;

        if (seconds < 60)
        {
            msg = msg + ss;
        }

        return  msg;
    }
}
