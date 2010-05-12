package bacond.timeslicer.app.core;

import java.util.Comparator;

public class ByWhen implements Comparator<StartTag>
{
    private static final ByWhen singleton = new ByWhen();

    public static ByWhen get()
    {
        return singleton;
    }

    @Override
    public int compare(StartTag o1, StartTag o2)
    {
        return o1.getWhen().compareTo(o2.getWhen());
    }

}
