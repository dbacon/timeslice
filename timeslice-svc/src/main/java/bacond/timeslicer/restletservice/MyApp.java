package bacond.timeslicer.restletservice;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
import bacond.timeslicer.svc.javaresource.JavaResourceResource;
import bacond.timeslicer.svc.rolodex.RolodexResource;
import bacond.timeslicer.svc.task.StartTagResource;
import bacond.timeslicer.svc.task.StartTagsResource;
import bacond.timeslicer.svc.todo.TodoItemListingResource;
import bacond.timeslicer.svc.upgrade.UpgradeInfoResource;
import bacond.timeslicer.svc.upgrade.UpgradeInfosResource;
import bacond.timeslicer.timeslice.TimesliceApp;

public class MyApp extends Application
{
	private final Map<String, String> users = new LinkedHashMap<String, String>();
	private final String localRootUri;
	private final TimesliceApp timesliceApp;

	public MyApp(Context context, String localRootUri, TimesliceApp timesliceApp)
	{
		super(context);

		this.timesliceApp = timesliceApp;
		this.localRootUri = localRootUri;
	}

	public TimesliceApp getTimesliceApp()
	{
		return timesliceApp;
	}

	public Map<String, String> getUsers()
	{
		return users;
	}

	public void restart(String rebootTo)
	{
		try
		{
			getTimesliceApp().writeBackup(TimesliceApp.Key_Upgrade);

			if (rebootTo.contains(File.separator))
			{
				throw new RuntimeException("Restart location name cannot contain a '" + File.separator + "'.");
			}

			String newRoot = FilenameUtils.concat(getTimesliceApp().getSafeDir(), rebootTo);
			FileUtils.writeStringToFile(new File("/home/dbacon/.timeslice.nextroot"), newRoot);
			System.exit(42);
		}
		catch (Exception e)
		{
			System.err.println("Could not write root, to restart: " + e.getMessage());
		}
	}

	private Restlet aclProtectedFinder(Class<?> targetClass)
	{
		Guard guard = new Guard(
				getContext(),
				ChallengeScheme.HTTP_BASIC,
				"Items managing.");

		guard.setSecretResolver(new AclFile(getTimesliceApp().getAclFileName()));

		guard.setNext(new Finder(getContext(), targetClass));

		return guard;
	}

	@Override
	public Restlet createRoot()
	{
		Router router = new Router(getContext());

		addItemsRoute(router);
		addItemRoute(router);

		addResourcesRoute(router);

		addStaticFilesRoute(router);

		addVersionsRoute(router);
		addVersionRoute(router);

		addTodoRoute(router);

		addRolodexesRoute(router);

		return router;
	}

	private void addRolodexesRoute(Router router)
	{
		router.attach("/rolodex", RolodexResource.class);
	}

	private void addTodoRoute(Router router)
	{
		Route todoRoute = router.attach("/todo", TodoItemListingResource.class);
		todoRoute.extractQuery("hello", "hello", true);
	}

	private void addVersionRoute(Router router)
	{
		Route versionRoute = router.attach("/version/{versionId}", UpgradeInfoResource.class);
		versionRoute.getTemplate().getVariables().put("versionId", new Variable(Variable.TYPE_URI_PATH));
		versionRoute.extractQuery("action", "action", true);
	}

	private void addVersionsRoute(Router router)
	{
		Route versionsRoute = router.attach("/version", UpgradeInfosResource.class);
		versionsRoute.extractQuery("action", "action", true);
		versionsRoute.extractQuery("filter", "filter", true);
	}

	private void addStaticFilesRoute(Router router)
	{
		Directory directory = new Directory(getContext().createChildContext(), localRootUri);
		directory.setListingAllowed(true);
		directory.setIndexName("index.html");

		router.attach("/", directory);
	}

	private void addResourcesRoute(Router router)
	{
		Route resRoute = router.attach("/resources/{resPath}", JavaResourceResource.class);
		resRoute.getTemplate().getVariables().put("resPath", new Variable(Variable.TYPE_ALL));
	}

	private void addItemRoute(Router router)
	{
		router.attach("/items/{when}", aclProtectedFinder(StartTagResource.class));
	}

	private void addItemsRoute(Router router)
	{
		Route route = router.attach("/items", aclProtectedFinder(StartTagsResource.class));

		for (String queryParam: StartTagsResource.QueryParamNameList)
		{
			route.extractQuery(queryParam, queryParam, true);
		}
	}
}
