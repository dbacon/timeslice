package bacond.timeslicer.app.periodbilling.api;

import java.util.List;


public interface IChargeFactory
{
	List<Charge> createCharges(long millis);
}
