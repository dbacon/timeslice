package bacond.timeslice.web.gwt.server.rpc;

import javax.servlet.ServletContext;

public class InitParamUtils
{
    public static Integer parseIntegerOrDefault(ServletContext context, String name, Integer defaultValue)
    {
        try
        {
            return Integer.valueOf(context.getInitParameter(name));
        }
        catch (NumberFormatException e)
        {
            System.err.println("Init-param '" + name + "' not found, using value '" + defaultValue + "'.");
            return defaultValue;
        }
    }

    public static String msgIfMissing(ServletContext context, String name, String messageOnMissing)
    {
        String value = context.getInitParameter(name);

        if (null == value)
        {
            System.err.println(messageOnMissing);
        }

        return value;
    }
}
