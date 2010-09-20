package com.enokinomi.timeslice.appjob.stockjobs;

import com.enokinomi.timeslice.web.gwt.server.appjob.AppJob;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class StockJobsModule extends AbstractModule
{

    @Override
    protected void configure()
    {
        Multibinder<AppJob> appJobSetBinder = Multibinder.newSetBinder(binder(), AppJob.class);

//        appJobSetBinder.addBinding().toInstance(new TestJob1("test-job-1-succeeds"));
//        appJobSetBinder.addBinding().toInstance(new TestJob1("test-job-2-succeeds"));
//        appJobSetBinder.addBinding().toInstance(new FailingTestJob("test-job-2-fails"));
//        appJobSetBinder.addBinding().to(SchemaOpAppJob.class);
//        appJobSetBinder.addBinding().to(DowngradeSchema1To0AppJob.class);

        appJobSetBinder.addBinding().to(ListTablesAppJob.class);
        appJobSetBinder.addBinding().to(DetectSchemaVersionAppJob.class);
        appJobSetBinder.addBinding().to(UpgradeSchema0To1AppJob.class);
    }

}
