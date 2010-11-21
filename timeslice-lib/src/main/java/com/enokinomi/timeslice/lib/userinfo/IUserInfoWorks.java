package com.enokinomi.timeslice.lib.userinfo;

import com.enokinomi.timeslice.lib.commondatautil.ConnectionWork;

public interface IUserInfoWorks
{

    ConnectionWork<TsSettings> workLoadUserSettings(String username, String prefix);
    ConnectionWork<Void> workSaveUserSettings(String username, TsSettings settings);

}
