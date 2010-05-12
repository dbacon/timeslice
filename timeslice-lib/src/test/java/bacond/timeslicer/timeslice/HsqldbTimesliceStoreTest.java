package bacond.timeslicer.timeslice;

import java.io.File;

import org.joda.time.Instant;
import org.junit.Test;


public class HsqldbTimesliceStoreTest
{
    @Test
    public void test1()
    {
        HsqldbTimesliceStore store = new HsqldbTimesliceStore(new File("."), "abc", "first-task", new Instant(), new Instant());
        store.enable();
        store.disable();
    }
}
