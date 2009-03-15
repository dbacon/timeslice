package bacond.timeslicer.app.restlet.resource;

import java.util.LinkedHashMap;
import java.util.Map;

import org.joda.time.Instant;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Directory;
import org.restlet.Finder;
import org.restlet.Guard;
import org.restlet.Restlet;
import org.restlet.Router;
import org.restlet.data.ChallengeScheme;

import bacond.timeslicer.app.auth.AclFile;
import bacond.timeslicer.app.dto.StartTag;

public class MyApp extends Application
{
	private final Map<Instant, StartTag> startTags = new LinkedHashMap<Instant, StartTag>();
	private final Map<String, String> users = new LinkedHashMap<String, String>();
	private final String localRootUri;
	private final String aclFileName;

	public MyApp(Context context, String localRootUri, String aclFileName)
	{
		super(context);
		this.localRootUri = localRootUri;
		this.aclFileName = aclFileName;
	}
	
	@Override
	public Restlet createRoot()
	{
		Router router = new Router(getContext().createChildContext());
		
		Guard guard0 = new Guard(getContext().createChildContext(), ChallengeScheme.HTTP_BASIC, "Hello.");
		guard0.setSecretResolver(new AclFile(aclFileName));
		guard0.setNext(new Finder(router.getContext(), StartTagsResource.class));
		
		Guard guard1 = new Guard(getContext().createChildContext(), ChallengeScheme.HTTP_BASIC, "Hello.");
		guard1.setSecretResolver(new AclFile(aclFileName));
		guard1.setNext(new Finder(router.getContext(), StartTagResource.class));

		router
			.attach("/items", guard0)
			.extractQuery("sortcol", "sortcol", true)
			.extractQuery("sortdir", "sortdir", true)
			.extractQuery("max", "max", true)
			.extractQuery("enrich", "enrich", true)
			.extractQuery("mediatype", "mediatype", true)
			;
		
		router
			.attach("/items/{when}", guard1);
		
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
