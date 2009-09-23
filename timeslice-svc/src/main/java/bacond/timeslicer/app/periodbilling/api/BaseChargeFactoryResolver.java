package bacond.timeslicer.app.periodbilling.api;

import bacond.lib.util.Check;

public class BaseChargeFactoryResolver implements IChargeFactoryResolver
{
	@Override
	public IChargeFactory resolve(String name)
	{
		Check.disallowNull(name, "name");

		return new BaseChargeFactory(name);
	}
}
