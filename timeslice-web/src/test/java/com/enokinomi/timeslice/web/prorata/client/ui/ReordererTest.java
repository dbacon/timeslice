package com.enokinomi.timeslice.web.prorata.client.ui;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.enokinomi.timeslice.web.prorata.client.ui.Reorderer;


public class ReordererTest
{
    @Test
    public void test1()
    {
        assertEquals(
                Arrays.asList("zero", "one", "two", "four", "three", "five"),
                new ArrayList<String>(new Reorderer<String, Integer>()
                        .reorder(createInputMap(), 4, -1).keySet()));
    }

    @Test
    public void test2()
    {
        assertEquals(
                Arrays.asList("zero", "one", "four", "two", "three", "five"),
                new ArrayList<String>(new Reorderer<String, Integer>()
                        .reorder(createInputMap(), 4, -2).keySet()));
    }

    @Test
    public void test3()
    {
        assertEquals(
                Arrays.asList("zero", "one", "two", "three", "four", "five"),
                new ArrayList<String>(new Reorderer<String, Integer>()
                        .reorder(createInputMap(), 4, 0).keySet()));
    }

    @Test
    public void test4()
    {
        assertEquals(
                Arrays.asList("zero", "one", "two", "three", "five", "four"),
                new ArrayList<String>(new Reorderer<String, Integer>()
                        .reorder(createInputMap(), 4, 1).keySet()));
    }

    @Test(expected=RuntimeException.class)
    public void test5()
    {
        assertEquals(
                Arrays.asList("zero", "one", "two", "three", "five", "four"),
                new ArrayList<String>(new Reorderer<String, Integer>()
                        .reorder(createInputMap(), -1, 1).keySet()));
    }

    @Test(expected=RuntimeException.class)
    public void test6()
    {
        assertEquals(
                Arrays.asList("zero", "one", "two", "three", "five", "four"),
                new ArrayList<String>(new Reorderer<String, Integer>()
                        .reorder(createInputMap(), 6, 1).keySet()));
    }

    @Test(expected=RuntimeException.class)
    public void test7()
    {
        assertEquals(
                Arrays.asList("zero", "one", "two", "three", "five", "four"),
                new ArrayList<String>(new Reorderer<String, Integer>()
                        .reorder(createInputMap(), 5, 1).keySet()));
    }

    @Test(expected=RuntimeException.class)
    public void test8()
    {
        assertEquals(
                Arrays.asList("zero", "one", "two", "three", "five", "four"),
                new ArrayList<String>(new Reorderer<String, Integer>()
                        .reorder(createInputMap(), 0, -1).keySet()));
    }

    private Map<String, Integer> createInputMap()
    {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put("zero", 0);
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        map.put("four", 4);
        map.put("five", 5);

        return Collections.unmodifiableMap(map);
    }
}
