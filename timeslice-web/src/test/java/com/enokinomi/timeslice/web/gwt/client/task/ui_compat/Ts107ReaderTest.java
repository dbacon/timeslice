package com.enokinomi.timeslice.web.gwt.client.task.ui_compat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.ui_compat.Ts107Reader;


public class Ts107ReaderTest
{

    @Test
    public void test1()
    {
        Ts107Reader r = new Ts107Reader("[monkey#2010-09-11T12:03:03#monkey]");
        List<StartTag> parsed = r.parseItems();

        assertNotNull(parsed);
        assertEquals(1, parsed.size());
    }
}
