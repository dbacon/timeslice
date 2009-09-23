package bacond.timeslicer.app.periodbilling.api;

public class BillableTotal
{
	public static final String Unbilled = "";

	private final String description;
	private final long millis;

	private String billedTo = Unbilled;

	public BillableTotal(String description, long millis)
	{
		this(description, millis, Unbilled);
	}

	public BillableTotal(String description, long millis, String billTo)
	{
		this.description = description;
		this.millis = millis;
		this.billedTo = billTo;
	}

	public String getBilledTo()
	{
		return billedTo;
	}

	public BillableTotal setBilledTo(String billTo)
	{
		this.billedTo = billTo;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public long getMillis()
	{
		return millis;
	}
}
