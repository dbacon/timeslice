package com.enokinomi.timeslice.web.task.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.enokinomi.timeslice.web.task.client.core.StartTag;
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
import com.google.inject.Inject;

public class ImportBulkItemsDialog extends DialogBox implements IImportBulkItemsDialog
{
    private final ImportBulkItemsDialogConstants constants;
    private final ImportBulkItemsDialogMessages messages;
    private Button importButton = new Button("");

    private List<BulkItemListener> listeners = new ArrayList<BulkItemListener>();

    private final ArrayList<StartTag> parsedItems = new ArrayList<StartTag>();
//    private final ITs107Reader ts107Reader;

    public DialogBox asDialog()
    {
        return this;
    }

    @Override
    public void addBulkItemListener(BulkItemListener listener)
    {
        if (null != listener) listeners.add(listener);
    }

    @Override
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

    @Inject
    ImportBulkItemsDialog(ImportBulkItemsDialogConstants constants, ImportBulkItemsDialogMessages messages)
    {
        super(false, true);

        this.constants = constants;
        this.messages = messages;
//        this.ts107Reader = ts107Reader;

        initWidgets();
    }

    public void initWidgets()
    {
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
//                parsedItems.addAll(ts107Reader.parseItems(textArea.getText()));

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
