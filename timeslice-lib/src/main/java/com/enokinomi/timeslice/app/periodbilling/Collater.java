/**
 *
 */
package com.enokinomi.timeslice.app.periodbilling;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.enokinomi.timeslice.lib.task.Bucket;
import com.enokinomi.timeslice.lib.util.ITransform;
import com.enokinomi.timeslice.lib.util.Transforms;


public class Collater implements ITransform<List<Charge>, List<Charge>>
{
    @Override
    public List<Charge> apply(List<Charge> allCharges)
    {
        Map<String, List<Charge>> buckets = Bucket.create(Transforms.member(Charge.class, String.class, "chargeableName"))
            .bucket(allCharges)
            .getBuckets();

        List<Charge> result = new ArrayList<Charge>(buckets.size());

        for (Entry<String, List<Charge>> entry: buckets.entrySet())
        {
            String chargeableName = entry.getKey();
            List<Charge> charges = entry.getValue();

            long total = 0;
            for (Charge charge: charges)
            {
                total += charge.getMillis();
            }

            result.add(new Charge(chargeableName, total));
        }

        return result;
    }
}
