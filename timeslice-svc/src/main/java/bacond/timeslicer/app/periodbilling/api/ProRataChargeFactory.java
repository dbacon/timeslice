package bacond.timeslicer.app.periodbilling.api;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class ProRataChargeFactory implements IChargeFactory
{
	public static class Component
	{
		private final IChargeFactory chargeFactory;
		private final int weight;

		public Component(IChargeFactory chargeFactory, int weight)
		{
			this.chargeFactory = chargeFactory;
			this.weight = weight;
		}

		public IChargeFactory getChargeFactory()
		{
			return chargeFactory;
		}

		public int getWeight()
		{
			return weight;
		}
	}

	private final List<Component> components = new ArrayList<Component>();

	public ProRataChargeFactory addComponent(Component component)
	{
		components.add(component);
		return this;
	}

	private int calculateTotalWeight()
	{
		int total = 0;

		for (Component component: components)
		{
			total += component.weight;
		}

		return total;
	}

	@Override
	public List<Charge> createCharges(long millis)
	{
		List<Charge> result = new ArrayList<Charge>();

		int totalWeight = calculateTotalWeight();

		for (Component component: components)
		{
			result.addAll(component.chargeFactory.createCharges(
					new BigDecimal(millis)
						.multiply(
								BigDecimal.valueOf(component.weight)
									.divide(BigDecimal.valueOf(totalWeight), MathContext.DECIMAL64))
						.setScale(0, RoundingMode.HALF_UP)
						.longValueExact()
				));
		}

		return result;
	}
}
