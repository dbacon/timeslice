package bacond.timeslicer.app.periodbilling.api;


public interface IChargeFactoryResolver
{
	public IChargeFactory resolve(String name);
}
