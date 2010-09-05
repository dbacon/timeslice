package com.enokinomi.timeslice.app.core;

import java.util.List;


public class Sum
{
    public TaskTotal sum(List<StartTag> items)
    {
        String who = null;
        int millis = 0;
        String what = null;

        for (StartTag tag: items)
        {
            if (null == who)
            {
                who = tag.getWho();
            }
            else
            {
                if (!who.equals(tag.getWho()))
                {
                    throw new RuntimeException("'who' did not match");
                }
            }
            if (null == what)
            {
                what = tag.getWhat();
            }
            else
            {
                if (!what.equals(tag.getWhat()))
                {
                    throw new RuntimeException("'what' did not match");
                }
            }

            if (null == tag.getWhen())
            {
                throw new RuntimeException("'when' was null");
            }

            if (null == tag.getUntil())
            {
                throw new RuntimeException("'until' was null");
            }

            millis += new org.joda.time.Duration(tag.getWhen(), tag.getUntil()).getMillis();
        }

        return new TaskTotal(who, millis, what);
    }
}
