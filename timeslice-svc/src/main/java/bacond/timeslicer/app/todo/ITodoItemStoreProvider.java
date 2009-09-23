package bacond.timeslicer.app.todo;

import bacond.timeslicer.app.generic.GenericStore;


public interface ITodoItemStoreProvider
{
	GenericStore<TodoItem> getTodoItemStore();
}
