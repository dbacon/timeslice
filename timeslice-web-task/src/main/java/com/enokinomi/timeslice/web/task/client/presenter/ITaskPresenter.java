package com.enokinomi.timeslice.web.task.client.presenter;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.task.client.core.StartTag;

public interface ITaskPresenter
{
    public static interface ITaskPresenterListener
    {
        void onRefreshItemsDone(List<StartTag> result);
        void onAddItemDone();
    }

    Registration addListener(ITaskPresenterListener listener);

    void startAddItem(String instantString, String taskDescription);
    void startRefreshItems(int maxSize, String startingInstant, String endingInstant);
    void startEditDescription(StartTag editedStartTag);
    void startAddItems(List<StartTag> items);
}
