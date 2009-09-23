package bacond.lib.restlet;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import bacond.lib.util.ITransform;
import bacond.lib.util.Transforms;

public class ToStringRepr implements ITransform<String, Representation>
{
	private final MediaType mediaType;

	public static <T> ITransform<T, Representation> create(MediaType mediaType, ITransform<T, String> stringifier)
	{
		return Transforms.compose(stringifier, new ToStringRepr(mediaType));
	}

	public ToStringRepr(MediaType mediaType)
	{
		this.mediaType = mediaType;
	}

	@Override
	public Representation apply(String r)
	{
		StringRepresentation representation = new StringRepresentation(r);
		representation.setMediaType(mediaType);
		return representation;
	}
}
