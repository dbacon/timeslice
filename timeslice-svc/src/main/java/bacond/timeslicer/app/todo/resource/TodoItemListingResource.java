package bacond.timeslicer.app.todo.resource;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

import bacond.timeslicer.app.generic.GenericStore;
import bacond.timeslicer.app.generic.IStoreProvider;
import bacond.timeslicer.app.generic.ListableResource;
import bacond.timeslicer.app.todo.api.ITodoItemStoreProvider;
import bacond.timeslicer.app.todo.api.TodoItem;

public class TodoItemListingResource extends ListableResource<TodoItem>
{
	public TodoItemListingResource(Context context, Request request, Response response)
	{
		super(context, request, response);
	}

	@Override
	public IStoreProvider<TodoItem> getStoreProvider()
	{
		return new IStoreProvider<TodoItem>()
		{
			@Override
			public GenericStore<TodoItem> getStore()
			{
				return ((ITodoItemStoreProvider) getApplication()).getTodoItemStore();
			}
		};
	}
}
