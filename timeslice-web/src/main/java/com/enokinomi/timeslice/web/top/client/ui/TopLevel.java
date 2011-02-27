package com.enokinomi.timeslice.web.top.client.ui;


import com.enokinomi.timeslice.web.appjob.client.ui.impl.AppJobActivityFactory;
import com.enokinomi.timeslice.web.appjob.client.ui.impl.AppJobPlace;
import com.enokinomi.timeslice.web.core.client.ui.SimpleLayoutPanel;
import com.enokinomi.timeslice.web.report.client.presenter.ReportActivityFactory;
import com.enokinomi.timeslice.web.report.client.presenter.ReportPlace;
import com.enokinomi.timeslice.web.settings.client.presenter.impl.OptionsActivityFactory;
import com.enokinomi.timeslice.web.settings.client.presenter.impl.OptionsPlace;
import com.enokinomi.timeslice.web.task.client.presenter.InputActivityFactory;
import com.enokinomi.timeslice.web.task.client.presenter.InputPlace;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.inject.Inject;

public class TopLevel implements EntryPoint
{
    public static class ActivityMapRegistration
    {
        private final GenericActivityMapper genericActivityMapper;
        private final InputActivityFactory inputActivityFactory;
        private final ReportActivityFactory reportActivityFactory;
        private final OptionsActivityFactory optionsActivityFactory;
        private final AppJobActivityFactory appJobActivityFactory;

        @Inject
        public ActivityMapRegistration(
                GenericActivityMapper genericActivityMapper,
                InputActivityFactory inputActivityFactory,
                ReportActivityFactory reportActivityFactory,
                OptionsActivityFactory optionsActivityFactory,
                AppJobActivityFactory appJobActivityFactory
                )
        {
            this.genericActivityMapper = genericActivityMapper;
            this.inputActivityFactory = inputActivityFactory;
            this.reportActivityFactory = reportActivityFactory;
            this.optionsActivityFactory = optionsActivityFactory;
            this.appJobActivityFactory = appJobActivityFactory;
        }

        public void mapAll()
        {
            genericActivityMapper.map(InputPlace.class, inputActivityFactory);
            genericActivityMapper.map(ReportPlace.class, reportActivityFactory);
            genericActivityMapper.map(OptionsPlace.class, optionsActivityFactory);
            genericActivityMapper.map(AppJobPlace.class, appJobActivityFactory);
        }
    }

    @WithTokenizers({
        InputPlace.Tokenizer.class,
        ReportPlace.Tokenizer.class,
        OptionsPlace.Tokenizer.class,
        AppJobPlace.Tokenizer.class})
    public static interface TimeslicePlaceHistoryMapper extends PlaceHistoryMapper { }

    @Override
    public void onModuleLoad()
    {
        UiOneGinjector injector = GWT.create(UiOneGinjector.class);

        SimpleLayoutPanel container = new SimpleLayoutPanel();

        injector.getActivityManager().setDisplay(container);

        injector.getRegistration().mapAll();

        PlaceHistoryHandler placeHistoryHandler = injector.getPlaceHistoryHandler();
        placeHistoryHandler.register(
                injector.getPlaceController(),
                injector.getEventBus(),
                new InputPlace("default-place", true, null));

        RootLayoutPanel.get().add(container);

        placeHistoryHandler.handleCurrentHistory();
    }

}
