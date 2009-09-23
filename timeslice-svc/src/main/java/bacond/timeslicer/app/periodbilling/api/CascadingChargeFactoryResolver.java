package bacond.timeslicer.app.periodbilling.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CascadingChargeFactoryResolver implements IChargeFactoryResolver
{
	private final IChargeFactoryResolver resolver;
	private final IChargeFactoryResolver nextResolver;

	public CascadingChargeFactoryResolver(IChargeFactoryResolver resolver, IChargeFactoryResolver nextResolver)
	{
		this.resolver = resolver;
		this.nextResolver  = nextResolver;
	}

	@Override
	public IChargeFactory resolve(String name)
	{
		IChargeFactory chargeFactory = resolver.resolve(name);

		if (null == chargeFactory)
		{
			if (null == nextResolver)
			{
				chargeFactory = nextResolver.resolve(name);
			}
			else
			{
				throw new RuntimeException("No resolver in cascading resolution provided a charge factory.");
			}
		}

		return chargeFactory;
	}

	public static CascadingChargeFactoryResolver create(List<IChargeFactoryResolver> elements)
	{
		CascadingChargeFactoryResolver head = null;

		ArrayList<IChargeFactoryResolver> reversedElements = new ArrayList<IChargeFactoryResolver>(elements);
		Collections.reverse(reversedElements);

		for (IChargeFactoryResolver resolver: reversedElements)
		{
			head = new CascadingChargeFactoryResolver(resolver, head);
		}

		return head;
	}
}
