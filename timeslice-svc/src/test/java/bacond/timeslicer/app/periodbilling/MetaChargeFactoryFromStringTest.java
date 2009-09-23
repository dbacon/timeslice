package bacond.timeslicer.app.periodbilling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import bacond.timeslicer.app.periodbilling.api.Charge;
import bacond.timeslicer.app.periodbilling.api.IChargeFactory;
import bacond.timeslicer.app.periodbilling.api.StringParsedMetaChargeFactoryResolver;


public class MetaChargeFactoryFromStringTest
{
	@Test
	public void check() throws Exception
	{
		String inputString = "meta:1,abc def:3,abc:2,stuff:1,things";

		StringParsedMetaChargeFactoryResolver resolver = new StringParsedMetaChargeFactoryResolver();

		IChargeFactory factory = resolver.resolve(inputString);
		assertNotNull("string parseable into factory", factory);

		List<Charge> charges = factory.createCharges(100);

		for (Charge charge: charges)
		{
//			System.out.printf("charge: %2$ 6d -> %1$s\n", charge.getChargeableName(), charge.getMillis());

			if ("abc def".equals(charge.getChargeableName()))
			{
				assertEquals(14, charge.getMillis());
			}
			else if ("abc".equals(charge.getChargeableName()))
			{
				assertEquals(43, charge.getMillis());
			}
			else if ("stuff".equals(charge.getChargeableName()))
			{
				assertEquals(29, charge.getMillis());
			}
			else if ("things".equals(charge.getChargeableName()))
			{
				assertEquals(14, charge.getMillis());
			}
		}
	}
}
