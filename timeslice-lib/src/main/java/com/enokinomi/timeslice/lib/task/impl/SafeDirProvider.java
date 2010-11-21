package com.enokinomi.timeslice.lib.task.impl;

import java.io.File;

import com.enokinomi.timeslice.lib.task.api.ISafeDirProvider;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SafeDirProvider implements ISafeDirProvider
{
    private final File safeDir;

    @Inject
    SafeDirProvider(@Named("safeDir") File safeDir)
    {
        this.safeDir = safeDir;
    }

    @Override
    public File getSafeDir()
    {
        return safeDir;
    }
}
