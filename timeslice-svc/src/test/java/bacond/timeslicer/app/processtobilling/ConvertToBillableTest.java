package bacond.timeslicer.app.processtobilling;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import bacond.timeslicer.app.periodbilling.api.BillableTotal;
import bacond.timeslicer.app.periodbilling.api.BillableTotalList;
import bacond.timeslicer.app.processtobilling.ConvertToBillable;
import bacond.timeslicer.app.tasktotal.TaskTotal;


public class ConvertToBillableTest
{
	@Test
	public void test_0() throws Exception
	{
		List<TaskTotal> taskTotals = Arrays.asList(
				new TaskTotal("user", 245246, "working on A")
				);

		BillableTotalList taskList = new ConvertToBillable().convert(taskTotals);

		assertNotNull("taskList should not be null", taskList);

		System.out.println("totals:");
		for (BillableTotal total: taskList.getConstBillableTasks())
		{
			System.out.printf("  %2$s(%3$d) -> %1$s\n",
					total.getBilledTo(),
					total.getDescription(),
					total.getMillis());
		}
		System.out.println("end.");
	}
}
