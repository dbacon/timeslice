package com.enokinomi.timeslice.web.gwt.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.web.gwt.client.task.core.StartTag;
import com.enokinomi.timeslice.web.gwt.client.task.ui_compat.Ts107Reader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;

public class ImportBulkItemsDialog extends DialogBox
{
    private final ImportBulkItemsDialogConstants constants = GWT.create(ImportBulkItemsDialogConstants.class);
    private final ImportBulkItemsDialogMessages messages = GWT.create(ImportBulkItemsDialogMessages.class);

    private final ArrayList<StartTag> parsedItems = new ArrayList<StartTag>();

    public static interface BulkItemListener
    {
        void addItems(List<StartTag> items);
    }

    private List<BulkItemListener> listeners = new ArrayList<BulkItemListener>();
    private Button importButton = new Button("");

    public void addBulkItemListener(BulkItemListener listener)
    {
        if (null != listener) listeners.add(listener);
    }

    public void removeBulkItemListener(BulkItemListener listener)
    {
        listeners.remove(listener);
    }

    protected void fireAddItems(List<StartTag> items)
    {
        for(BulkItemListener listener: listeners)
        {
            listener.addItems(items);
        }
    }

    public ImportBulkItemsDialog()
    {
        super(false, true);

        setText(constants.bulkEntry());
        setGlassEnabled(true);
        setAnimationEnabled(true);


        final TextArea textArea = new TextArea();
        textArea.setSize("35em", "20em");

        importButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                fireAddItems(new ArrayList<StartTag>(parsedItems));

                parsedItems.clear();
                textArea.setText("");
                importButton.setText(constants.noItemsToImport());
                importButton.setEnabled(false);
            }
        });
        updateImportButton();
        importButton.setText(constants.noItemsToImport());
        importButton.setEnabled(false);

        textArea.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(ChangeEvent event)
            {
                parsedItems.clear();
                parsedItems.addAll(new Ts107Reader(textArea.getText()).parseItems());

                updateImportButton();
            }
        });

        HorizontalPanel bp = new HorizontalPanel();

        bp.add(importButton);
        bp.add(new Button(constants.close(), new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                ImportBulkItemsDialog.this.hide();
            }
        }));

        DockLayoutPanel dlp = new DockLayoutPanel(Unit.EM);
        dlp.setSize("40em", "25em");
        dlp.addSouth(bp, 3);
        dlp.add(textArea);

        add(dlp);

        center();
    }

    private void updateImportButton()
    {
        if (parsedItems.size() > 0)
        {
            importButton.setText(messages.importNItems(parsedItems.size()));
            importButton.setEnabled(true);
        }
        else
        {
            importButton.setText(constants.noItemsToImport());
            importButton.setEnabled(false);
        }
    }
}
