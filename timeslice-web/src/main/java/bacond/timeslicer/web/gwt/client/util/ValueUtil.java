/**
 * 
 */
package bacond.timeslicer.web.gwt.client.util;

import com.google.gwt.user.client.ui.HasText;

public class ValueUtil
{
	public static IReadableWritableValue<String> asReadableWritable(final HasText hasText)
	{
		return new IReadableWritableValue<String>()
		{
			public String getValue()
			{
				return hasText.getText();
			}

			public void setValue(String value)
			{
				hasText.setText(value);
			}
		};
	}
}