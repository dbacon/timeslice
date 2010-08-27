package com.enokinomi.timeslice.timeslice;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import com.enokinomi.timeslice.app.core.ITimesliceStore;


public class StoreManagerTest
{
    boolean flag = false;

    @Test
    public void test1()
    {
        StoreManager storeManager = new StoreManager(
                new File("test-input/storemanager/dir1"),
                Arrays.<StoreManager.IParser>asList(new StoreManager.MemoryPlugin()));

        ArrayList<ITimesliceStore> stores = storeManager.configure();

        assertEquals(1, stores.size());
    }
}
