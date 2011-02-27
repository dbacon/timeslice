package com.enokinomi.timeslice.web.report.client.presenter;

public class Mutable<T>
{
    private T value;

    public Mutable()
    {
        this(null);
    }

    public Mutable(T value)
    {
        this.setValue(value);
    }

    public void setValue(T value)
    {
        this.value = value;
    }

    public T getValue()
    {
        return value;
    }
}
