package com.enokinomi.timeslice.lib.commondatautil.api;




public interface IConnectionContext
{

    <R> R doWorkWithinContext(IConnectionWork<R> work);

}
