package com.enokinomi.timeslice.lib.task.api;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import com.enokinomi.timeslice.lib.util.IHasWhen;
import com.enokinomi.timeslice.lib.util.Transforms;


public class StartTag implements IHasWhen
{
    private final String who;
    private final Instant when;
    private final String what;
    private final Instant until;
    private final boolean continues;

    /**
     * Parameter {@code when} defaults to now if passed as {@code null}.
     *
     * @param who
     * @param when
     * @param what
     * @param until
     */
    public StartTag(String who, String when, String what, String until, boolean continues)
    {
        this(
                Transforms.mapNullTo(who, "unknown"),
                null == when ? new Instant() : ISODateTimeFormat.dateTime().parseDateTime(when).toInstant(),
                Transforms.mapNullTo(what, "unknown"),
                null == until ? null : ISODateTimeFormat.dateTime().parseDateTime(until).toInstant(),
                continues);
    }

    public StartTag(String who, Instant when, String what, Instant until, boolean continues)
    {
        this.who = who;
        this.when = when;
        this.what = what;
        this.until = until;
        this.continues = continues;
    }

    @Override
    public String toString()
    {
        return String.format("[%s, %s, %s, %s, %s]", getWho(), getWhen(), getUntil(), getWhat(), isContinues());
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

    public boolean isContinues()
    {
        return continues;
    }
}
