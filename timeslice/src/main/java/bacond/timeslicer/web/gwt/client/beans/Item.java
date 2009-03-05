package bacond.timeslicer.web.gwt.client.beans;

public class Item
{
	private String key;
	private String project;
	
	public Item()
	{
		this(null, null);
	}
	
	public Item(String key, String project)
	{
		this.key = key;
		this.project = project;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getProject()
	{
		return project;
	}

	public void setProject(String project)
	{
		this.project = project;
	}
	
}
