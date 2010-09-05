package com.enokinomi.timeslice.web.gwt.client.controller;


import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.beans.AssignedTaskTotal;
import com.enokinomi.timeslice.web.gwt.client.beans.NotAuthenticException;
import com.enokinomi.timeslice.web.gwt.client.beans.StartTag;
import com.enokinomi.timeslice.web.gwt.client.beans.TaskTotal;
import com.enokinomi.timeslice.web.gwt.client.entry.AsyncResult;
import com.enokinomi.timeslice.web.gwt.client.server.IAssignmentSvc;
import com.enokinomi.timeslice.web.gwt.client.server.IAssignmentSvcAsync;
import com.enokinomi.timeslice.web.gwt.client.server.ITimesliceSvc;
import com.enokinomi.timeslice.web.gwt.client.server.ITimesliceSvcAsync;
import com.enokinomi.timeslice.web.gwt.client.server.ProcType;
import com.enokinomi.timeslice.web.gwt.client.server.SortDir;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GwtRpcController extends BaseController
{
    private final ITimesliceSvcAsync svc = GWT.create(ITimesliceSvc.class);
    private final IAssignmentSvcAsync assignedSvc = GWT.create(IAssignmentSvc.class);
    private String authToken = null;
    private LoginDialog loginDialog = null;

    public ITimesliceSvcAsync getSvc()
    {
        return svc;
    }

    public IAssignmentSvcAsync getAssignedSvc()
    {
        return assignedSvc;
    }

    public String getAuthToken()
    {
        return authToken;
    }

    public void setAuthToken(String authToken)
    {
        this.authToken = authToken;
    }

    public void logout()
    {
        getSvc().logout(authToken, new AsyncCallback<Void>()
        {
            @Override
            public void onSuccess(Void result)
            {
                GWT.log("forgetting auth token");
                setAuthToken(null);
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
        getSvc().serverInfo(new AsyncCallback<String>()
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

    private <R> void requestAuthentication(String user, String password, final IOnAuthenticated action)
    {
        GWT.log("Requesting authentication token for '" + user + "'.");
        getSvc().authenticate(user, password, new AsyncCallback<String>()
                {
                    @Override
                    public void onSuccess(String result)
                    {
                        setAuthToken(result);
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

                        setAuthToken(null);
                        fireUnauthenticated(false);
                    }
                });
    }


    public void authenticate(IOnAuthenticated retryAction)
    {
        authenticate("Please login.", null, retryAction);
    }

    public void authenticate(String subtext, IOnAuthenticated retryAction)
    {
        authenticate("Please login.", subtext, retryAction);
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

        String token = getAuthToken();
        if (null == token)
        {
            // not authenticated.
            authenticate(retryAction);
        }
        else
        {
            getSvc().addItem(token, instantString, taskDescription, new AsyncCallback<Void>()
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

        String token = getAuthToken();
        if (null == token)
        {
            // not authenticated.
            authenticate(retryAction);
        }
        else
        {
            getSvc().addItems(token, items, new AsyncCallback<Void>()
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

        String token = getAuthToken();
        if (null == token)
        {
            authenticate(retryAction);
        }
        else
        {
            getSvc().update(token, editedStartTag, new AsyncCallback<Void>()
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

        String token = getAuthToken();
        if (null == token)
        {
            // not authenticated.
            authenticate(retryAction);
        }
        else
        {
            getSvc().refreshItems(token, maxSize, SortDir.desc, null, startingInstant, endingInstant, new AsyncCallback<List<StartTag>>()
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
    public void startRefreshTotals(final int maxSize, final SortDir sortDir, final ProcType procType, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startRefreshTotals(maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords);
            }
        };

        String token = getAuthToken();
        if (null == token)
        {
            // not authenticated.
            authenticate(retryAction);
        }
        else
        {
            getSvc().refreshTotals(token, maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords, new AsyncCallback<List<TaskTotal>>()
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

        String token = getAuthToken();
        if (null == token)
        {
            authenticate(retryAction);
        }
        else
        {
            getAssignedSvc().assign(token, description, newBillee, new AsyncCallback<Void>()
            {
                @Override
                public void onSuccess(Void result)
                {
                    GWT.log("success - assign billee");
                    fireAssignBilleeDone(new AsyncResult<Void>(result, null));
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
                        throw new RuntimeException("Service error: " + caught.getMessage(), caught);
                    }
                }
            });
        }
    }

    @Override
    public void startRefreshTotalsAssigned(final int maxSize, final SortDir sortDir, final ProcType procType, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startRefreshTotalsAssigned(maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords);
            }
        };

        String token = getAuthToken();
        if (null == token)
        {
            authenticate(retryAction);
        }
        else
        {
            getAssignedSvc().refreshTotals(token, maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords, new AsyncCallback<List<AssignedTaskTotal>>()
            {
                @Override
                public void onSuccess(List<AssignedTaskTotal> result)
                {
                    GWT.log("got back success for assigned totals");
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
                        throw new RuntimeException("Service error: " + caught.getMessage(), caught);
                    }
                }
            });
        }
    }

    @Override
    public void startPersistTotals(final String persistAsName, final int maxSize, final SortDir sortDir, final ProcType procType, final String startingInstant, final String endingInstant, final List<String> allowWords, final List<String> ignoreWords)
    {
        final IOnAuthenticated retryAction = new IOnAuthenticated()
        {
            @Override
            public void startRetry()
            {
                startPersistTotals(persistAsName, maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords);
            }
        };

        String authToken = getAuthToken();
        if (null == authToken)
        {
            authenticate(retryAction);
        }
        else
        {
            getSvc().persistTotals(authToken, persistAsName, maxSize, sortDir, procType, startingInstant, endingInstant, allowWords, ignoreWords, new AsyncCallback<String>()
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
}
