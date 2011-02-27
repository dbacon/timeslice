package com.enokinomi.timeslice.web.report.client.presenter;

import java.util.List;

import com.enokinomi.timeslice.web.assign.client.core.AssignedTaskTotal;
import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.core.client.util.SortDir;
import com.enokinomi.timeslice.web.report.client.core.TaskTotal;

public interface IReportPresenter
{
    public interface IReportsPresenterListener
    {
        void onRefreshTotalsDone(List<TaskTotal> result);
        void onRefreshTotalsAssignedDone(List<AssignedTaskTotal> result);
        void onAssignBilleeDone();
        void onAllBilleesDone(List<String> result);
    }

    Registration addListener(IReportsPresenterListener listener);

    void startRefreshTotalsAssigned(int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
    void startGetAllBillees();
    void startAssignBillee(String description, String newBillee);
}
