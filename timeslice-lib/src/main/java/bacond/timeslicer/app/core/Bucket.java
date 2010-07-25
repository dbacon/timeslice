package bacond.timeslicer.app.core;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import bacond.lib.util.ITransform;
import bacond.lib.util.Transforms;

public class Bucket<T, K, V>
{
    private final ITransform<T, K> keyMaker;
    private final ITransform<T, V> valueMaker;

    private final Map<K, List<V>> buckets = new LinkedHashMap<K, List<V>>();

    public static <T1, K1> Bucket<T1, K1, T1> create(ITransform<T1, K1> keyMaker)
    {
        return create(keyMaker, Transforms.<T1>identity());
    }

    public static <T1, K1, V1> Bucket<T1, K1, V1> create(ITransform<T1, K1> keyMaker, ITransform<T1, V1> valueMaker)
    {
        return new Bucket<T1, K1, V1>(keyMaker, valueMaker);
    }

    public Bucket(ITransform<T, K> keyMaker, ITransform<T, V> valueMaker)
    {
        this.keyMaker = keyMaker;
        this.valueMaker = valueMaker;
    }

    public ITransform<T, K> getKeyMaker()
    {
        return keyMaker;
    }

    public ITransform<T, V> getValueMaker()
    {
        return valueMaker;
    }

    public Map<K, List<V>> getBuckets()
    {
        return buckets;
    }

    public Bucket<T, K, V> bucket(Collection<? extends T> items)
    {
        for (T item: items)
        {
            K key = getKeyMaker().apply(item);
            List<V> bucket = getBuckets().get(key);

            if (null == bucket)
            {
                bucket = new LinkedList<V>();
                getBuckets().put(key, bucket);
            }

            bucket.add(getValueMaker().apply(item));
        }

        return this;
    }
}
