package com.enokinomi.timeslice.lib.task;


public class TaskTotalMember
{
    private final String who;
    private final int millis;
    private final String what;
    private final Double percentage;

    public TaskTotalMember(String who, int millis, String what, Double percentage)
    {
        this.who = who;
        this.millis = millis;
        this.what = what;
        this.percentage = percentage;
    }

    @Override
    public String toString()
    {
        return String.format("[%s, %d, %s, %.4f]", getWho(), getMillis(), getWhat(), getPercentage());
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

    public Double getPercentage()
    {
        return percentage;
    }
}
