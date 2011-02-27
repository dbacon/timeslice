package com.enokinomi.timeslice.web.prorata.client.tree;

import com.enokinomi.timeslice.web.core.client.util.Checks;

public class TotalTotalingVisitor extends TotalingVisitor<TotalTotalingVisitor>
{
    @Override
    public TotalTotalingVisitor visit(Tree t, Branch parent, int currentDepth, int[] siblingCounts, int[] siblingIndexes)
    {
        totals.put(t.getName(), Checks.mapNullTo(totals.get(t.getName()), 0.) + t.getValue());
        return this;
    }
}
