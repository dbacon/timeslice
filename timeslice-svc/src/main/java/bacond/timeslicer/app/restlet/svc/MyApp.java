package bacond.timeslicer.app.restlet.svc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.resource.Directory;
import org.restlet.resource.Finder;
import org.restlet.routing.Route;
import org.restlet.routing.Router;
import org.restlet.security.Guard;
import org.restlet.util.Variable;

import bacond.timeslicer.app.auth.AclFile;
import bacond.timeslicer.app.generic.GenericStore;
import bacond.timeslicer.app.javaresource.resource.JavaResourceResource;
import bacond.timeslicer.app.task.api.StartTag;
import bacond.timeslicer.app.task.resource.StartTagResource;
import bacond.timeslicer.app.task.resource.StartTagsResource;
import bacond.timeslicer.app.todo.api.ITodoItemStoreProvider;
import bacond.timeslicer.app.todo.api.TodoItem;
import bacond.timeslicer.app.todo.resource.TodoItemListingResource;
import bacond.timeslicer.app.upgrade.resource.UpgradeInfoResource;
import bacond.timeslicer.app.upgrade.resource.UpgradeInfosResource;
import bacond.timeslicer.ui.cli.SumEntry;

public class MyApp extends Application implements ITodoItemStoreProvider
{
	private static final String Key_Upgrade = "upgrade";
	private final GenericStore<StartTag> startTagStore = new GenericStore<StartTag>();
	private final GenericStore<TodoItem> todoStore = new GenericStore<TodoItem>();
	private final Map<String, String> users = new LinkedHashMap<String, String>();
	private final String localRootUri;
	private final String aclFileName;
	private final String safeDir;
	private final String updateUrl;

	public GenericStore<StartTag> getStartTagStore()
	{
		return startTagStore;
	}

	@Override
	public GenericStore<TodoItem> getTodoItemStore()
	{
		return todoStore;
	}

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

	public Map<String, String> getUsers()
	{
		return users;
	}

	public String getSafeDir()
	{
		return safeDir;
	}

	private File findBackupFile(String key)
	{
		return new File(FilenameUtils.concat(getSafeDir(), "backup-" + key + ".dat"));
	}

	public void snapshot(String key) throws IOException
	{
		writeBackup("snapshot-" + ISODateTimeFormat.dateTime().print(new Instant()) + "-" + key);
	}

	public void restart(String rebootTo)
	{
		try
		{
			writeBackup(Key_Upgrade);

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

	private void writeBackup(String key) throws IOException
	{
		List<String> lines = new ArrayList<String>(startTagStore.getAllItems().size());

		for (StartTag tag: startTagStore.getAllItems())
		{
			lines.add(SumEntry.toLine(tag));
		}

		FileUtils.writeLines(findBackupFile(key), lines);
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

		Route resRoute = router.attach("/resources/{resPath}", JavaResourceResource.class);
		resRoute.getTemplate().getVariables().put("resPath", new Variable(Variable.TYPE_ALL));

		Directory directory = new Directory(getContext().createChildContext(), localRootUri);
		directory.setListingAllowed(true);
		directory.setIndexName("index.html");

		router.attach("/", directory);

		Route versionsRoute = router.attach("/version", UpgradeInfosResource.class);
		versionsRoute.extractQuery("action", "action", true);
		versionsRoute.extractQuery("filter", "filter", true);

		Route versionRoute = router.attach("/version/{versionId}", UpgradeInfoResource.class);
		versionRoute.getTemplate().getVariables().put("versionId", new Variable(Variable.TYPE_URI_PATH));
		versionRoute.extractQuery("action", "action", true);

		Route todoRoute = router.attach("/todo", TodoItemListingResource.class);
		todoRoute.extractQuery("hello", "hello", true);

		return router;
	}

	public MyApp preLoadFromFile(boolean doPreload)
	{
		if (doPreload)
		{
			File backupFile = findBackupFile(Key_Upgrade);
			try
			{
				List<StartTag> preloadItems = SumEntry.readItems(new FileInputStream(backupFile));

				startTagStore.enterAllTags(preloadItems);

				System.out.println("Pre-loaded " + preloadItems.size() + " item(s) from '" + backupFile + "'.");
			}
			catch (IOException e)
			{
				System.err.println("Could not pre-load file '" + backupFile + "': " + e.getMessage());
			}
		}
		return this;
	}

}
