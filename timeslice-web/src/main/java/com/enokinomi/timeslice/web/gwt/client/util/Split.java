package bacond.timeslice.web.gwt.client.util;

import java.util.Arrays;
import java.util.List;



public class Split implements Tx<String, List<String>>
{
    private final String regex;

    public Split(String regex)
    {
        this.regex = regex;
    }

    @Override
    public List<String> apply(String name)
    {
        return Arrays.asList(name.split(regex));
    }
}
