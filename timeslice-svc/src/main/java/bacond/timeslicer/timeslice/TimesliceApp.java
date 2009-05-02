package bacond.timeslicer.timeslice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;

import bacond.timeslicer.app.generic.GenericStore;
import bacond.timeslicer.app.task.StartTag;
import bacond.timeslicer.app.task.StartTagIo;
import bacond.timeslicer.app.todo.TodoItem;

public class TimesliceApp
{
	public static final String Key_Upgrade = "upgrade";

	private final GenericStore<StartTag> startTagStore = new GenericStore<StartTag>();
	private final GenericStore<TodoItem> todoStore = new GenericStore<TodoItem>();

	private String aclFileName;
	private String safeDir;
	private String updateUrl;

	public TimesliceApp(String aclFilename, String safeDir, String updateUrl)
	{
		this.aclFileName = aclFilename;
		this.safeDir = safeDir;
		this.updateUrl = updateUrl;
	}

	public GenericStore<StartTag> getStartTagStore()
	{
		return startTagStore;
	}

	public GenericStore<TodoItem> getTodoStore()
	{
		return todoStore;
	}

	public String getAclFileName()
	{
		return aclFileName;
	}

	public void setAclFileName(String aclFileName)
	{
		this.aclFileName = aclFileName;
	}

	public String getSafeDir()
	{
		return safeDir;
	}

	public void setSafeDir(String safeDir)
	{
		this.safeDir = safeDir;
	}

	public String getUpdateUrl()
	{
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl)
	{
		this.updateUrl = updateUrl;
	}

	public void snapshot(String key) throws IOException
	{
		if (!canSaveLoad())
		{
			throw new RuntimeException("Cannot save/load as no safe-dir is available.");
		}

		writeBackup("snapshot-" + ISODateTimeFormat.dateTime().print(new Instant()) + "-" + key);
	}

	public boolean canSaveLoad()
	{
		return null != getSafeDir();
	}

	private File findBackupFile(String key)
	{
		return new File(FilenameUtils.concat(getSafeDir(), "backup-" + key + ".dat"));
	}

	public void writeBackup(String key) throws IOException
	{
		List<String> lines = new ArrayList<String>(startTagStore.getAllItems().size());

		for (StartTag tag: startTagStore.getAllItems())
		{
			lines.add(StartTagIo.toLine(tag));
		}

		FileUtils.writeLines(findBackupFile(key), lines);
	}

	public TimesliceApp preload(boolean doPreload)
	{
		if (doPreload)
		{
			if (!canSaveLoad())
			{
				throw new RuntimeException("Pre-load requested, but no safe-dir available to save/load.");
			}

			File backupFile = findBackupFile(Key_Upgrade);
			try
			{
				List<StartTag> preloadItems = new StartTagIo().readItems(new FileInputStream(backupFile));

				startTagStore.enterAllTags(preloadItems);

				System.out.println("Pre-loaded " + preloadItems.size() + " item(s) from '" + backupFile + "'.");
			}
			catch (IOException e)
			{
				System.err.println("Could not pre-load file '" + backupFile + "': " + e.getMessage());
			}
		}

		return this;
	}
}