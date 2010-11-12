package com.enokinomi.timeslice.web.task.client.controller.impl;


import java.util.List;

import com.enokinomi.timeslice.web.appjob.client.core.AppJobCompletion;
import com.enokinomi.timeslice.web.appjob.client.core.IAppJobSvcAsync;
import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.assign.client.core.IAssignmentSvcAsync;
import com.enokinomi.timeslice.web.core.client.ui.ErrorBox;
import com.enokinomi.timeslice.web.core.client.util.AsyncResult;
import com.enokinomi.timeslice.web.core.client.util.NotAuthenticException;
import com.enokinomi.timeslice.web.task.client.controller.api.IAuthTokenHolder;
import com.enokinomi.timeslice.web.task.client.core.ITimesliceSvcAsync;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.core.TaskTotal;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.BrandInfo;
import com.enokinomi.timeslice.web.task.client.core_todo_move_out.SortDir;
import com.enokinomi.timeslice.web.task.client.ui_one.api.PrefHelper;
import com.enokinomi.timeslice.web.task.client.ui_one.impl.TimesliceAppConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class GwtRpcController extends BaseController implements IAuthTokenHolder
{
    private final ITimesliceSvcAsync svc;
    private final IAssignmentSvcAsync assignedSvc;
    private final IAppJobSvcAsync jobSvc;

    private String authToken = Cookies.getCookie("timeslice.authtoken");
    private LoginDialog loginDialog = null;

    private final TimesliceAppConstants constants;

    @Inject
    GwtRpcController(TimesliceAppConstants constants, ITimesliceSvcAsync svc, IAssignmentSvcAsync assignedSvc, IAppJobSvcAsync jobSvc)
    {
        this.constants = constants;
        this.svc = svc;
        this.assignedSvc = assignedSvc;
        this.jobSvc = jobSvc;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void logout()
    {
        svc.logout(authToken, new AsyncCallback<Void>()
        {
            @Override
            public void onSuccess(Void result)
            {
                GWT.log("forgetting auth token");
                authToken = null;
                fireUnauthenticated(false);
            }

            @Override
            public void onFailure(Throwable caught)
            {
                GWT.log("logging out failed ??");
                // eh ? leave it I guess.
            }
        });
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

    private <R> void requestAuthentication(String user, String password, final IOnAuthenticated action)
    {
        GWT.log("Requesting authentication token for '" + user + "'.");
        svc.authenticate(user, password, new AsyncCallback<String>()
                {
                    @Override
                    public void onSuccess(String result)
                    {
                        authToken = result;
                        Cookies.setCookie("timeslice.authtoken", result, PrefHelper.createDateSufficientlyInTheFuture());
                        fireAuthenticated();
                        if (null != action) action.startRetry();
                    }

                    @Override
                    public void onFailure(Throwable caught)
                    {
                        StringBuilder sb = new StringBuilder();
                        for (StackTraceElement f: caught.getStackTrace())
                        {
                            sb.append(f.toString()).append("\n");
                        }
                        GWT.log(sb.toString());

                        new ErrorBox("authentication", caught.getMessage()).show();

                        authToken = null;
                        fireUnauthenticated(false);
                    }
                });
    }


    public void authenticate(IOnAuthenticated retryAction)
    {
        authenticate(constants.pleaseLogin(), null, retryAction);
    }

    public void authenticate(String subtext, IOnAuthenticated retryAction)
    {
        authenticate(constants.pleaseLogin(), subtext, retryAction);
    }

    public static interface IOnAuthenticated
    {
        void startRetry();
    }

    public void authenticate(String title, String subText, final IOnAuthenticated action)
    {
        if (null == loginDialog)
        {
            loginDialog = new LoginDialog(title, subText, new LoginDialog.IListener()
            {
                @Override
                public void submitted(String user, String password)
                {
                    requestAuthentication(user, password, action);
                    loginDialog = null;
                }

                @Override
                public void canceled()
                {
                    // nothing. let them have at it.
                    loginDialog = null;
                }
            });

            loginDialog.center();
        }
        else
        {
        }
    }

    @Override
    public void startAddItem(final String instantString, final String taskDescription)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startAddItem(instantString, taskDescription);
            }
        };

        if (null == authToken)
        {
            // not authenticated.
            authenticate(retryAction);
        }
        else
        {
            svc.addItem(authToken, instantString, taskDescription, new AsyncCallback<Void>()
                    {
                        @Override
                        public void onFailure(Throwable caught)
                        {
                            if (caught instanceof NotAuthenticException)
                            {
                                authenticate(caught.getMessage(), retryAction);
                            }
                            else
                            {
                                fireAddItemDone(new AsyncResult<Void>(null, caught));
                            }
                        }

                        @Override
                        public void onSuccess(Void result)
                        {
                            fireAddItemDone(new AsyncResult<Void>(result, null));
                        }
                    });
        }
    }

    @Override
    public void startAddItems(final List<StartTag> items)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startAddItems(items);
            }
        };

        if (null == authToken)
        {
            // not authenticated.
            authenticate(retryAction);
        }
        else
        {
            svc.addItems(authToken, items, new AsyncCallback<Void>()
                    {
                        @Override
                        public void onFailure(Throwable caught)
                        {
                            if (caught instanceof NotAuthenticException)
                            {
                                authenticate(caught.getMessage(), retryAction);
                            }
                            else
                            {
                                fireAddItemDone(new AsyncResult<Void>(null, caught));
                            }
                        }

                        @Override
                        public void onSuccess(Void result)
                        {
                            fireAddItemDone(new AsyncResult<Void>(result, null));
                        }
                    });
        }
    }

    @Override
    public void startEditDescription(final StartTag editedStartTag)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startEditDescription(editedStartTag);
            }
        };

        if (null == authToken)
        {
            authenticate(retryAction);
        }
        else
        {
            svc.update(authToken, editedStartTag, new AsyncCallback<Void>()
            {
                @Override
                public void onFailure(Throwable caught)
                {
                    if (caught instanceof NotAuthenticException)
                    {
                        authenticate(caught.getMessage(), retryAction);
                    }
                    else
                    {
                        fireAddItemDone(new AsyncResult<Void>(null, caught));
                    }
                }

                @Override
                public void onSuccess(Void result)
                {
                    fireAddItemDone(new AsyncResult<Void>(result, null));
                }
            });
        }
    }

    @Override
    public void startRefreshItems(final int maxSize, final String startingInstant, final String endingInstant)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startRefreshItems(maxSize, startingInstant, endingInstant);
            }
        };

        if (null == authToken)
        {
            // not authenticated.
            authenticate(retryAction);
        }
        else
        {
            svc.refreshItems(authToken, maxSize, SortDir.desc, startingInstant, endingInstant, new AsyncCallback<List<StartTag>>()
                    {
                        @Override
                        public void onSuccess(List<StartTag> result)
                        {
                            fireRefreshItemsDone(new AsyncResult<List<StartTag>>(result, null));
                        }

                        @Override
                        public void onFailure(Throwable caught)
                        {
                            if (caught instanceof NotAuthenticException)
                            {
                                authenticate(caught.getMessage(), retryAction);
                            }
                            else
                            {
                                fireRefreshItemsDone(new AsyncResult<List<StartTag>>(null, caught));
                            }
                        }
                    });
        }
    }

    @Override
    public void startRefreshTotals(final int maxSize, final SortDir sortDir, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startRefreshTotals(maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords);
            }
        };

        if (null == authToken)
        {
            // not authenticated.
            authenticate(retryAction);
        }
        else
        {
            svc.refreshTotals(authToken, maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords, new AsyncCallback<List<TaskTotal>>()
                    {
                        @Override
                        public void onFailure(Throwable caught)
                        {
                            if (caught instanceof NotAuthenticException)
                            {
                                authenticate(caught.getMessage(), retryAction);
                            }
                            else
                            {
                                fireRefreshTotalsDone(new AsyncResult<List<TaskTotal>>(null, caught));
                            }
                        }

                        @Override
                        public void onSuccess(List<TaskTotal> result)
                        {
                            fireRefreshTotalsDone(new AsyncResult<List<TaskTotal>>(result, null));
                        }
                    });
        }
    }

    @Override
    public void startAssignBillee(final String description, final String newBillee)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startAssignBillee(description, newBillee);
            }
        };

        if (null == authToken)
        {
            authenticate(retryAction);
        }
        else
        {
            assignedSvc.assign(authToken, description, newBillee, new AsyncCallback<Void>()
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
                    if (caught instanceof NotAuthenticException)
                    {
                        authenticate(caught.getMessage(), retryAction);
                    }
                    else
                    {
                        fireAssignBilleeDone(new AsyncResult<Void>(null, caught));
//                        new ErrorBox("authentication", caught.getMessage()).show();
//                        throw new RuntimeException("Service error: " + caught.getMessage(), caught);
                    }
                }
            });
        }
    }

    @Override
    public void startGetAllBillees()
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startGetAllBillees();
            }
        };

        if (null == authToken)
        {
            authenticate(retryAction);
        }
        else
        {
            assignedSvc.getAllBillees(authToken, new AsyncCallback<List<String>>()
            {
                @Override
                public void onSuccess(List<String> result)
                {
                    fireAllBilleesDone(new AsyncResult<List<String>>(result, null));
                }

                @Override
                public void onFailure(Throwable caught)
                {
                    if (caught instanceof NotAuthenticException)
                    {
                        authenticate(caught.getMessage(), retryAction);
                    }
                    else
                    {
                        fireAllBilleesDone(new AsyncResult<List<String>>(null, caught));
                    }
                }
            });
        }
    }

    @Override
    public void startRefreshTotalsAssigned(final int maxSize, final SortDir sortDir, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startRefreshTotalsAssigned(maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords);
            }
        };

        if (null == authToken)
        {
            authenticate(retryAction);
        }
        else
        {
            assignedSvc.refreshTotals(authToken, maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords, new AsyncCallback<List<AssignedTaskTotal>>()
            {
                @Override
                public void onSuccess(List<AssignedTaskTotal> result)
                {
                    fireRefreshTotalsAssignedDone(new AsyncResult<List<AssignedTaskTotal>>(result, null));
                }

                @Override
                public void onFailure(Throwable caught)
                {
                    GWT.log("got back error for assigned totals: " + caught.getMessage());

                    if (caught instanceof NotAuthenticException)
                    {
                        authenticate(caught.getMessage(), retryAction);
                    }
                    else
                    {
                        fireRefreshTotalsAssignedDone(new AsyncResult<List<AssignedTaskTotal>>(null, caught));
//                        new ErrorBox("authentication", caught.getMessage()).show();
//                        throw new RuntimeException("Service error: " + caught.getMessage(), caught);
                    }
                }
            });
        }
    }

    @Override
    public void startPersistTotals(final String persistAsName, final int maxSize, final SortDir sortDir, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startPersistTotals(persistAsName, maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords);
            }
        };

        if (null == authToken)
        {
            authenticate(retryAction);
        }
        else
        {
            svc.persistTotals(authToken, persistAsName, maxSize, sortDir, startingInstant, endingInstant, allowWords, ignoreWords, new AsyncCallback<String>()
            {
                @Override
                public void onSuccess(String result)
                {
                    firePersistTotalsDone(new AsyncResult<String>(result, null));
                }

                @Override
                public void onFailure(Throwable caught)
                {
                    if (caught instanceof NotAuthenticException)
                    {
                        authenticate(caught.getMessage(), retryAction);
                    }
                    else
                    {
                        firePersistTotalsDone(new AsyncResult<String>(null, caught));
                    }
                }
            });
        }
    }

    @Override
    public void startListAvailableJobs()
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startListAvailableJobs();
            }
        };

        if (null == authToken)
        {
            authenticate(retryAction);
        }
        else
        {
            jobSvc.getAvailableJobIds(authToken, new AsyncCallback<List<String>>()
            {
                @Override
                public void onSuccess(List<String> result)
                {
                    fireListAvailableJobsDone(new AsyncResult<List<String>>(result, null));
                }

                @Override
                public void onFailure(Throwable caught)
                {
                    if (caught instanceof NotAuthenticException)
                    {
                        authenticate(caught.getMessage(), retryAction);
                    }
                    else
                    {
                        fireListAvailableJobsDone(new AsyncResult<List<String>>(null, caught));
                    }
                }
            });
        }
    }

    @Override
    public void startPerformJob(final String jobId)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startPerformJob(jobId);
            }
        };

        if (null == authToken)
        {
            authenticate(retryAction);
        }
        else
        {
            jobSvc.performJob(authToken, jobId, new AsyncCallback<AppJobCompletion>()
            {
                @Override
                public void onSuccess(AppJobCompletion result)
                {
                    firePerformJobDone(new AsyncResult<AppJobCompletion>(result, null));
                }

                @Override
                public void onFailure(Throwable caught)
                {
                    if (caught instanceof NotAuthenticException)
                    {
                        authenticate(caught.getMessage(), retryAction);
                    }
                    else
                    {
                        firePerformJobDone(new AsyncResult<AppJobCompletion>(null, caught));
                    }
                }
            });
        }
    }
}
