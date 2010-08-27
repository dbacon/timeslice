package com.enokinomi.timeslice.timeslice;


public interface IUserInfoDao
{
    TsSettings loadUserSettings(String username);
}
