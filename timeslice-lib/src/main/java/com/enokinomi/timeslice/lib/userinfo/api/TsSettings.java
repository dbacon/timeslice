package com.enokinomi.timeslice.lib.userinfo.api;

import static com.enokinomi.timeslice.lib.util.Transforms.tr;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enokinomi.timeslice.lib.util.ITransform;
import com.enokinomi.timeslice.lib.util.Transforms;

public class TsSettings
{
    private Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();

    public TsSettings()
    {
    }

    public Set<String> getKeys()
    {
        return map.keySet();
    }

    public List<String> getRawValuesForKey(String key)
    {
        return map.get(key);
    }

    public Map<String, List<String>> getMap()
    {
        return map;
    }

    public void setConfScalar(String name, Integer value)
    {
        setConfScalar(name, value, Integer.class);
    }

    /**
     * Uses stringify for clazz to encode the value.
     *
     * @param <T>
     * @param name
     * @param value
     * @param clazz
     */
    public <T> void setConfScalar(String name, T value, Class<T> clazz)
    {
        setConfScalar(name, stringify(clazz).apply(value));
    }

    public <T> void setConfScalar(String name, T value, ITransform<T, String> stringifier)
    {
        setConfScalar(name, stringifier.apply(value));
    }

    public void setConfScalar(String name, String value)
    {
        List<String> list = map.get(name);
        if (null == list)
        {
            list = new ArrayList<String>();
            map.put(name, list);
        }
        list.clear();
        list.add(value);
    }

    public void addConfValue(String name, String type, String value)
    {
        List<String> list = map.get(name);
        if (null == list)
        {
            list = new ArrayList<String>();
            map.put(name, list);
        }
        list.add(value);
    }

    public <T> void setConfVector(String name, List<T> values, ITransform<T, String> tx)
    {
        setConfVector(name, Transforms.tr(values, new ArrayList<String>(values.size()), tx));
    }

    public void setConfVector(String name, List<String> values)
    {
        List<String> list = map.get(name);
        if (null == list)
        {
            list = new ArrayList<String>();
            map.put(name, list);
        }
        list.clear();
        list.addAll(values);
    }

    public static final <T> ITransform<T, String> stringify(Class<T> clazz)
    {
        return new ITransform<T, String>()
        {
            @Override
            public String apply(Object r)
            {
                return r.toString();
            }
        };
    }

    public static final ITransform<String, Integer> ParseInt = new ITransform<String, Integer>()
    {
        @Override
        public Integer apply(String r)
        {
            return Integer.valueOf(r);
        }
    };

    public static final ITransform<String, Long> ParseLong = new ITransform<String, Long>()
    {
        @Override
        public Long apply(String r)
        {
            return Long.valueOf(r);
        }
    };

    public List<Long> getListOfLongs(String name, List<Long> valueIfMissing)
    {
        return getList(name, ParseLong, valueIfMissing);
    }

    public List<Integer> getListOfIntegers(String name, List<Integer> valueIfMissing)
    {
        return getList(name, ParseInt, valueIfMissing);
    }

    public List<String> getListOfStrings(String name, List<String> valueIfMissing)
    {
        return getList(name, Transforms.<String>identity(), valueIfMissing);
    }

    public <T> List<T> getList(String name, ITransform<String, T> tx, List<T> valueIfMissing)
    {
        if (!map.containsKey(name)) return valueIfMissing;

        List<String> list = map.get(name);
        if (null == list) throw new RuntimeException("Found configuration, but no values at all found.");

        return tr(list, new ArrayList<T>(list.size()), tx);
    }

    public Integer getScalar(String name, Integer valueIfMissing)
    {
        return getScalar(name, ParseInt, valueIfMissing);
    }

    public Long getScalar(String name, Long valueIfMissing)
    {
        return getScalar(name, ParseLong, valueIfMissing);
    }

    public String getScalar(String name, String valueIfMissing)
    {
        return getScalar(name, Transforms.<String>identity(), valueIfMissing);
    }

    public <T> T getScalar(String name, ITransform<String, T> tx, T valueIfMissing)
    {
        if (!map.containsKey(name)) return valueIfMissing;

        List<String> list = map.get(name);

        if (null == list) throw new RuntimeException("Found configuration expecting scalar value, but no values at all found.");
        if (list.size() == 0) throw new RuntimeException("Found configuration expecting scalar value, but found an empty list.");
        if (list.size() > 1) throw new RuntimeException("Found configuration expecting scalar value, but found a multi-valued list: " + list.toString());

        String value = list.get(0);

        return tx.apply(value);
    }

    public int getTzOffsetMinutes()
    {
        return getScalar("usersession.tzoffsetmin", ParseInt, 0);
    }

}
