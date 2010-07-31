package com.enokinomi.timeslice.app.periodbilling;

public class Charge
{
    private final String chargeableName;
    private final long millis;

    public Charge(String chargeableName, long millis)
    {
        this.chargeableName = chargeableName;
        this.millis = millis;
    }

    public String getChargeableName()
    {
        return chargeableName;
    }

    public long getMillis()
    {
        return millis;
    }
}
