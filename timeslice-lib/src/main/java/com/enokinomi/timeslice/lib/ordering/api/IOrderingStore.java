package com.enokinomi.timeslice.lib.ordering.api;

import java.util.List;

public interface IOrderingStore
{
    /**
     *
     * @param setName
     * @param smaller - null means nothing is smaller, i.e. larger set should be at the beginning.
     * @param larger
     */
    void addPartialOrdering(String setName, String smaller, List<String> larger);

    List<String> requestOrdering(String setName);
}
