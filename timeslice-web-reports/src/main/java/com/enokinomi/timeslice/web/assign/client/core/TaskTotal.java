package com.enokinomi.timeslice.web.assign.client.core;

import java.io.Serializable;

public class TaskTotal implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String who;
    private Double hours;
    private Double percentage;
    private String what;

    public TaskTotal()
    {
        this("", 0., 0., "");
    }

    public TaskTotal(String who, Double hours, Double percentage, String what)
    {
        this.who = who;
        this.hours = hours;
        this.percentage = percentage;
        this.what = what;
    }

    public String getWho()
    {
        return who;
    }

    public void setWho(String who)
    {
        this.who = who;
    }

    public Double getHours()
    {
        return hours;
    }

    public void setHours(Double hours)
    {
        this.hours = hours;
    }

    public Double getPercentage()
    {
        return percentage;
    }

    public void setPercentage(Double percentage)
    {
        this.percentage = percentage;
    }

    public String getWhat()
    {
        return what;
    }

    public void setWhat(String what)
    {
        this.what = what;
    }
}
