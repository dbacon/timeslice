package bacond.timeslicer.app.resource;

import java.util.LinkedHashMap;
import java.util.Map;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Directory;
import org.restlet.Restlet;
import org.restlet.Router;

import bacond.timeslicer.app.dto.Item;

public class MyApp extends Application
{
	private final Map<String, Item> items = new LinkedHashMap<String, Item>();
	private final Map<String, String> users = new LinkedHashMap<String, String>();

	public MyApp(Context context)
	{
		super(context);
	}
	
	@Override
	public Restlet createRoot()
	{
		Router route = new Router(getContext().createChildContext());
		
		route.attach("/items", ItemsResource.class);
		route.attach("/items/{itemId}", ItemResource.class);
		
//		String mappedLocation = "file:///C:/Documents and Settings/dbacon/Desktop/java/eclipse-workspaces-SR1/ws-0/timeslice/src/main/webapp/forms";
		String mappedLocation = "file:///C:/Documents and Settings/dbacon/Desktop/java/eclipse-workspaces-SR1/ws-0/timeslice/target/timeslice-0.0.1-SNAPSHOT";
		
		Directory directory = new Directory(getContext().createChildContext(), mappedLocation);
		directory.setListingAllowed(true);
		directory.setIndexName("index.html");

//		route.attach("/forms", directory);
		route.attach("/", directory);
		
		return route;
	}

	public Map<String, Item> getItems()
	{
		return items;
	}

	public Map<String, String> getUsers()
	{
		return users;
	}
}
