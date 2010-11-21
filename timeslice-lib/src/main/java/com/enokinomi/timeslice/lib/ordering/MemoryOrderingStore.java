package com.enokinomi.timeslice.lib.ordering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MemoryOrderingStore implements IOrderingStore
{
    List<String> data = new ArrayList<String>();

    public MemoryOrderingStore()
    {
        this(Collections.<String>emptyList());
    }

    public MemoryOrderingStore(List<String> existingData)
    {
        data.addAll(existingData);
    }

    @Override
    public void addPartialOrdering(String setName, String smaller, List<String> larger)
    {
        // we are removing anything we are adding.
        data.removeAll(larger);

        int indexOf = -1;

        if (null != smaller)
        {
            if (larger.contains(smaller)) throw new IllegalArgumentException("larger set cannot contain smaller element");

            // missing destination anchor is implicitly added
            if (!data.contains(smaller)) data.add(smaller);

            // add new members after anchor
            indexOf = data.indexOf(smaller);
        }

        data.addAll(indexOf + 1, larger);

        dump("after adding partial");
    }

    private void dump(String msg)
    {
        System.out.println("Data (" + msg + "):");
        for (String element: data)
        {

            System.out.println("  " + element);
        }
        System.out.println("End.");
    }

    @Override
    public List<String> requestOrdering(String setName, List<String> unorderedElements)
    {
        return new OrderApplier().applyOrdering(unorderedElements, data);
    }

}
