package com.enokinomi.timeslice.lib.ordering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderApplier
{
    public <T> List<T> applyOrdering(List<T> unorderedSetValues, List<T> ordering)
    {
        final Map<T, Integer> map = new LinkedHashMap<T, Integer>();

        for (Integer i = 0; i < ordering.size(); ++i)
        {
            map.put(ordering.get(i), i);
        }

        List<T> result = new ArrayList<T>();
        result.addAll(unorderedSetValues);
        Collections.sort(result, new Comparator<T>()
        {
            @Override
            public int compare(T o1, T o2)
            {
                Integer o1Index = map.get(o1);
                Integer o2Index = map.get(o2);

                o1Index = null == o1Index ? Integer.MAX_VALUE : o1Index;
                o2Index = null == o2Index ? Integer.MAX_VALUE : o2Index;

                return o1Index.compareTo(o2Index);
            }
        });

        return result;
    }
}
