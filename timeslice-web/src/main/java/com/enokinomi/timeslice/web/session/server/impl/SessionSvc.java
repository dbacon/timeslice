package com.enokinomi.timeslice.web.session.server.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enokinomi.timeslice.lib.userinfo.api.TsSettings;
import com.enokinomi.timeslice.web.session.client.core.ISessionSvc;
import com.enokinomi.timeslice.web.session.server.core.ISessionTracker;
import com.enokinomi.timeslice.web.session.server.core.SessionData;
import com.google.inject.Inject;

public class SessionSvc implements ISessionSvc
{
    private final ISessionTracker tracker;

    @Inject
    SessionSvc(ISessionTracker tracker)
    {
        this.tracker = tracker;
    }

    private List<String> getList(Map<String, List<String>> map, String key)
    {
        List<String> list = map.get(key);
        if (null == list)
        {
            list = new ArrayList<String>();
            map.put(key, list);
        }
        return list;
    }

    private void addValue(Map<String, List<String>> map, String key, String value)
    {
        getList(map, key).add(value);
    }

    @Override
    public Map<String, List<String>> getSettings(String authToken)
    {
        SessionData sd = tracker.checkToken(authToken);

        Map<String, List<String>> settings = new LinkedHashMap<String, List<String>>();

        String sessionExpiresAt = sd.getExpiresAt().toString();
        String loggedInAt = sd.getLoggedInAt().toString();
        String user = sd.getUser();
        String sessionKey = sd.getUuid();

        addValue(settings, "sessionExpiresAt", sessionExpiresAt);
        addValue(settings, "loggedInAt", loggedInAt);
        addValue(settings, "user", user);
        addValue(settings, "sessionKey", sessionKey);

        TsSettings tsSettings = sd.getSettings();

        for (String key: tsSettings.getKeys())
        {
            List<String> values = tsSettings.getRawValuesForKey(key);
            for(String value: values)
            {
                addValue(settings, key, value);
            }
        }

        return settings;
    }
}
