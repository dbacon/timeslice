package com.enokinomi.timeslice.lib.commondatautil.api;

public final class SetParam
{
    private final Object[] values;

    public SetParam(Object[] values)
    {
        this.values = values;
    }

    public Object[] getValues()
    {
        return values;
    }
}
