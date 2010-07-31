package com.enokinomi.timeslice.app.rolodex;

import java.util.LinkedHashMap;
import java.util.Map;

import com.enokinomi.timeslice.lib.util.Check;


public class ClientInfo extends LinkedHashMap<String, String>
{
    private static final long serialVersionUID = 1L;

    public static final class Key
    {
        public static final String Name = "name";
    }

    public ClientInfo(String name)
    {
        super();

        Check.disallowNull(name, "name");
        put(Key.Name, name);
    }

    public ClientInfo(int initialCapacity, float loadFactor, boolean accessOrder, String name)
    {
        super(initialCapacity, loadFactor, accessOrder);

        Check.disallowNull(name, "name");
        put(Key.Name, name);
    }

    public ClientInfo(int initialCapacity, float loadFactor, String name)
    {
        super(initialCapacity, loadFactor);

        Check.disallowNull(name, "name");
        put(Key.Name, name);
    }

    public ClientInfo(int initialCapacity, String name)
    {
        super(initialCapacity);

        Check.disallowNull(name, "name");
        put(Key.Name, name);
    }

    public ClientInfo(Map<? extends String, ? extends String> m, String name)
    {
        super(m);

        Check.disallowNull(name, "name");
        put(Key.Name, name);
    }

    public String getName()
    {
        return get(Key.Name);
    }
}
