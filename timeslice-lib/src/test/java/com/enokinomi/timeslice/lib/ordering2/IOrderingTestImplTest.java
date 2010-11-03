package com.enokinomi.timeslice.lib.ordering2;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;


public class IOrderingTestImplTest
{
    private IOrderingStore createImplUnderTest(List<String> existingData)
    {
        return new MemoryOrderingStore(existingData);
    }

    @Test
    public void testPermGen()
    {
        Set<List<String>> perms = generatePermutations(Collections.<String>emptyList(), new LinkedHashSet<String>(Arrays.asList("grape", "balloon", "car")));

        LinkedHashSet<List<String>> expected = new LinkedHashSet<List<String>>();
        expected.add(Arrays.asList("balloon", "grape", "car"));
        expected.add(Arrays.asList("grape", "balloon", "car"));
        expected.add(Arrays.asList("car", "grape", "balloon"));
        expected.add(Arrays.asList("grape", "car", "balloon"));
        expected.add(Arrays.asList("car", "balloon", "grape"));
        expected.add(Arrays.asList("balloon", "car", "grape"));

        assertEquals(expected, perms);
    }

    private Set<List<String>> generatePermutations(String ... elements)
    {
        return generatePermutations(Collections.<String>emptyList(), new LinkedHashSet<String>(Arrays.asList(elements)));
    }

    private Set<List<String>> generatePermutations(List<String> prefix, Set<String> leftovers)
    {
        Set<List<String>> result = new LinkedHashSet<List<String>>();

        for (String nextElem: leftovers)
        {
            List<String> nextListPrefix = new ArrayList<String>(prefix.size() + 1);
            nextListPrefix.addAll(prefix);
            nextListPrefix.add(nextElem);

            if (1 == leftovers.size())
            {
                result.add(nextListPrefix);
                System.out.println("adding: " + nextListPrefix);
            }
            else
            {
                Set<String> nextLeftovers = new LinkedHashSet<String>(leftovers);
                nextLeftovers.remove(nextElem);

                result.addAll(generatePermutations(nextListPrefix, nextLeftovers));
            }
        }

        return result;
    }

    @Test
    public void hello()
    {
        IOrderingStore impl = createImplUnderTest(Arrays.<String>asList());

        String smaller = "grape";
        List<String> larger = Arrays.asList("orange", "apple");

        impl.addPartialOrdering("order1", smaller, larger);

        for(List<String> perm: generatePermutations("grape", "apple", "orange", "banana"))
        {
            List<String> orderedResult = impl.requestOrdering("order1", perm);

            assertEquals(Arrays.asList("grape", "orange", "apple", "banana"), orderedResult);
        }
    }

}
