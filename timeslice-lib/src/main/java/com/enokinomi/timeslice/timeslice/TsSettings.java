package com.enokinomi.timeslice.timeslice;

public class TsSettings
{
    private final int tzOffset;

    public TsSettings(int tzOffset)
    {
        this.tzOffset = tzOffset;
    }

    public int getTzOffset()
    {
        return tzOffset;
    }
}
