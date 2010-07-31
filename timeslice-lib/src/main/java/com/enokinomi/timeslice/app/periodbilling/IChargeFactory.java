package com.enokinomi.timeslice.app.periodbilling;

import java.util.List;


public interface IChargeFactory
{
    List<Charge> createCharges(long millis);
}
