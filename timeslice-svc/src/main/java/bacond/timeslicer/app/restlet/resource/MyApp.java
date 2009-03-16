package bacond.timeslicer.app.restlet.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Directory;
import org.restlet.Finder;
import org.restlet.Guard;
import org.restlet.Restlet;
import org.restlet.Route;
import org.restlet.Router;
import org.restlet.data.ChallengeScheme;

import bacond.lib.util.Check;
import bacond.timeslicer.app.auth.AclFile;
import bacond.timeslicer.app.dto.StartTag;
import bacond.timeslicer.app.processing.Split;

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
	
	public Map<Instant, StartTag> getStartTags()
	{
		return startTags;
	}

	public Map<String, String> getUsers()
	{
		return users;
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

		Route route = router.attach("/items", guard0);

		for (String queryParam: StartTagsResource.QueryParamNameList)
		{
			route.extractQuery(queryParam, queryParam, true);
		}

		router.attach("/items/{when}", guard1);
		
		Directory directory = new Directory(getContext().createChildContext(), localRootUri);
		directory.setListingAllowed(true);
		directory.setIndexName("index.html");
		
		router.attach("/", directory);
		
		return router;
	}
	
	public List<StartTag> getMeSomeTags(Instant minInstant, Instant maxInstant, boolean sortReverse, int pageSize, int pageIndex)
	{
		Check.notNull(minInstant, "minimum instant");
		Check.notNull(maxInstant, "maximum instant");
		
		List<StartTag> items = new ArrayList<StartTag>(getStartTags().values());
		List<StartTag> itemsInRange = new ArrayList<StartTag>();
		
		for (StartTag item: items)
		{
			if (true
					&& (item.getWhen().isEqual(minInstant) || item.getWhen().isAfter(minInstant))
					&& (item.getWhen().isEqual(maxInstant) || item.getWhen().isBefore(maxInstant)))
			{
				itemsInRange.add(item);
			}
		}

		Comparator<StartTag> cmp = CompareByTime.Instance;
		
		if (sortReverse)
		{
			cmp = Collections.reverseOrder(cmp);
		}
		
		Collections.sort(itemsInRange, cmp);
		
		if (pageIndex < 0)
		{
			throw new RuntimeException("Page Index cannot be negative.");
		}
		
		if (pageSize <= 0)
		{
			throw new RuntimeException("Page Size is not positive.");
		}
		
		// pageIndex 0..maxint
		// pagesize 1..maxint

		itemsInRange = itemsInRange.subList(Math.min(itemsInRange.size(), pageIndex*pageSize), Math.min(itemsInRange.size(), (pageIndex + 1)*pageSize));

		List<StartTag> independentItems = new Split().split(itemsInRange, new Instant());

		return independentItems;
	}
}
