package com.enokinomi.timeslice.lib.userinfo.impl;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public final class Sha1V1Scheme implements IPasswordScheme
{
    private static String bytesToHexString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();

        char[] digit = {
                '0', '1', '2', '3',
                '4', '5', '6', '7',
                '8', '9', 'a', 'b',
                'c', 'd', 'e', 'f',
        };

        for (byte b : bytes)
        {
            sb.append(digit[(b>>4) & 0x0f]);
            sb.append(digit[(b>>0) & 0x0f]);
        }

        return sb.toString();
    }

    @Override
    public String encode(String salt, String password)
    {
        try
        {
            Charset utf8 = Charset.forName("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(salt.getBytes(utf8));
            md.update(password.getBytes(utf8));
            return bytesToHexString(md.digest());
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Could not use SHA: " + e.getMessage());
        }
    }

    @Override
    public String newSalt()
    {
        return UUID.randomUUID().toString();
    }
}
