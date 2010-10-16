package com.enokinomi.timeslice.lib.ordering;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MemoryOrderingStore<T> implements IOrderingStore<T>
{
    private Map<String, List<T>> store = new LinkedHashMap<String, List<T>>();

    @Override
    public List<T> requestOrdering(String setName, List<T> unorderedSetValues)
    {
        List<T> ordering = store.get(setName);

        if (null == ordering)
        {
            return unorderedSetValues;
        }
        else
        {
            List<T> result = new OrderApplier().<T>applyOrdering(unorderedSetValues, ordering);

            return result;
        }
    }

    @Override
    public void setOrdering(String setName, List<T> orderedSetMembers)
    {
        // delete any existing under setName
        // save items and their index in the list under setName

        List<T> list = store.get(setName);

        if (null == list)
        {
            list = new ArrayList<T>();
            store.put(setName, list);
        }

        list.clear();
        list.addAll(orderedSetMembers);
    }
}
