package com.enokinomi.timeslice.app.periodbilling;

import java.util.Arrays;


public class DefaultChargeFactoryResolver
{
    public static IChargeFactoryResolver create()
    {
        return CascadingChargeFactoryResolver.create(Arrays.asList(
                new StringParsedMetaChargeFactoryResolver(),
                new NamedMetaChargeFactoryResolver(),
                new BaseChargeFactoryResolver()
                ));
    }
}
