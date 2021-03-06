package com.enokinomi.timeslice.web.prorata.client.ui.api;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.util.Registration;
import com.enokinomi.timeslice.web.prorata.client.core.Group;
import com.google.gwt.user.client.ui.IsWidget;

public interface IProrataManagerPanel extends IsWidget
{
    public static interface Listener
    {
        void addGroupRequested(String name, String target, Double weight);
        void rulesLoadRequested(String text);
        void removeParsedRulesRequested(String text);
        void removeGroupRequested(String groupName, String name);
    }

    Registration addListener(Listener listener);

    void updateGroupInfoTable(List<Group> result, String textRepr);
    void resetInput();
    void clear();
    void setGroupInfo(List<Group> result);
}
