package bacond.timeslicer.app.restlet.resource;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.Instant;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Directory;
import org.restlet.Restlet;
import org.restlet.Router;

import bacond.timeslicer.app.dto.StartTag;

public class MyApp extends Application
{
	private final Map<Instant, StartTag> startTags = new LinkedHashMap<Instant, StartTag>();
	private final Map<String, String> users = new LinkedHashMap<String, String>();
	private final String localRootUri;

	public MyApp(Context context, String localRootUri)
	{
		super(context);
		this.localRootUri = localRootUri;
	}
	
	@Override
	public Restlet createRoot()
	{
		Router router = new Router(getContext().createChildContext());
		
		router
			.attach("/items", StartTagsResource.class)
			.extractQuery("sortcol", "sortcol", true)
			.extractQuery("sortdir", "sortdir", true)
			.extractQuery("max", "max", true)
			.extractQuery("enrich", "enrich", true)
			.extractQuery("mediatype", "mediatype", true)
			;
		
		router.attach("/items/{when}", StartTagResource.class);
		
		
		Directory directory = new Directory(getContext().createChildContext(), localRootUri);
		directory.setListingAllowed(true);
		directory.setIndexName("index.html");

//		route.attach("/forms", directory);
		router.attach("/", directory);
		
		return router;
	}

	public Map<Instant, StartTag> getStartTags()
	{
		return startTags;
	}

	public Map<String, String> getUsers()
	{
		return users;
	}
}
