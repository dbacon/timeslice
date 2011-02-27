package com.enokinomi.timeslice.web.task.client.ui;

import java.util.List;

import com.enokinomi.timeslice.web.core.client.ui.IIsDialog;
import com.enokinomi.timeslice.web.task.client.core.StartTag;

public interface IImportBulkItemsDialog extends IIsDialog
{
    public interface BulkItemListener
    {
        void addItems(List<StartTag> items);
    }

    void addBulkItemListener(BulkItemListener listener);
    void removeBulkItemListener(BulkItemListener listener);
}
