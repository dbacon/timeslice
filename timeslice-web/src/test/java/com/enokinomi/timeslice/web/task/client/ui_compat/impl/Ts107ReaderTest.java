package com.enokinomi.timeslice.web.task.client.ui_compat.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.enokinomi.timeslice.web.task.client.core.StartTag;
import com.enokinomi.timeslice.web.task.client.ui_compat.api.ITs107Reader;


public class Ts107ReaderTest
{

    @Test
    public void test1()
    {
        ITs107Reader r = new Ts107Reader();
        List<StartTag> parsed = r.parseItems("[monkey#2010-09-11T12:03:03#monkey]");

        assertNotNull(parsed);
        assertEquals(1, parsed.size());
    }
}
