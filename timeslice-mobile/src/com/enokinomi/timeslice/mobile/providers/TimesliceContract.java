package com.enokinomi.timeslice.mobile.providers;

import android.net.Uri;

public class TimesliceContract
{
    public static final String AUTHORITY = "timeslice.mobile.providers";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static class Tasks
    {
        private Tasks() { }

        public static final String _ID = "_id";
        public static final String _COUNT = "_count";

        public static final String WHO = "who";
        public static final String WHAT = "what";
        public static final String WHEN = "_when";
        public static final String STATUS = "status";

        public static class StatusValue
        {
            private StatusValue() { }

            public static final String ST_UNSENT = "U";
            public static final String ST_SENT = "S";
        }


        /**
         * All tasks.
         */
        public static Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "tasks");


        /**
         * Filtered tasks.
         *
         * Following path-segment is filter argument.
         */
        public static Uri CONTENT_FILTER_URI = Uri.withAppendedPath(CONTENT_URI, "filter");


        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.timeslice.task";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/tstask";

    }

}
