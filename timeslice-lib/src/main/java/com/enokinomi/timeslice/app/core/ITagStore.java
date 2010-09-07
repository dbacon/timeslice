package com.enokinomi.timeslice.app.core;

import org.joda.time.DateTime;

public interface ITagStore
{
    /**
    *
    * @param description
    * @param asOf
    * @return
    */
   String lookupBillee(String description, DateTime asOf);

   /**
    * Invalid if anything newer already exists.
    *
    * @param description
    * @param billee
    * @param asOf
    */
   void assignBillee(String description, String billee, DateTime asOf);

}
