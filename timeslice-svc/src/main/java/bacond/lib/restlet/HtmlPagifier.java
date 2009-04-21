package bacond.lib.restlet;

import bacond.lib.util.ITransform;
import bacond.lib.util.Transforms;

public class HtmlPagifier implements ITransform<String, String>
{
	public static <T> ITransform<T, String> pagify(ITransform<T, String> htmlSnippetMaker)
	{
		return Transforms.compose(htmlSnippetMaker, new HtmlPagifier());
	}

//	private final String htmlSnippet;
//
//	public HtmlPagifier(String htmlSnippet)
//	{
//		this.htmlSnippet = htmlSnippet;
//	}

	@Override
	public String apply(String r)
	{
		return new StringBuilder()
			.append("<html><head></head><body>")
			.append(r)
			.append("</body></html>")
			.toString();
	}

}
