package com.enokinomi.timeslice.web.prorata.client.tree;

import com.enokinomi.timeslice.web.prorata.client.core.GroupComponent;

public interface IRuleSource
{
    GroupComponent[] expand(String name);
}
