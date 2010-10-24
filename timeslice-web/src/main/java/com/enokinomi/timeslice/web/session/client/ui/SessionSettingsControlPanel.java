package com.enokinomi.timeslice.web.session.client.ui;

import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.web.session.client.core.ISessionSvc;
import com.enokinomi.timeslice.web.session.client.core.ISessionSvcAsync;
import com.enokinomi.timeslice.web.task.client.controller.IAuthTokenHolder;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

public class SessionSettingsControlPanel extends Composite implements ClickHandler
{
    private final SessionSettingsPanel ssp = new SessionSettingsPanel();

    private final ISessionSvcAsync sessionSvc = GWT.create(ISessionSvc.class);

    private final Button refreshButton = new Button("Refresh");

    private IAuthTokenHolder authTokenHolder;

    @Inject
    public SessionSettingsControlPanel(IAuthTokenHolder authTokenHolder)
    {
        this.authTokenHolder = authTokenHolder;

        refreshButton.addClickHandler(this);

        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(10);
        hp.add(refreshButton);

        VerticalPanel vp = new VerticalPanel();
        vp.add(hp);
        vp.add(ssp);

        initWidget(vp);
    }

    @Override
    public void onClick(ClickEvent event)
    {
        if (event.getSource().equals(refreshButton))
        {
            if (null == authTokenHolder)
            {
                GWT.log("No auth-token available, cannot update settings view.");
            }
            else
            {
                String authToken = authTokenHolder.getAuthToken();

                ssp.clear();

                sessionSvc.getSettings(authToken, new AsyncCallback<Map<String,List<String>>>()
                    {
                        @Override
                        public void onSuccess(Map<String, List<String>> result)
                        {
                            ssp.add(result);
                        }

                        @Override
                        public void onFailure(Throwable caught)
                        {
                            GWT.log("Getting session settings failed: " + caught.getMessage(), caught);
                        }
                    });
            }
        }
    }
}
