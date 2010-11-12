package com.enokinomi.timeslice.web.prorata.client.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Reorderer<K,V>
{
    Reorderer()
    {
    }

    public Map<K, V> reorder(Map<K,V> map, int rowi, int rel)
    {
        int starting = rowi;
        int ending = rowi + rel;

        if (starting < 0) throw new RuntimeException("Attempt to move element of negative index: " + starting + " -> " + ending);
        if (starting >= map.size()) throw new RuntimeException("Attempt to move element of index beyond last index " + (map.size() + 1) + ": " + starting + " -> " + ending);
        if (ending < 0) throw new RuntimeException("Attempt to move element to position of negative index: " + starting + " -> " + ending);
        if (ending >= map.size()) throw new RuntimeException("Attempt to move element to position of index out of range " + (map.size() + 1) + ": " + starting + " -> " + ending);

        LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
        List<K> keys = new ArrayList<K>(map.keySet());

        for (int i = 0; i < keys.size(); ++i)
        {
            K key;

            if (i == ending)
            {
                key = keys.get(starting);
            }
            else
            {
                if (rel < 0 && ending <= i && i <= starting)
                {
                    key = keys.get(i-1);
                }
                else if (rel > 0 && starting <= i && i <= ending)
                {
                    key = keys.get(i+1);
                }
                else
                {
                    key = keys.get(i);
                }
            }

            result.put(key, map.get(key));
        }

        return result;
    }
}
