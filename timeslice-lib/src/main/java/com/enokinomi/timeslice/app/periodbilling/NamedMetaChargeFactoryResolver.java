package com.enokinomi.timeslice.app.periodbilling;

import com.enokinomi.timeslice.lib.util.Check;

public class NamedMetaChargeFactoryResolver implements IChargeFactoryResolver
{
    @Override
    public IChargeFactory resolve(String name)
    {
        Check.disallowNull(name, "name");

        if ("magic".equals(name))
        {
            return new ProRataChargeFactory();
        }

        return null;
    }
}
