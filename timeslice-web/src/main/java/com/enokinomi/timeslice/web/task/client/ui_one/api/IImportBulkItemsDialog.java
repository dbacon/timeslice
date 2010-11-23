package com.enokinomi.timeslice.web.task.client.ui_one.api;

import com.enokinomi.timeslice.web.core.client.ui.IIsDialog;



public interface IImportBulkItemsDialog extends IIsDialog
{
    void addBulkItemListener(BulkItemListener listener);
    void removeBulkItemListener(BulkItemListener listener);
}
