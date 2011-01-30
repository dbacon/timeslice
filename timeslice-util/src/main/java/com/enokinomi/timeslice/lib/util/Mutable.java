package com.enokinomi.timeslice.lib.util;

public class Mutable<T>
{
    private T t;
    public Mutable(T t) { this.t = t; }
    public T get() { return t; }
    public void set(T t) { this.t = t; }
}
