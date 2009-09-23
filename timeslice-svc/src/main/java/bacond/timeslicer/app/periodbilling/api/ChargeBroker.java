package bacond.timeslicer.app.periodbilling.api;

import java.util.ArrayList;
import java.util.List;


public class ChargeBroker
{
	private final IChargeFactoryResolver chargeFactoryResolver;

	public ChargeBroker(IChargeFactoryResolver chargeFactoryResolver)
	{
		this.chargeFactoryResolver = chargeFactoryResolver;
	}

	public IChargeFactoryResolver getChargeFactoryResolver()
	{
		return chargeFactoryResolver;
	}

	public List<Charge> crunch(BillableTotalList billableTasks)
	{
		return new Collater().apply(createAllCharges(billableTasks));
	}

	public List<Charge> createAllCharges(BillableTotalList billableTasks)
	{
		List<Charge> charges = new ArrayList<Charge>(100);

		for (BillableTotal total: billableTasks.getConstBillableTasks())
		{
			if (null == total.getBilledTo() || total.getBilledTo().trim().isEmpty())
			{
				throw new RuntimeException("Found an un-assigned billable total: " +
						total.getMillis() +
						"ms of '" +
						total.getDescription() +
						"'");
			}

			charges.addAll(getChargeFactoryResolver().resolve(total.getBilledTo()).createCharges(total.getMillis()));
		}

		return charges;
	}
}
