package com.enokinomi.timeslice.web.task.client.presenter;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.ListenerManager;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport;
import com.enokinomi.timeslice.web.login.client.ui.api.ILoginSupport.IOnAuthenticated;
import com.enokinomi.timeslice.web.task.client.core.ITaskSvcAsync;
import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class TaskPresenter implements ITaskPresenter
{
    private final ILoginSupport loginSupport;
    private final ITaskSvcAsync svc;

    private final ListenerManager<ITaskPresenterListener> listenerMgr = new ListenerManager<ITaskPresenterListener>();
    @Override public Registration addListener(ITaskPresenterListener l) { return listenerMgr.addListener(l); }
    protected void fireAddItemDone() { for (ITaskPresenterListener l: listenerMgr.getListeners()) { l.onAddItemDone(); } }
    protected void fireDeleteDone() { for (ITaskPresenterListener l: listenerMgr.getListeners()) { l.onDeleteDone(); } }
    protected void fireRefreshItemsDone(List<StartTag> result) { for (ITaskPresenterListener l: listenerMgr.getListeners()) { l.onRefreshItemsDone(result); } }
    protected void fireGenericFail(String msg) { for (ITaskPresenterListener l: listenerMgr.getListeners()) { l.genericFail(msg); } }

    @Inject
    public TaskPresenter(ITaskSvcAsync svc, ILoginSupport loginSupport)
    {
        this.svc = svc;
        this.loginSupport = loginSupport;
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
                                fireAddItemDone();
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                fireGenericFail("Service-call add-item failed: " + caught.getMessage());
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
                                fireGenericFail("Service-call add-items failed: " + caught.getMessage());
                            }

                            @Override
                            public void onSuccess(Void result)
                            {
                                fireAddItemDone();
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
                                fireGenericFail("Service-call update-description failed: " + caught.getMessage());
                            }

                            @Override
                            public void onSuccess(Void result)
                            {
                                fireAddItemDone();
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
                                fireRefreshItemsDone(result);
                            }

                            @Override
                            public void onFailure(Throwable caught)
                            {
                                fireGenericFail("Service-call refresh-items failed: " + caught.getMessage());
                            }
                        }));
            }
        }.runAsync();
    }

    @Override
    public void startDeleteTask(final StartTag startTag)
    {
        new IOnAuthenticated()
        {
            @Override
            public void runAsync()
            {
                svc.removeItem(loginSupport.getAuthToken(),
                        startTag,
                        loginSupport.withRetry(this, new AsyncCallback<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void result)
                                    {
                                        fireDeleteDone();
                                    }

                                    @Override
                                    public void onFailure(Throwable caught)
                                    {
                                        fireGenericFail("Service-call delete-item failed: " + caught.getMessage());
                                    }
                                }));
            }
        }.runAsync();
    }

}
