package com.enokinomi.timeslice.web.gwt.server.rpc;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.enokinomi.timeslice.web.gwt.client.beans.AssignedTaskTotal;
import com.enokinomi.timeslice.web.gwt.client.server.IAssignmentSvc;
import com.enokinomi.timeslice.web.gwt.client.server.ProcType;
import com.enokinomi.timeslice.web.gwt.client.server.SortDir;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@Deprecated
public class AssignmentSvcServlet extends RemoteServiceServlet implements IAssignmentSvc, INeedsInjectionHelp
{
    public static final String TIMESLICE_SERVLET_ASSIGNMENT = "timeslice.servlet.assignment";

    private static final long serialVersionUID = 1L;

    private IAssignmentSvc assignmentSvc;

    public IAssignmentSvc getAssignmentSvc()
    {
        return assignmentSvc;
    }

    public void setAssignmentSvc(IAssignmentSvc assignmentSvc)
    {
        this.assignmentSvc = assignmentSvc;
    }

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        Injector injector = (Injector) config.getServletContext().getAttribute(TimesliceStartupServletContextListener.INJECTOR_SVC);
        injector.inject(this);
    }

    @Override
    public void assign(String authToken, String description, String billTo)
    {
        getAssignmentSvc().assign(authToken, description, billTo);
    }

    @Override
    public String lookup(String authToken, String description, String valueWhenAssignmentNotFound)
    {
        return getAssignmentSvc().lookup(authToken, description, valueWhenAssignmentNotFound);
    }

    @Override
    public List<AssignedTaskTotal> refreshTotals(String authToken, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        return getAssignmentSvc().refreshTotals(authToken, maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords);
    }

    @Override
    public void inject(AuthenticatedTimesliceSvc authenticatedTimesliceSvc, AssignmentSvcSession assignmentSvcSession)
    {
        this.setAssignmentSvc(assignmentSvcSession);
    }
}
