package com.enokinomi.timeslice.web.gwt.client.prorata.tree;

import com.enokinomi.timeslice.web.gwt.client.util.Checks;

public class TotalTotalingVisitor extends TotalingVisitor<TotalTotalingVisitor>
{
    @Override
    public TotalTotalingVisitor visit(Tree t, Branch parent, int currentDepth, int[] siblingCounts, int[] siblingIndexes)
    {
        totals.put(t.getName(), Checks.mapNullTo(totals.get(t.getName()), 0.) + t.getValue());
        return this;
    }
}
