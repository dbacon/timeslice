package com.enokinomi.timeslice.lib.task;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import com.enokinomi.timeslice.lib.generic.IHasWhen;
import com.enokinomi.timeslice.lib.util.Transforms;


public class StartTag implements IHasWhen
{
    private final String who;
    private final Instant when;
    private final String what;
    private final Instant until;

    /**
     * Parameter {@code when} defaults to now if passed as {@code null}.
     *
     * @param who
     * @param when
     * @param what
     * @param until
     */
    public StartTag(String who, String when, String what, String until)
    {
        this(
                Transforms.mapNullTo(who, "unknown"),
                null == when ? new Instant() : ISODateTimeFormat.dateTime().parseDateTime(when).toInstant(),
                Transforms.mapNullTo(what, "unknown"),
                null == until ? null : ISODateTimeFormat.dateTime().parseDateTime(until).toInstant()
                );
    }

    public StartTag(String who, Instant when, String what, Instant until)
    {
        this.who = who;
        this.when = when;
        this.what = what;
        this.until = until;
    }

    @Override
    public String toString()
    {
        return String.format("[%s, %s, %s, %s]", getWho(), getWhen(), getUntil(), getWhat());
    }

    public String getWho()
    {
        return who;
    }

    public Instant getWhen()
    {
        return when;
    }

    public String getWhat()
    {
        return what;
    }

    public Instant getUntil()
    {
        return until;
    }
}
