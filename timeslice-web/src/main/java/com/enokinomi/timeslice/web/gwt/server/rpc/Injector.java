package com.enokinomi.timeslice.web.gwt.server.rpc;

@Deprecated
public class Injector
{
    private final AuthenticatedTimesliceSvc authenticatedTimesliceSvc;
    private final AssignmentSvcSession assignmentSvcSession;

    public Injector(AuthenticatedTimesliceSvc authenticatedTimesliceSvc, AssignmentSvcSession assignmentSvcSession)
    {
        this.authenticatedTimesliceSvc = authenticatedTimesliceSvc;
        this.assignmentSvcSession = assignmentSvcSession;
    }

    public void inject(INeedsInjectionHelp injectable)
    {
        injectable.inject(authenticatedTimesliceSvc, assignmentSvcSession);
    }
}
