package com.enokinomi.timeslice.lib.appjob_stockjobs;

import com.enokinomi.timeslice.lib.appjob.AppJob;
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
//
//        appJobSetBinder.addBinding().to(DowngradeSchema1To0AppJob.class);
//        appJobSetBinder.addBinding().to(Version3To2DowngradeSchemaAppJob.class);
//        appJobSetBinder.addBinding().to(Version4To3DowngradeSchemaAppJob.class);

        appJobSetBinder.addBinding().to(ListTablesAppJob.class);
        appJobSetBinder.addBinding().to(DetectSchemaVersionAppJob.class);
        appJobSetBinder.addBinding().to(UpgradeSchema0To1AppJob.class);
        appJobSetBinder.addBinding().to(Version1To2UpgradeSchemaAppJob.class);
        appJobSetBinder.addBinding().to(Version2To3UpgradeSchemaAppJob.class);
        appJobSetBinder.addBinding().to(Version3To4UpgradeSchemaAppJob.class);
    }

}
