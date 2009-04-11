package bacond.timeslicer.app.restlet.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
import org.restlet.util.Variable;

import bacond.lib.util.Check;
import bacond.timeslicer.app.auth.AclFile;
import bacond.timeslicer.app.dto.StartTag;
import bacond.timeslicer.app.processing.Split;
import bacond.timeslicer.ui.cli.SumEntry;

public class MyApp extends Application
{
	private final Map<Instant, StartTag> startTags = new LinkedHashMap<Instant, StartTag>();
	private final Map<String, String> users = new LinkedHashMap<String, String>();
	private final String localRootUri;
	private final String aclFileName;
	private final String safeDir;
	private final String updateUrl;

	public MyApp(Context context, String localRootUri, String aclFileName, String safeDir, String updateUrl)
	{
		super(context);
		
		this.localRootUri = localRootUri;
		this.aclFileName = aclFileName;
		this.safeDir = safeDir;
		this.updateUrl = updateUrl;
	}
	
	public String getUpdateUrl()
	{
		return updateUrl;
	}

	public Map<Instant, StartTag> getStartTags()
	{
		return startTags;
	}

	public Map<String, String> getUsers()
	{
		return users;
	}

	public String getSafeDir()
	{
		return safeDir;
	}

	private File findBackupFile()
	{
		return new File(FilenameUtils.concat(getSafeDir(), "backup.dat"));
	}

	public void restart(String rebootTo)
	{
		List<String> lines = new ArrayList<String>(getStartTags().values().size());
		for (StartTag tag: getStartTags().values())
		{
			lines.add(SumEntry.toLine(tag));
		}

		try
		{
			FileUtils.writeLines(findBackupFile(), lines);

			if (rebootTo.contains(File.separator))
			{
				throw new RuntimeException("Restart location name cannot contain a '" + File.separator + "'.");
			}

			String newRoot = FilenameUtils.concat(safeDir, rebootTo);
			FileUtils.writeStringToFile(new File("/home/dbacon/.timeslice.nextroot"), newRoot);
			System.exit(42);
		}
		catch (Exception e)
		{
			System.err.println("Could not write root, to restart: " + e.getMessage());
		}
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
		
		Route resRoute = router.attach("/resources/{resPath}", ResourceResource.class);
		resRoute.getTemplate().getVariables().put("resPath", new Variable(Variable.TYPE_ALL));
		
		Directory directory = new Directory(getContext().createChildContext(), localRootUri);
		directory.setListingAllowed(true);
		directory.setIndexName("index.html");
		
		router.attach("/", directory);
		
		Route versionRoute = router.attach("/version", UpgradeInfoResources.class);
		versionRoute.extractQuery("action", "action", true);
		versionRoute.extractQuery("filter", "filter", true);

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

	public MyApp preLoadFromFile(boolean doPreload)
	{
		if (doPreload)
		{
			File backupFile = findBackupFile();
			try
			{
				List<StartTag> preloadItems = SumEntry.readItems(new FileInputStream(backupFile));

				enterAllTags(preloadItems);

				System.out.println("Pre-loaded " + preloadItems.size() + " item(s) from '" + backupFile + "'.");
			}
			catch (IOException e)
			{
				System.err.println("Could not pre-load file '" + backupFile + "': " + e.getMessage());
			}
		}
		return this;
	}

	public void enterTag(StartTag startTag)
	{
		getStartTags().put(startTag.getWhen(), startTag);
	}

	public void enterAllTags(Collection<? extends StartTag> all)
	{
		for (StartTag tag: all)
		{
			enterTag(tag);
		}
	}
}
