package com.enokinomi.timeslice.lib.userinfo.impl;

public interface IPasswordScheme
{
    String encode(String salt, String password);

    String newSalt();
}
