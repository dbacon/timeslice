package bacond.timeslice.web.gwt.client.beans;

public class TaskTotal
{
	private String who;
	private Double durationMillis;
	private String what;
	
	public TaskTotal()
	{
		this(null, null, null);
	}
	
	public TaskTotal(String who, Double durationMillis, String what)
	{
		this.who = who;
		this.durationMillis = durationMillis;
		this.what = what;
	}

	public String getWho()
	{
		return who;
	}

	public void setWho(String who)
	{
		this.who = who;
	}

	public Double getDurationMillis()
	{
		return durationMillis;
	}

	public void setDurationMillis(Double durationMillis)
	{
		this.durationMillis = durationMillis;
	}

	public String getWhat()
	{
		return what;
	}

	public void setWhat(String what)
	{
		this.what = what;
	}
}
