package com.enokinomi.timeslice.web.task.client.controller.impl;


import java.util.List;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.appjob.client.core.IAppJobSvcAsync;
import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.core.IAssignmentSvcAsync;
import com.enokinomi.timeslice.web.core.client.ui.SortDir;
import com.enokinomi.timeslice.web.core.client.util.AsyncResult;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.IOnAuthenticated;
import com.enokinomi.timeslice.web.task.client.core.ITimesliceSvcAsync;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class GwtRpcController extends BaseController
{
    private final ITimesliceSvcAsync svc;
    private final IAssignmentSvcAsync assignedSvc;
    private final IAppJobSvcAsync jobSvc;
    private final ILoginSupport loginSupport;

    @Inject
    GwtRpcController(ITimesliceSvcAsync svc, IAssignmentSvcAsync assignedSvc, IAppJobSvcAsync jobSvc, ILoginSupport loginSupport)
    {
        this.svc = svc;
        this.assignedSvc = assignedSvc;
        this.jobSvc = jobSvc;
        this.loginSupport = loginSupport;
    }

    public void serverInfo()
    {
        svc.serverInfo(new AsyncCallback<String>()
        {
            @Override
            public void onFailure(Throwable caught)
            {
                fireServerInfoRecieved("[server info not available]");
            }

            @Override
            public void onSuccess(String result)
            {
                fireServerInfoRecieved(result);
            }
        });
    }

    public void startGetBranding()
    {
        svc.getBrandInfo(new AsyncCallback<BrandInfo>()
        {
            @Override
            public void onSuccess(BrandInfo result)
            {
                fireBranded(new AsyncResult<BrandInfo>(result, null));
            }

            @Override
            public void onFailure(Throwable caught)
            {
                GWT.log("Could not get branding: " + caught.getMessage());
                fireBranded(new AsyncResult<BrandInfo>(null, caught));
            }
        });
    }


    @Override
    public void startAddItem(final String instantString, final String taskDescription)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                svc.addItem(loginSupport.getAuthToken(),
                        instantString, taskDescription,
                        loginSupport.withRetry(this, new AsyncCallback<Void>()
                        {
                            @Override
                            public void onSuccess(Void result)
                            {
                                fireAddItemDone(new AsyncResult<Void>(result, null));
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                // TODO: log error here instead of firing.
                                 fireAddItemDone(new AsyncResult<Void>(null, caught));
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void startAddItems(final List<StartTag> items)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                svc.addItems(loginSupport.getAuthToken(),
                        items,
                        loginSupport.withRetry(this, new AsyncCallback<Void>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                // TODO: report here instead of firing.
                                fireAddItemDone(new AsyncResult<Void>(null, caught));
                            }

                            @Override
                            public void onSuccess(Void result)
                            {
                                fireAddItemDone(new AsyncResult<Void>(result, null));
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void startEditDescription(final StartTag editedStartTag)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                svc.update(loginSupport.getAuthToken(),
                        editedStartTag,
                        loginSupport.withRetry(this, new AsyncCallback<Void>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                // TODO: report here instead of firing.
                                fireAddItemDone(new AsyncResult<Void>(null, caught));
                            }

                            @Override
                            public void onSuccess(Void result)
                            {
                                fireAddItemDone(new AsyncResult<Void>(result, null));
                            }
                        }));
            }
        }.runAsync();

    }

    @Override
    public void startRefreshItems(final int maxSize, final String startingInstant, final String endingInstant)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                svc.refreshItems(loginSupport.getAuthToken(),
                        maxSize, SortDir.desc, startingInstant, endingInstant,
                        loginSupport.withRetry(this, new AsyncCallback<List<StartTag>>()
                        {
                            @Override
                            public void onSuccess(List<StartTag> result)
                            {
                                fireRefreshItemsDone(new AsyncResult<List<StartTag>>(result, null));
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                // TODO: handle here instead of firing.
                                fireRefreshItemsDone(new AsyncResult<List<StartTag>>(null, caught));
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void startRefreshTotals(final int maxSize, final SortDir sortDir, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                svc.refreshTotals(loginSupport.getAuthToken(),
                        maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords,
                        loginSupport.withRetry(this, new AsyncCallback<List<TaskTotal>>()
                        {
                            @Override
                            public void onFailure(Throwable caught)
                            {
                                // TODO: handle this here instead of propagating.
                                fireRefreshTotalsDone(new AsyncResult<List<TaskTotal>>(null, caught));
                            }

                            @Override
                            public void onSuccess(List<TaskTotal> result)
                            {
                                fireRefreshTotalsDone(new AsyncResult<List<TaskTotal>>(result, null));
                            }
                        }));
            }
        }.runAsync();
    }

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
                                fireAssignBilleeDone(new AsyncResult<Void>(result, null));

                                startGetAllBillees();
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                GWT.log("failure - assign billee: " + caught.getMessage());
                                // TODO: handle this here instead of propagating
                                fireAssignBilleeDone(new AsyncResult<Void>(null, caught));
//                                new ErrorBox("authentication", caught.getMessage()).show();
//                                throw new RuntimeException("Service error: " + caught.getMessage(), caught);
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
                                fireAllBilleesDone(new AsyncResult<List<String>>(result, null));
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                // TODO: handle this here instead of propagating
                                fireAllBilleesDone(new AsyncResult<List<String>>(null, caught));
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
                assignedSvc.refreshTotals(loginSupport.getAuthToken(),
                        maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords,
                        loginSupport.withRetry(this, new AsyncCallback<List<AssignedTaskTotal>>()
                        {
                            @Override
                            public void onSuccess(List<AssignedTaskTotal> result)
                            {
                                fireRefreshTotalsAssignedDone(new AsyncResult<List<AssignedTaskTotal>>(result, null));
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                // TODO: why do we fire and handle errors everywhere else, why not just handle here?
                                fireRefreshTotalsAssignedDone(new AsyncResult<List<AssignedTaskTotal>>(null, caught));
//                              new ErrorBox("authentication", caught.getMessage()).show();
//                              throw new RuntimeException("Service error: " + caught.getMessage(), caught);
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void startListAvailableJobs()
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                jobSvc.getAvailableJobIds(loginSupport.getAuthToken(),
                        loginSupport.withRetry(this, new AsyncCallback<List<String>>()
                        {
                            @Override
                            public void onSuccess(List<String> result)
                            {
                                fireListAvailableJobsDone(new AsyncResult<List<String>>(result, null));
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                // TODO: handle here instead of propagating
                                fireListAvailableJobsDone(new AsyncResult<List<String>>(null, caught));
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void startPerformJob(final String jobId)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                jobSvc.performJob(loginSupport.getAuthToken(),
                        jobId,
                        loginSupport.withRetry(this, new AsyncCallback<AppJobCompletion>()
                        {
                            @Override
                            public void onSuccess(AppJobCompletion result)
                            {
                                firePerformJobDone(new AsyncResult<AppJobCompletion>(result, null));
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                // TODO: handle here instead of propagating
                                firePerformJobDone(new AsyncResult<AppJobCompletion>(null, caught));
                            }
                        }));
            }
        }.runAsync();
    }
}
