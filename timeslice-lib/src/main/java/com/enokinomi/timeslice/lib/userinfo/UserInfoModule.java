package com.enokinomi.timeslice.lib.userinfo;

import com.google.inject.AbstractModule;

public class UserInfoModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IUserInfoDao.class).to(UserInfoDao.class);
    }

}
