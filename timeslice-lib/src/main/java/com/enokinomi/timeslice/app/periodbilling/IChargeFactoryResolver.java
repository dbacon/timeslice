package com.enokinomi.timeslice.app.periodbilling;


public interface IChargeFactoryResolver
{
    public IChargeFactory resolve(String name);
}
