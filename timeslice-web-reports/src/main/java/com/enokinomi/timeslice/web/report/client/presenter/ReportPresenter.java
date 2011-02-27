package com.enokinomi.timeslice.web.report.client.presenter;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.core.IAssignmentSvcAsync;
import com.enokinomi.timeslice.web.core.client.util.ListenerManager;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.IOnAuthenticated;
import com.enokinomi.timeslice.web.report.client.core.TaskTotal;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class ReportPresenter implements IReportPresenter
{
    private final IAssignmentSvcAsync assignedSvc;

    private final ListenerManager<IReportsPresenterListener> listenerMgr = new ListenerManager<IReportsPresenterListener>();

    private final ILoginSupport loginSupport;
    @Override public Registration addListener(IReportsPresenterListener listener) { return listenerMgr.addListener(listener); }
    protected void fireRefreshTotalsDone(List<TaskTotal> result) { for (IReportsPresenterListener l: listenerMgr.getListeners()) l.onRefreshTotalsDone(result); }
    protected void fireAssignBilleeDone() { for (IReportsPresenterListener l: listenerMgr.getListeners()) l.onAssignBilleeDone(); }
    protected void fireAllBilleesDone(List<String> result) { for (IReportsPresenterListener l: listenerMgr.getListeners()) l.onAllBilleesDone(result); }
    protected void fireRefreshTotalsAssignedDone(List<AssignedTaskTotal> result) { for (IReportsPresenterListener l: listenerMgr.getListeners()) l.onRefreshTotalsAssignedDone(result); }

    @Inject
    public ReportPresenter(IAssignmentSvcAsync assignSvc, ILoginSupport loginSupport)
    {
        this.assignedSvc = assignSvc;
        this.loginSupport = loginSupport;
    }

//    @Override
//    public void startRefreshTotals(final int maxSize, final SortDir sortDir, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
//    {
//        new IOnAuthenticated()
//        {
//            @Override
//            public void runAsync()
//            {
//                assignedSvc.refreshTotals(loginSupport.getAuthToken(),
//                        maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords,
//                        loginSupport.withRetry(this, new AsyncCallback<List<TaskTotal>>()
//                        {
//                            @Override
//                            public void onFailure(Throwable caught)
//                            {
//                            }
//
//                            @Override
//                            public void onSuccess(List<TaskTotal> result)
//                            {
//                                fireRefreshTotalsDone(result);
//                            }
//                        }));
//            }
//        }.runAsync();
//    }

    @Override
    public void startAssignBillee(final String description, final String newBillee)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                assignedSvc.assign(loginSupport.getAuthToken(),
                        description, newBillee,
                        loginSupport.withRetry(this, new AsyncCallback<Void>()
                        {
                            @Override
                            public void onSuccess(Void result)
                            {
                                fireAssignBilleeDone();

                                startGetAllBillees();
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void startGetAllBillees()
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                assignedSvc.getAllBillees(loginSupport.getAuthToken(),
                        loginSupport.withRetry(this, new AsyncCallback<List<String>>()
                        {
                            @Override
                            public void onSuccess(List<String> result)
                            {
                                fireAllBilleesDone(result);
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void startRefreshTotalsAssigned(final int maxSize, final SortDir sortDir, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                assignedSvc.refreshAssignedTotals(loginSupport.getAuthToken(),
                        maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords,
                        loginSupport.withRetry(this, new AsyncCallback<List<AssignedTaskTotal>>()
                        {
                            @Override
                            public void onSuccess(List<AssignedTaskTotal> result)
                            {
                                fireRefreshTotalsAssignedDone(result);
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                            }
                        }));
            }
        }.runAsync();
    }

}
