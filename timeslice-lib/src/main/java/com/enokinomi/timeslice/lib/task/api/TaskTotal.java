package com.enokinomi.timeslice.lib.task.api;


public class TaskTotal
{
    private final String who;
    private final int millis;
    private final String what;

    public TaskTotal(String who, int millis, String what)
    {
        this.who = who;
        this.millis = millis;
        this.what = what;
    }

    @Override
    public String toString()
    {
        return String.format("[%s, %d, %s]", getWho(), getMillis(), getWhat());
    }

    public String getWho()
    {
        return who;
    }

    public int getMillis()
    {
        return millis;
    }

    public String getWhat()
    {
        return what;
    }
}
