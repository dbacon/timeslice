package com.enokinomi.timeslice.timeslice;

import java.io.File;

import org.joda.time.Instant;
import org.junit.Test;

import com.enokinomi.timeslice.timeslice.HsqldbTimesliceStore;


public class HsqldbTimesliceStoreTest
{
    @Test
    public void test1()
    {
        HsqldbTimesliceStore store = new HsqldbTimesliceStore(new File("."), "target/test-generated-data/abc", "first-task", new Instant(), new Instant());
        store.enable();
        store.disable();
    }
}
