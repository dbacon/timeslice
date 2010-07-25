package bacond.timeslicer.app.periodbilling;

import bacond.lib.util.Check;

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
