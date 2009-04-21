package bacond.timeslicer.app.generic;



public interface IStoreProvider<T extends IHasWhen>
{
	GenericStore<T> getStore();
}