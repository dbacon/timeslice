package com.enokinomi.timeslice.timeslice;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.enokinomi.timeslice.app.core.ITimesliceStore;
import com.enokinomi.timeslice.timeslice.StoreManager;
import com.enokinomi.timeslice.timeslice.TimesliceApp;


public class StoreManagerTest
{
    boolean flag = false;

    @Test
    public void test1()
    {
        TimesliceApp tsApp = new TimesliceApp(null, null, null, null, null)
        {
            @Override
            public void pushFront(ITimesliceStore store)
            {
                super.pushFront(store);

                System.out.println("added store: " + store.getFirstTagText());
                flag = true;
            }
        };

        StoreManager storeManager = new StoreManager(
                new File("test-input/storemanager/dir1"),
                Arrays.<StoreManager.IParser>asList(new StoreManager.MemoryPlugin()));

        storeManager.configure(tsApp);

        tsApp.disableAllStores();

        Assert.assertTrue("store added correctly", flag);
    }
}
