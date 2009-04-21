package bacond.timeslicer.app.todo.api;

import bacond.timeslicer.app.generic.GenericStore;


public interface ITodoItemStoreProvider
{
	GenericStore<TodoItem> getTodoItemStore();
}