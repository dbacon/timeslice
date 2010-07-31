package com.enokinomi.timeslice.app.core;

import java.util.Collection;
import java.util.List;

import org.joda.time.Instant;


public interface ITimesliceStore
{
    //T find(K key);

    /**
     * Throws if tag exists.
     * Throws if tag is after getEnding() or before getStarting()
     *
     * @param tag
     */
    void add(StartTag tag);

    /**
     * Adds all tags only if all tags do not already exist and
     * all tags are between getStarting() and getEnding().
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


    /**
     * Called exactly once before any query, add, remove, or disable is called.
     */
    boolean enable();

    /**
     * No call to query, add, remove, or disable will happen after a call
     *  to disable (only enable will be called).
     */
    boolean disable();

    /**
     * Returns {@code true} if {@code
     * enable()} has been called, and add/remove/query/updateText/disable can be called.
     *
     * @return
     */
    boolean isEnabled();


    /**
     * Returns the earliest time a tag can have to be held in this store.
     * No tag earlier than this instant will be returned by query.
     *
     * @return
     */
    Instant getStarting();

    /**
     * Returns the latest time a tag can have to be held in this store.
     * No tag later than this instant will be returned by query.
     *
     * @return
     */
    Instant getEnding();


    /**
     * Gets the string to be used for the 1st tag.
     *
     * When a query time-span contains getStarting(),
     * the first tag will have its text set to the string
     * returned by getFirstTagText();
     *
     * @return
     */
    String getFirstTagText();

}
