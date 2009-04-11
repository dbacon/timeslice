package bacond.timeslicer.app.restlet.resource;

import java.io.File;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

import bacond.timeslicer.app.upgrade.UpgradeInfo;
import bacond.timeslicer.app.upgrade.Upgrader;

public class UpgradeInfoResources extends Resource
{
	public UpgradeInfoResources(Context context, Request request, Response response)
	{
		super(context, request, response);

		getVariants().add(new Variant(MediaType.TEXT_PLAIN));
	}

	protected MyApp getMyApp()
	{
		return (MyApp) getApplication();
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException
	{
		UpgradeInfo latestUpgradeInfo = null;
		String msg = "-uninitialized-";

		Upgrader upgrader = new Upgrader(getMyApp().getSafeDir());

		try
		{
			latestUpgradeInfo = upgrader.getLatestUpgradeInfo();
		}
		catch (Exception e)
		{
			msg = "Could not get upgrade information: " + e.getMessage();
		}

		String action = (String) getRequest().getAttributes().get("action");

		if (false)
		{
		}
		else if ("download".equals(action))
		{
			try
			{
				upgrader.download(latestUpgradeInfo);
				msg = "downloaded.";
			}
			catch (Exception e)
			{
				throw new ResourceException(e);
			}
		}
		else if ("extract".equals(action))
		{
			try
			{
				File downloadedFile = upgrader.download(latestUpgradeInfo);
				upgrader.extract(downloadedFile);
				msg = "downloaded, extracted.";
			}
			catch (Exception e)
			{
				throw new ResourceException(e);
			}
		}
		else if ("restart".equals(action))
		{
			try
			{
				File downloadedFile = upgrader.download(latestUpgradeInfo);
				File extractedFolder = upgrader.extract(downloadedFile);
				getMyApp().restart(extractedFolder.getName());
				msg = "downloaded, extracted.restarting.";
			}
			catch (Exception e)
			{
				throw new ResourceException(e);
			}
		}
		else
		{
			msg = String.format(
					"%2$s released at %1$s<br/>" +
					"<ul>" +
					"  <li><a href=\"?action=download\">Download only</a></li>" +
					"  <li><a href=\"?action=upgrade\">Upgrade now</a></li>" +
					"  <li><a href=\"%2$s\">Download locally</a></li>" +
					"</ul>" +
					"<p>safe-dir is <tt>%3$s</tt></p>",
					latestUpgradeInfo.getReleaseTime(),
					latestUpgradeInfo.getDownloadUrl(),
					upgrader.getSafeDir());
		}

		return new StringRepresentation(msg, MediaType.TEXT_HTML);
	}
}
