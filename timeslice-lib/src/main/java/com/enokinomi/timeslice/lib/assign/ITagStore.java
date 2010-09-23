package com.enokinomi.timeslice.lib.assign;

import java.util.List;

import org.joda.time.DateTime;

public interface ITagStore
{
    /**
    *
    * @param description
    * @param asOf
    * @return
    */
    String lookupBillee(String description, DateTime asOf, String valueOnMiss);

   /**
    * Invalid if anything newer already exists.
    *
    * @param description
    * @param billee
    * @param asOf
    */
   void assignBillee(String description, String billee, DateTime asOf);

   List<String> getAllBillees();

}
