package bacond.timeslicer.svc.upgrade;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;

import bacond.lib.restlet.ToStringRepr;
import bacond.lib.util.ITransform;
import bacond.lib.util.MapMaker;
import bacond.lib.util.Transforms;
import bacond.timeslicer.app.upgrade.UpgradeInfo;
import bacond.timeslicer.app.upgrade.Upgrader;
import bacond.timeslicer.restletservice.MyApp;
import bacond.timeslicer.svc.task.StartTagsResource;
import bacond.timeslicer.timeslice.TimesliceApp;

public class UpgradeInfoResource extends Resource
{
	private final Map<MediaType, ITransform<UpgradeInfo, Representation>> versionRenderers = MapMaker.create(new LinkedHashMap<MediaType, ITransform<UpgradeInfo, Representation>>())
		.put(MediaType.TEXT_HTML, Transforms.compose(
				new ITransform<UpgradeInfo, String>()
				{
					@Override
					public String apply(UpgradeInfo r)
					{
						return String.format(
								"" +
								" <p>%2$s</p>" +
								" <p><small>Released: %s</small></p>" +
								" <p><a href=\"?action=restart\">[download, install & restart]</a></p>" +
								"",
								r.getReleaseTime(),
								r.getDownloadUrl(),
								r.getTags());
					}
				},
				new ToStringRepr(MediaType.TEXT_HTML)
				))
		.getMap();

	public UpgradeInfoResource(Context context, Request request, Response response)
	{
		super(context, request, response);

		for (MediaType mediaType: versionRenderers.keySet())
		{
			getVariants().add(new Variant(mediaType));
		}
	}

	protected MyApp getMyApp()
	{
		return (MyApp) getApplication();
	}

	protected TimesliceApp getTimesliceApp()
	{
		return getMyApp().getTimesliceApp();
	}

	@Override
	public Representation represent(Variant variant) throws ResourceException
	{
		Upgrader upgrader = new Upgrader(getTimesliceApp().getUpdateUrl(), getTimesliceApp().getSafeDir());

		List<UpgradeInfo> versions = Collections.emptyList();
		try
		{
			versions = upgrader.getLatestUpgradeInfo();
		}
		catch (Exception e1)
		{
			System.err.println("Could not retrieve upgrades.");
		}

		UpgradeInfo thisInfo = null;

		Instant releasedTime = StartTagsResource.parseInstantIfAvailable((String) getRequest().getAttributes().get("versionId"));
		System.out.println("Looking for version: " + releasedTime);
		for (UpgradeInfo info: versions)
		{
			if (releasedTime.equals(info.getReleaseTime()))
			{
				System.out.println(" Found version: " + info.getReleaseTime());
				thisInfo = info;
			}
			else
			{
				System.out.println(" Not same version: " + info.getReleaseTime());
			}
		}

		if (null != thisInfo)
		{
			String action = (String) getRequest().getAttributes().get("action");

			System.out.println(" action is to '" + action + "'");

			if (false)
			{
			}
			else if ("download".equals(action))
			{
				try
				{
					upgrader.download(thisInfo);
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
					File downloadedFile = upgrader.download(thisInfo);
					upgrader.extract(downloadedFile);
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
					File downloadedFile = upgrader.download(thisInfo);
					File extractedFolder = upgrader.extract(downloadedFile);
					getMyApp().restart(extractedFolder.getName());
				}
				catch (Exception e)
				{
					throw new ResourceException(e);
				}
			}
			else
			{
				// no recognized action.
			}
		}
		else
		{
			// no upgrade-info found.
		}

		return versionRenderers.get(variant.getMediaType()).apply(thisInfo);
	}
}
