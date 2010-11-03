package com.enokinomi.timeslice.web.task.client.ui_one.api;



public interface IImportBulkItemsDialog extends IIsDialog
{
    void addBulkItemListener(BulkItemListener listener);
    void removeBulkItemListener(BulkItemListener listener);
}
