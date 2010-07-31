package com.enokinomi.timeslice.lib.util;

public class Check
{
    /**
     * @deprecated - use {@link disallowNull} (if you must, see API docs).
     * @param o
     * @param s
     */
    @Deprecated
    public static void notNull(Object o, String s)
    {
        if (null == o)
        {
            throw new RuntimeException("Object must not be null: " + s);
        }
    }

    /**
     * Use only for defensive checks in external-facing API (library code).
     *
     * @param <T>
     * @param t
     * @return
     */
    public static <T> T disallowNull(T t)
    {
        return disallowNull(t, null);
    }

    /**
     * Use only for defensive checks in external-facing API (library code).
     *
     * @param <T>
     * @param t
     * @param name
     * @return
     */
    public static <T> T disallowNull(T t, String name)
    {
        if (null == t)
        {
            throw new RuntimeException("Null not allowed" + ((null == name) ? "" : (" for '" + name + "'")) + ".");
        }

        return t;
    }
}
