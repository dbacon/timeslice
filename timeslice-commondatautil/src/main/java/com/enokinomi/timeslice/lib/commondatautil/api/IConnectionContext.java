package com.enokinomi.timeslice.lib.commondatautil.api;




public interface IConnectionContext
{

    <R> R doWorkWithinWritableContext(IConnectionWork<R> work);

    <R> R doWorkWithinReadOnlyContext(IConnectionWork<R> work);

}
