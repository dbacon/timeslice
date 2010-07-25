package bacond.timeslicer.app.periodbilling;


import java.util.Collection;

import bacond.timeslicer.app.core.TaskTotal;

/**
 * Creates a {@link BillableTotalList} out
 * of a list of {@link TaskTotal}s.
 *
 * @author dbacon
 *
 */
public class ConvertToBillable
{
    public BillableTotalList convert(Collection<TaskTotal> taskTotals)
    {
        BillableTotalList billableTaskList = new BillableTotalList();

        for (TaskTotal taskTotal: taskTotals)
        {
            billableTaskList.addBillableTotal(new BillableTotal(taskTotal.getWhat(), taskTotal.getMillis()));
        }

        return billableTaskList;
    }
}
