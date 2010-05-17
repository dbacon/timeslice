package bacond.timeslice.web.gwt.client.controller;


import java.util.List;

import bacond.timeslice.web.gwt.client.beans.NotAuthenticException;
import bacond.timeslice.web.gwt.client.beans.StartTag;
import bacond.timeslice.web.gwt.client.beans.TaskTotal;
import bacond.timeslice.web.gwt.client.entry.AsyncResult;
import bacond.timeslice.web.gwt.client.server.ITimesliceSvc;
import bacond.timeslice.web.gwt.client.server.ITimesliceSvcAsync;
import bacond.timeslice.web.gwt.client.server.ProcType;
import bacond.timeslice.web.gwt.client.server.SortDir;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GwtRpcController extends BaseController
{
    private ITimesliceSvcAsync svc = GWT.create(ITimesliceSvc.class);
    private String authToken = null;
    private LoginDialog loginDialog = null;

    public ITimesliceSvcAsync getSvc()
    {
        return svc;
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

    private void requestAuthentication(String user, String password)
    {
        GWT.log("Requesting authentication token for '" + user + "'.");
        getSvc().authenticate(user, password, new AsyncCallback<String>()
                {
                    @Override
                    public void onSuccess(String result)
                    {
                        setAuthToken(result);
                        fireAuthenticated();
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


    public void authenticate()
    {
        authenticate("Please login.", null);
    }

    public void authenticate(String subtext)
    {
        authenticate("Please login.", subtext);
    }

    public void authenticate(String title, String subText)
    {
        if (null == loginDialog)
        {
            loginDialog = new LoginDialog(title, subText, new LoginDialog.IListener()
            {
                @Override
                public void submitted(String user, String password)
                {
                    requestAuthentication(user, password);
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
    public void startAddItem(String instantString, String taskDescription)
    {
        String token = getAuthToken();
        if (null == token)
        {
            // not authenticated.
            authenticate();
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
                                authenticate(caught.getMessage());
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
    public void startAddItems(List<StartTag> items)
    {
        String token = getAuthToken();
        if (null == token)
        {
            // not authenticated.
            authenticate();
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
                                authenticate(caught.getMessage());
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
    public void startEditDescription(StartTag editedStartTag)
    {
        String token = getAuthToken();
        if (null == token)
        {
            authenticate();
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
                        authenticate(caught.getMessage());
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
    public void startRefreshItems(int maxSize)
    {
        String token = getAuthToken();
        if (null == token)
        {
            // not authenticated.
            authenticate();
        }
        else
        {
            getSvc().refreshItems(token, maxSize, SortDir.desc, null, null, null, new AsyncCallback<List<StartTag>>()
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
                                authenticate(caught.getMessage());
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
    public void startRefreshTotals(int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        String token = getAuthToken();
        if (null == token)
        {
            // not authenticated.
            authenticate();
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
                                authenticate(caught.getMessage());
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
    public void startPersistTotals(String persistAsName, int maxSize, SortDir sortDir, ProcType procType, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords)
    {
        String authToken = getAuthToken();
        if (null == authToken)
        {
            authenticate();
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
                    if (caught instanceof NotAuthenticException) authenticate(caught.getMessage());
                    else
                    {
                        firePersistTotalsDone(new AsyncResult<String>(null, caught));
                    }
                }
            });
        }
    }
}
