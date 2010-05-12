/**
 *
 */
package bacond.timeslice.web.gwt.client.widget;

public interface IOptionsProvider
{
    public static String CurrentTaskToken = "@current.task@";

    int getMaxSize();
    boolean isControlSpaceSends();
    boolean isCurrentTaskInTitlebar();
    boolean isAutoRefresh();
    int getAutoRefreshMs();
    String getTitleBarTemplate();

}
