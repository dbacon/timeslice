package com.enokinomi.timeslice.lib.userinfo.api;

import com.enokinomi.timeslice.lib.commondatautil.api.IConnectionWork;
import com.enokinomi.timeslice.lib.userinfo.impl.UserDbWorks.AccountData;

public interface IUserDbWorks
{

    IConnectionWork<AccountData> getUserAccountData(String user, String password);

    IConnectionWork<Void> createUser(String user, String hashscheme, String hashsalt, String hashvalue);

    IConnectionWork<Integer> userCount();

}
