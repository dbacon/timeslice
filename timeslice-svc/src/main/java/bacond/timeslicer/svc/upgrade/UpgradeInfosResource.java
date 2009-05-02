package bacond.timeslicer.svc.upgrade;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.Resource;
import org.restlet.resource.ResourceException;

import bacond.lib.restlet.BulletedListTextHtmlRenderer;
import bacond.lib.restlet.FormattedStringTextPlainRenderer;
import bacond.lib.restlet.HtmlPagifier;
import bacond.lib.restlet.ToStringRepr;
import bacond.lib.util.ITransform;
import bacond.lib.util.MapMaker;
import bacond.timeslicer.app.upgrade.UpgradeInfo;
import bacond.timeslicer.app.upgrade.Upgrader;
import bacond.timeslicer.restletservice.MyApp;

public class UpgradeInfosResource extends Resource
{
	private final Map<MediaType, ITransform<Collection<UpgradeInfo>, Representation>> versionRenderers = MapMaker.create(new LinkedHashMap<MediaType, ITransform<Collection<UpgradeInfo>, Representation>>())
		.put(MediaType.TEXT_PLAIN, FormattedStringTextPlainRenderer.create(new ITransform<Collection<UpgradeInfo>, String>()
		{
			@Override
			public String apply(Collection<UpgradeInfo> r)
			{
				StringBuilder sb = new StringBuilder("[\n");

				for (UpgradeInfo info: r)
				{
					sb.append("[upgrade ").append(info.getDownloadUrl()).append("]\n");
				}

				return sb.append("]\n").toString();
			}
		}))
		.put(MediaType.TEXT_HTML,
			ToStringRepr.create(MediaType.TEXT_HTML,
					HtmlPagifier.pagify(BulletedListTextHtmlRenderer.create(
							new ITransform<UpgradeInfo, String>()
							{
								@Override
								public String apply(UpgradeInfo r)
								{
									return String.format(
											"<small>%1$s</small> " +
											"<a href=\"%1$s\">%2$s</a> - %3$s<br/>",
											r.getReleaseTime(),
											r.getDownloadUrl(),
											r.getTags());
								}
							}
							))))
		.getMap();

	public UpgradeInfosResource(Context context, Request request, Response response)
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

	@Override
	public Representation represent(Variant variant) throws ResourceException
	{
		Upgrader upgrader = new Upgrader(getMyApp().getUpdateUrl(), getMyApp().getSafeDir());

		try
		{
			return versionRenderers.get(variant.getMediaType()).apply(upgrader.getLatestUpgradeInfo());
		}
		catch (Exception e1)
		{
			throw new ResourceException(e1);
		}
	}
}
