package com.enokinomi.timeslice.web.gwt.server.rpc;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.eclipse.jetty.util.log.Log;

import com.enokinomi.timeslice.web.gwt.client.beans.NotAuthenticException;
import com.enokinomi.timeslice.web.gwt.client.beans.StartTag;
import com.enokinomi.timeslice.web.gwt.client.beans.TaskTotal;
import com.enokinomi.timeslice.web.gwt.client.server.ITimesliceSvc;
import com.enokinomi.timeslice.web.gwt.client.server.ProcType;
import com.enokinomi.timeslice.web.gwt.client.server.SortDir;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Deprecated
@Singleton
public class TimesliceSvcServlet extends RemoteServiceServlet implements ITimesliceSvc, INeedsInjectionHelp
{
    public static final String TIMESLICE_SERVLET_TIMESLICE = "timeslice.servlet.timeslice";

    private static final long serialVersionUID = 1L;

    private ITimesliceSvc timesliceSvc;

    public TimesliceSvcServlet()
    {
    }

    @Inject
    public TimesliceSvcServlet(ITimesliceSvc timesliceSvc)
    {
        this.timesliceSvc = timesliceSvc;
    }

    public ITimesliceSvc getTimesliceSvc()
    {
        return timesliceSvc;
    }

    public void setTimesliceSvc(ITimesliceSvc timesliceSvc)
    {
        this.timesliceSvc = timesliceSvc;
    }

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        if (null == timesliceSvc)
        {
            Injector injector = (Injector) config.getServletContext().getAttribute(TimesliceStartupServletContextListener.INJECTOR_SVC);
            injector.inject(this);
        }
        else
        {
            Log.info(" servlet.init found servlet already injected! great!");
        }
    }

    @Override
    public String serverInfo()
    {
        return getTimesliceSvc().serverInfo();
    }

    @Override
    public String authenticate(String username, String password)
    {
        return getTimesliceSvc().authenticate(username, password);
    }

    @Override
    public void logout(String authToken) throws NotAuthenticException
    {
        getTimesliceSvc().logout(authToken);
    }

    @Override
    public List<StartTag> refreshItems(String authToken, int maxSize,
            SortDir sortDir, ProcType procType, String startingInstant,
            String endingInstant) throws NotAuthenticException
    {
        return getTimesliceSvc().refreshItems(authToken, maxSize, sortDir, procType, startingInstant, endingInstant);
    }

    @Override
    public List<TaskTotal> refreshTotals(String authToken, int maxSize,
            SortDir sortDir, ProcType procType, String startingInstant,
            String endingInstant, List<String> allowWords,
            List<String> ignoreWords) throws NotAuthenticException
    {
        return getTimesliceSvc().refreshTotals(authToken, maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords);
    }

    @Override
    public void addItem(String authToken, String instantString,
            String taskDescription) throws NotAuthenticException
    {
        getTimesliceSvc().addItem(authToken, instantString, taskDescription);
    }

    @Override
    public void addItems(String authToken, List<StartTag> items)
    {
        getTimesliceSvc().addItems(authToken, items);
    }

    @Override
    public void update(String authToken, StartTag editedStartTag)
            throws NotAuthenticException
    {
        getTimesliceSvc().update(authToken, editedStartTag);
    }

    @Override
    public String persistTotals(String authToken, String persistAsName,
            int maxSize, SortDir sortDir, ProcType procType,
            String startingInstant, String endingInstant,
            List<String> allowWords, List<String> ignoreWords)
            throws NotAuthenticException
    {
        return getTimesliceSvc().persistTotals(authToken, persistAsName, maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords);
    }

    @Override
    public void inject(AuthenticatedTimesliceSvc authenticatedTimesliceSvc, AssignmentSvcSession assignmentSvcSession)
    {
        setTimesliceSvc(authenticatedTimesliceSvc);
    }
}
