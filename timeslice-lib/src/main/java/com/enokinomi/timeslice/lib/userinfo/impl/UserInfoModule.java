package com.enokinomi.timeslice.lib.userinfo.impl;

import com.enokinomi.timeslice.lib.userinfo.api.IUserDbWorks;
import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoDao;
import com.enokinomi.timeslice.lib.userinfo.api.IUserInfoWorks;
import com.google.inject.AbstractModule;

public class UserInfoModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IUserInfoDao.class).to(UserInfoDao.class);
        bind(IUserInfoWorks.class).to(UserInfoWorks.class);

        bind(IUserDbWorks.class).to(UserDbWorks.class);
    }

}
