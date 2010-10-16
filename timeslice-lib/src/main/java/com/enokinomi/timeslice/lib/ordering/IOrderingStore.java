package com.enokinomi.timeslice.lib.ordering;

import java.util.List;

public interface IOrderingStore<T>
{
    List<T> requestOrdering(String setName, List<T> unorderedSetValues);

    void setOrdering(String setName, List<T> orderedSetMembers);
}
