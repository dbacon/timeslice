package bacond.timeslicer.timeslice;

import java.util.Collection;
import java.util.List;

import org.joda.time.Instant;
import org.junit.Test;

import bacond.timeslicer.app.periodbilling.api.BillableTotal;
import bacond.timeslicer.app.periodbilling.api.BillableTotalList;
import bacond.timeslicer.app.periodbilling.api.Charge;
import bacond.timeslicer.app.periodbilling.api.ChargeBroker;
import bacond.timeslicer.app.periodbilling.api.DefaultChargeFactoryResolver;
import bacond.timeslicer.app.processing.Aggregate;
import bacond.timeslicer.app.processtobilling.ConvertToBillable;
import bacond.timeslicer.app.task.StartTag;
import bacond.timeslicer.app.tasktotal.TaskTotal;


public class TimesliceAppTest_SendToBilling
{
	@Test
	public void test_0()
	{
		TimesliceApp timesliceApp = new TimesliceApp("test.acl", null, null);

		timesliceApp.getStartTagStore().enterTag(new StartTag("bacond", new Instant(    0), "task a (dept-1)", null));
		timesliceApp.getStartTagStore().enterTag(new StartTag("bacond", new Instant(10000), "task b (dept-2)", null));
		timesliceApp.getStartTagStore().enterTag(new StartTag("bacond", new Instant(20000), "task c (dept-3)", null));
		timesliceApp.getStartTagStore().enterTag(new StartTag("bacond", new Instant(30000), "task d (dept-2)", null));
		timesliceApp.getStartTagStore().enterTag(new StartTag("bacond", new Instant(40000), "task e (dept-1)", null));
		timesliceApp.getStartTagStore().enterTag(new StartTag("bacond", new Instant(50000), "mail",    null));
		timesliceApp.getStartTagStore().enterTag(new StartTag("bacond", new Instant(50500), "break",   null));
		timesliceApp.getStartTagStore().enterTag(new StartTag("bacond", new Instant(60500), "mail",    null));
		timesliceApp.getStartTagStore().enterTag(new StartTag("bacond", new Instant(61000), "offline", null));

		List<StartTag> startTags = timesliceApp.queryForTags(
				false,
				new Instant(Integer.MIN_VALUE),
				new Instant(Integer.MAX_VALUE),
				Integer.MAX_VALUE,
				0);

		Collection<TaskTotal> taskTotals = new Aggregate().sumThem(new Aggregate().aggregate(startTags)).values();

		BillableTotalList billableTaskList = new ConvertToBillable().convert(taskTotals);

		// perform assigning of target buckets to charge.
		for (BillableTotal total: billableTaskList.getConstBillableTasks())
		{
			int ind = total.getDescription().indexOf("dept-");

			if (0 <= ind)
			{
				total.setBilledTo(total.getDescription().substring(ind, ind + 6));
			}
			else if ("mail".equals(total.getDescription()))
			{
//				total.setBilledTo("common");
				total.setBilledTo("meta:1,dept-1:2,dept-2");
			}
			else if (!"offline".equals(total.getDescription()))
			{
				total.setBilledTo("dave");
			}
		}

		List<Charge> charges = new ChargeBroker(DefaultChargeFactoryResolver.create()).crunch(billableTaskList);

		System.out.println("Final charges! :");
		for (Charge charge: charges)
		{
			System.out.printf("% 6d : %s\n", charge.getMillis(), charge.getChargeableName());
		}
		System.out.println("End.");
	}
}
