package com.enokinomi.timeslice.lib.task.api;

import java.util.Collection;
import java.util.List;

import org.joda.time.Instant;



public interface ITimesliceStore
{

    /**
     * Throws if tag exists.
     *
     * @param tag
     */
    void add(StartTag tag);

    /**
     * Adds all tags only if all tags do not already exist.
     *
     * @param tags
     * @param strict - if true nothing is added if any tags are invalid, if false all
     *  valid tags are added, and the invalid tags are ignored.
     */
    void addAll(Collection<? extends StartTag> tags, boolean strict);

    /**
     * Throws if tag does not exist.
     *
     * @param tag
     */
    void remove(StartTag tag);

    /**
     * Throws if tag does not exist.
     *
     * @param tag
     */
    void updateText(StartTag tag);

    /**
     * The first tag will not have any text.
     *
     * TODO: Need to scan a bit back to get the text for the first item.
     *
     * @param who
     * @param starting
     * @param ending
     * @return
     */
    List<StartTag> query(String who, Instant starting, Instant ending, int pageSize, int pageIndex);

}
