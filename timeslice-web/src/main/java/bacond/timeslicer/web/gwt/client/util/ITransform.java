package bacond.timeslicer.web.gwt.client.util;

public interface ITransform<R, D>
{
	D apply(R r);
}