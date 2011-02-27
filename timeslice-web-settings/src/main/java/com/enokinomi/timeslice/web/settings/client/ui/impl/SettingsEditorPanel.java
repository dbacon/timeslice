package com.enokinomi.timeslice.web.settings.client.ui.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SettingsEditorPanel extends Composite implements ISettingsEditorPanel
{
    private static ThisUiBinder uiBinder = GWT.create(ThisUiBinder.class);
    public interface ThisUiBinder extends UiBinder<Widget, SettingsEditorPanel> { }
    public interface ThisStyle extends CssResource { String live(); }

    private final SettingsEditorPanelConstants constants = GWT.create(SettingsEditorPanelConstants.class);

    @UiField protected Button refreshButton;
    @UiField protected TextBox currentValueTextBox;
    @UiField protected TextBox newNameTextBox;
    @UiField protected TextBox newValueTextBox;
    @UiField protected Button saveButton;
    @UiField protected Button cancelButton;
    @UiField protected FlexTable table;
    @UiField protected Panel settingEditorPanel;
    @UiField protected ThisStyle style;

    private final List<Row> rows = new ArrayList<Row>();

    static final String CREATE_NEW = "New Value:";
    static final int ActionColumn_Edit = 2;
    static final int ActionColumn_Delete = 3;

    @Inject
    SettingsEditorPanel()
    {
        initWidget(uiBinder.createAndBindUi(this));
    }

    private List<Listener> listeners = new ArrayList<SettingsEditorPanel.Listener>();

    public void addListener(Listener listener)
    {
        if (listener != null) listeners.add(listener);
    }

    @UiHandler("cancelButton")
    protected void onClick_CancelButton(ClickEvent e)
    {
        clearInputs();
    }

    @UiHandler("refreshButton")
    protected void onClick_RefreshButton(ClickEvent e)
    {
        fireOnRefreshButtonClicked();
    }

    @UiHandler("saveButton")
    protected void onClick_SaveButton(ClickEvent e)
    {

        if (currentValueTextBox.getText().equals(CREATE_NEW))
        {
            fireOnItemAdded(newNameTextBox.getText(), newValueTextBox.getText());
            clearInputs();
        }
        else
        {
            // do save
            fireOnItemEdited(
                    newNameTextBox.getText(),
                    currentValueTextBox.getText(),
                    newValueTextBox.getText());
            clearInputs();
        }
    }

    private void clearInputs()
    {
        newNameTextBox.setText("");
        newValueTextBox.setText("");
        currentValueTextBox.setText(CREATE_NEW);
        newNameTextBox.setEnabled(true);
        settingEditorPanel.getElement().removeClassName(style.live());
    }

    protected void fireOnItemDeleted(String name, String value)
    {
        for (Listener listener: listeners) listener.onItemDeleted(name, value);
    }

    protected void fireOnItemEdited(String name, String oldValue, String newValue)
    {
        for (Listener listener: listeners) listener.onItemEdited(name, oldValue, newValue);
    }

    protected void fireOnItemAdded(String name, String value)
    {
        for (Listener listener: listeners) listener.onItemAdded(name, value);
    }

    protected void fireOnRefreshButtonClicked()
    {
        for (Listener listener: listeners) listener.onRefreshButtonClicked();
    }

    @Override
    public void clear()
    {
        table.removeAllRows();

        table.getRowFormatter().addStyleName(0, "tsTableHeader");
        table.setText(0, 0, constants.name());
        table.setText(0, 1, constants.value());
    }

    @UiHandler("table")
    protected void onTableClicked(ClickEvent event)
    {
        Cell cell = table.getCellForEvent(event);
        if (cell.getRowIndex() < 1) return; // we don't care about the header row.

        Row item = rows.get(cell.getRowIndex() - 1);
        int column = cell.getCellIndex();

        if (column == ActionColumn_Edit)
        {
            newNameTextBox.setText(item.getName());
            currentValueTextBox.setText(item.getValue());
            newValueTextBox.setText(item.getValue());
            newNameTextBox.setEnabled(false);
            settingEditorPanel.getElement().addClassName(style.live());


//            fireOnItemEdited(item.getName(), item.getValue(), newValue);
        }
        else if (column == ActionColumn_Delete)
        {
            fireOnItemDeleted(item.getName(), item.getValue());
        }
    }

    @Override
    public void setSettings(Map<String, List<String>> settings)
    {
        rows.clear();

        for (Entry<String, List<String>> entry: settings.entrySet())
        {
            for (String scalar: entry.getValue())
            {
                rows.add(new Row(entry.getKey(), scalar, true));
            }
        }

        render();
    }

    static class Row
    {
        private final String name;
        private final String value;
        private final boolean editable;

        Row(String name, String value, boolean editable)
        {
            this.name = name;
            this.value = value;
            this.editable = editable;
        }

        public String getName()
        {
            return name;
        }

        public String getValue()
        {
            return value;
        }

        public boolean isEditable()
        {
            return editable;
        }

    }

    private void render()
    {
        clear();

        int row = 1;
        for (Row item: rows)
        {
            table.getRowFormatter().addStyleName(row, (row % 2 == 0) ? "evenRow" : "oddRow");

            int col = 0;

            table.setText(row, col, item.getName());
            ++col;

            table.setText(row, col, item.getValue());
            ++col;

            table.setText(row, col, "edit");
            ++col;

            table.setText(row, col, "delete");
            ++col;

            ++row;
        }
    }

}
