package bacond.timeslicer.app.periodbilling;


public interface IChargeFactoryResolver
{
	public IChargeFactory resolve(String name);
}
