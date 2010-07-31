package com.enokinomi.timeslice.app.periodbilling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BillableTotalList
{
    private final List<BillableTotal> billableTotals = new ArrayList<BillableTotal>();

//    private final Set<String> distinctNames = new LinkedHashSet<String>();

    public List<BillableTotal> getConstBillableTasks()
    {
        return Collections.unmodifiableList(billableTotals);
    }

    public BillableTotalList addBillableTotal(BillableTotal total)
    {
//        if (distinctNames.contains(total.getDescription()))
//        {
//            throw new RuntimeException("Billable totals list already contains a total '" + total.getDescription() + "'.");
//        }

        billableTotals.add(total);

        return this;
    }

    public BillableTotalList addAllBillableTotals(Collection<? extends BillableTotal> collection)
    {
        for (BillableTotal total: collection)
        {
            addBillableTotal(total);
        }

        return this;
    }
}
