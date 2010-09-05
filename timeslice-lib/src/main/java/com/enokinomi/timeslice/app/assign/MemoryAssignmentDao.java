package com.enokinomi.timeslice.app.assign;

import java.util.Map;

public class MemoryAssignmentDao implements IAssignmentDao
{
    private final Map<String, String> storeMap;

    public MemoryAssignmentDao(Map<String, String> storeMap)
    {
        this.storeMap = storeMap;
    }

    @Override
    public void assign(String description, String billTo)
    {
        String value = storeMap.get(description);
        if (null != value)
        {
            if (!value.equals(billTo))
            {
                throw new RuntimeException("Already assigned, cannot assign a different value(assigned: '" + value + "'; attempted re-assign: '" + billTo + "').");
            }
            else
            {
                // value already exists, but is the same, so no-op.
            }
        }
        else
        {
            storeMap.put(description, billTo);
        }
    }

    @Override
    public String getBillee(String description, String valueIfNotAssigned)
    {
        String result = storeMap.get(description);

        if (null != result)
        {
            return result;
        }
        else
        {
            return valueIfNotAssigned;
        }
    }

}
