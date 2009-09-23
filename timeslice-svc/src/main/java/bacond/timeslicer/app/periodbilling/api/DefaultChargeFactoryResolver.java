package bacond.timeslicer.app.periodbilling.api;

import java.util.Arrays;


public class DefaultChargeFactoryResolver
{
	public static IChargeFactoryResolver create()
	{
		return CascadingChargeFactoryResolver.create(Arrays.asList(
				new StringParsedMetaChargeFactoryResolver(),
				new NamedMetaChargeFactoryResolver(),
				new BaseChargeFactoryResolver()
				));
	}
}
