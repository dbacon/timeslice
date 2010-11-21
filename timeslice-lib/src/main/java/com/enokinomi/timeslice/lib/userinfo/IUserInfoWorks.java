package com.enokinomi.timeslice.lib.userinfo;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;

public interface IUserInfoWorks
{

    IConnectionWork<TsSettings> workLoadUserSettings(String username, String prefix);
    IConnectionWork<Void> workSaveUserSettings(String username, TsSettings settings);

}
