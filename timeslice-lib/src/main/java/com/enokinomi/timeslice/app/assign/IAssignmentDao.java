package com.enokinomi.timeslice.app.assign;

public interface IAssignmentDao
{
    void assign(String description, String billTo);

    String getBillee(String description, String valueIfNotAssigned);
}
