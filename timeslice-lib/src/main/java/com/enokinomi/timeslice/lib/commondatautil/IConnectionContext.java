package com.enokinomi.timeslice.lib.commondatautil;



public interface IConnectionContext
{

    <R> R doWorkWithinContext(ConnectionWork<R> work);

}
