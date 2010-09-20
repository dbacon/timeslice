package com.enokinomi.timeslice.lib.task;

import java.io.File;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SafeDirProvider implements ISafeDirProvider
{
    private final File safeDir;

    @Inject
    public SafeDirProvider(@Named("safeDir") File safeDir)
    {
        this.safeDir = safeDir;
    }

    @Override
    public File getSafeDir()
    {
        return safeDir;
    }
}
