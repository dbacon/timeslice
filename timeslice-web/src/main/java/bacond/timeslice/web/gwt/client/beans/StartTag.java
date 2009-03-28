package bacond.timeslice.web.gwt.client.beans;

public class StartTag
{
	private String instantString;
	private String untilString;
	private Double durationMillis;
	private String description;
	
	public StartTag()
	{
		this(null, null, null, null);
	}
	
	public StartTag(String instantString, String untilString, Double durationMillis, String description)
	{
		this.instantString = instantString;
		this.untilString = untilString;
		this.durationMillis = durationMillis;
		this.description = description;
	}

	public String getInstantString()
	{
		return instantString;
	}

	public String getUntilString()
	{
		return untilString;
	}

	public void setUntilString(String untilString)
	{
		this.untilString = untilString;
	}

	public Double getDurationMillis()
	{
		return durationMillis;
	}

	public void setDurationMillis(Double durationMillis)
	{
		this.durationMillis = durationMillis;
	}

	public void setInstantString(String instantString)
	{
		this.instantString = instantString;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

}
