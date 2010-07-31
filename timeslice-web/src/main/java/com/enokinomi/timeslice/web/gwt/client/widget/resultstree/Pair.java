package com.enokinomi.timeslice.web.gwt.client.widget.resultstree;


public class Pair<T1,T2>
{
    public T1 first;
    public T2 second;

    public Pair(T1 first, T2 second)
    {
        this.first = first;
        this.second = second;
    }

    public static <X,Y> Pair<X,Y> create(X x, Y y)
    {
        return new Pair<X,Y>(x, y);
    }

}
