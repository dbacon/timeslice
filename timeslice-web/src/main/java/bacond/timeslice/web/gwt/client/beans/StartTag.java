package bacond.timeslice.web.gwt.client.beans;

import java.io.Serializable;

public class StartTag implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String instantString;
    private String untilString;
    private Double durationMillis;
    private String description;
    private Boolean past;

    public StartTag()
    {
        this(null, null, null, null, null);
    }

    public StartTag(String instantString, String untilString, Double durationMillis, String description, Boolean past)
    {
        this.instantString = instantString;
        this.untilString = untilString;
        this.durationMillis = durationMillis;
        this.description = description;
        this.past = past;
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

    public Boolean getPast()
    {
        return past;
    }

    public void setPast(Boolean past)
    {
        this.past = past;
    }

}
