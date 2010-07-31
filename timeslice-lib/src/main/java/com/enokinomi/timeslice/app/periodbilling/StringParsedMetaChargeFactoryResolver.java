package com.enokinomi.timeslice.app.periodbilling;

import java.util.regex.Pattern;

import com.enokinomi.timeslice.lib.util.Check;



public class StringParsedMetaChargeFactoryResolver implements IChargeFactoryResolver
{
    private static final Pattern pattern = Pattern.compile("\\A\\Qmeta\\E(:\\d+,[^:]*)+\\Z");

    private boolean isParseable(String input)
    {
        return pattern.matcher(input).matches();
    }

    private ProRataChargeFactory createMetaChargeFactory(String inputString)
    {
        if (!isParseable(inputString))
        {
            throw new RuntimeException("Cannot make meta-charge-factory out of non-parseable string '" + inputString + "'.");
        }

        ProRataChargeFactory metaChargeFactory = new ProRataChargeFactory();

        String[] tokens = inputString.split(":");
        for (int i = 1; i < tokens.length; ++i)
        {
            String[] bucket = tokens[i].split(",");

            metaChargeFactory.addComponent(new ProRataChargeFactory.Component(new BaseChargeFactory(bucket[1]), Integer.valueOf(bucket[0])));
        }

        return metaChargeFactory;
    }

    @Override
    public IChargeFactory resolve(String name)
    {
        Check.disallowNull(name, "name");

        IChargeFactory result = null;

        if (isParseable(name))
        {
            result = createMetaChargeFactory(name);
        }

        return result;
    }
}
