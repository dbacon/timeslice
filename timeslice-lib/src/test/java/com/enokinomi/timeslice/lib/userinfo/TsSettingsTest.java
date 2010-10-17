package com.enokinomi.timeslice.lib.userinfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;


public class TsSettingsTest
{
    @Test
    public void test_valueIfNotFound_works()
    {
        TsSettings settings = new TsSettings();
        ArrayList<Integer> givenValueIfMissing = new ArrayList<Integer>();

        assertEquals(Integer.valueOf(-42), settings.getScalar("nothing", TsSettings.ParseInt, -42));
        assertEquals(Integer.valueOf(-32), settings.getScalar("nothing", TsSettings.ParseInt, -32));
        assertSame(givenValueIfMissing, settings.getList("nothing", TsSettings.ParseInt, givenValueIfMissing));
    }

    @Test
    public void test_setScalar()
    {
        TsSettings settings = new TsSettings();

        settings.setConfScalar("name1", 752875);
        assertEquals(Integer.valueOf(752875), settings.getScalar("name1", -24));
    }

    @Test
    public void test_setVector_string()
    {
        TsSettings settings = new TsSettings();

        settings.setConfVector("name2", Arrays.asList("abc", "def"));

        assertEquals(Arrays.asList("abc", "def"), settings.getList("name2", TsSettings.stringify(String.class), Arrays.asList("nothing")));
    }

    @Test
    public void test_setVector_tx()
    {
        TsSettings settings = new TsSettings();

        settings.setConfVector("name2", Arrays.asList(45L, 85L), TsSettings.stringify(Long.class));

        assertEquals(Arrays.asList(45L, 85L), settings.getList("name2", TsSettings.ParseLong, Arrays.asList(8275L)));
    }

}
