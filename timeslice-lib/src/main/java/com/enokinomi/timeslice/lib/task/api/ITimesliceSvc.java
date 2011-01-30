package com.enokinomi.timeslice.lib.task.api;

import java.util.List;

import org.joda.time.Instant;


public interface ITimesliceSvc
{

    List<com.enokinomi.timeslice.lib.task.api.StartTag> queryForTags(String who, Boolean sortReverse, Instant minDate, Instant maxDate, int pageSize, int pageIndex);
    List<StartTag> refreshItems(String user, int maxSize, SortDir sortDir, String startingInstant, String endingInstant);
    List<TaskTotalMember> createReport(List<com.enokinomi.timeslice.lib.task.api.TaskTotal> items);
    List<TaskTotalMember> refreshTotals(String user, int maxSize, SortDir sortDir, String startingInstant, String endingInstant, List<String> allowWords, List<String> ignoreWords);
    void addItem(String instantString, String taskDescription, String user);
    void addItems(String user, List<StartTag> items);
    void update(String user, StartTag editedStartTag);

}
