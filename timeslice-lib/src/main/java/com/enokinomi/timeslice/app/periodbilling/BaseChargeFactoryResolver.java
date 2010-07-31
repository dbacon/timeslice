package com.enokinomi.timeslice.app.periodbilling;

import com.enokinomi.timeslice.lib.util.Check;

public class BaseChargeFactoryResolver implements IChargeFactoryResolver
{
    @Override
    public IChargeFactory resolve(String name)
    {
        Check.disallowNull(name, "name");

        return new BaseChargeFactory(name);
    }
}
