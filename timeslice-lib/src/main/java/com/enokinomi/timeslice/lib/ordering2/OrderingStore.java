package com.enokinomi.timeslice.lib.ordering2;

import java.util.List;

import com.enokinomi.timeslice.lib.commondatautil.BaseHsqldbStore;
import com.enokinomi.timeslice.lib.ordering.OrderApplier;
import com.google.inject.Inject;

public class OrderingStore extends BaseLowLevelOrderingStore implements IOrderingStore
{
    @Inject
    public OrderingStore(BaseHsqldbStore baseStore)
    {
        super(baseStore);
    }

    @Override
    public List<String> requestOrdering(String setName, List<String> unorderedSetValues)
    {
        if (getBaseStore().versionIsAtLeast(3))
        {
            return new OrderApplier().<String>applyOrdering(unorderedSetValues, getSet(setName));
        }
        else
        {
            return unorderedSetValues;
        }
    }

    //    @Override
    @SuppressWarnings("unused")
    private void setOrdering(String setName, List<String> orderedSetMembers)
    {
        if (getBaseStore().versionIsAtLeast(3))
        {
            deleteSetByName(setName);
            insertSet(setName, orderedSetMembers);
        }
    }

    @Override
    public void addPartialOrdering(String setName, String smaller, List<String> larger)
    {
        if (getBaseStore().versionIsAtLeast(3))
        {
            // TODO: should be wrapped in transaction

            List<String> order = getSet(setName);

            order.removeAll(larger);

            int indexOfAnchor = -1;

            if (null != smaller)
            {
                if (larger.contains(smaller)) throw new IllegalArgumentException("larger set cannot contain smaller element");

                if (!order.contains(smaller)) order.add(smaller);

                indexOfAnchor = order.indexOf(smaller);
            }

            order.addAll(indexOfAnchor + 1, larger);

            deleteSetByName(setName);
            insertSet(setName, order);
        }
    }
}
