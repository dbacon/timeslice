package com.enokinomi.timeslice.web.prorata.client.tree;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class TotalingVisitor<V extends IVisitor<V>> implements IVisitor<V>
{
    public final Map<String, Double> totals = new LinkedHashMap<String, Double>();

    public Map<String, Double> getTotals()
    {
        return new LinkedHashMap<String, Double>(totals);
    }

    public void reset()
    {
        totals.clear();
    }
}
