package bacond.lib.util;

import java.util.Collection;
import java.util.Map;

public class MapMaker<K, V, MT extends Map<K, V>, I>
{
    private final MT map;
    ITransform<I, K> keyMaker;
    ITransform<I, V> valueMaker;

    public static <K,V, MT extends Map<K, V>, I> MapMaker<K, V, MT, I> create(MT map, ITransform<I, K> keyMaker, ITransform<I, V> valueMaker)
    {
        return new MapMaker<K, V, MT, I>(map, keyMaker, valueMaker);
    }

    public static <K,V, MT extends Map<K, V>> MapMaker<K, V, MT, Void> create(MT map)
    {
        return new MapMaker<K, V, MT, Void>(map, Transforms.<Void, K>invalid(), Transforms.<Void, V>invalid());
    }

    public MapMaker(MT map, ITransform<I, K> keyMaker, ITransform<I, V> valueMaker)
    {
        this.map = map;
        this.keyMaker = keyMaker;
        this.valueMaker = valueMaker;
    }

    public MapMaker<K, V, MT, I> put(K k, V v)
    {
        getMap().put(k, v);
        return this;
    }

    public MT getMap()
    {
        return map;
    }

    public MapMaker<K, V, MT, I> put(I item)
    {
        put(keyMaker.apply(item), valueMaker.apply(item));
        return this;
    }

    public MapMaker<K, V, MT, I> putAll(Collection<I> items, ITransform<I, K> keyMaker, ITransform<I, V> valueMaker)
    {
        for (I item: items)
        {
            put(item);
        }

        return this;
    }
}
