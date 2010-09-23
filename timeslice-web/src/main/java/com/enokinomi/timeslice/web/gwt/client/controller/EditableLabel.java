package com.enokinomi.timeslice.web.gwt.client.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class EditableLabel extends Composite
{
    private static final String Label_Empty = "- empty -";

    private final DeckPanel deckPanel = new DeckPanel();
    private Label label = new Label();
    private final SuggestBox editor;
    private Label empty = new Label(Label_Empty);

    private List<Listener> listeners = new ArrayList<Listener>();

    public Label getLabel()
    {
        return label;
    }

    public SuggestBox getEditor()
    {
        return editor;
    }

    public static interface Listener
    {
        void editBegun();
        void editCanceled();
        void editAccepted(String oldValue, String newValue);
    }

    public void addListener(Listener listener)
    {
        if (null != listener)
        {
            listeners.add(listener);
        }
    }

    protected void fireEditBegun()
    {
        for (Listener l: listeners)
        {
            l.editBegun();
        }
    }

    protected void fireEditCanceled()
    {
        for (Listener l: listeners)
        {
            l.editCanceled();
        }
    }

    protected void fireEditAccepted(String oldValue, String newValue)
    {
        for (Listener l: listeners)
        {
            l.editAccepted(oldValue, newValue);
        }
    }

    private void switchToLabel()
    {
        if (label.getText().trim().length() > 0)
        {
            deckPanel.showWidget(0);
            removeStyleDependentName("empty");
            addStyleDependentName("nonempty");
        }
        else
        {
            deckPanel.showWidget(2);
            removeStyleDependentName("nonempty");
            addStyleDependentName("empty");
        }
    }

    private void switchToEdit()
    {
        editor.setValue(label.getText());
        deckPanel.showWidget(1);
        editor.setFocus(true);
        editor.getTextBox().selectAll();
    }

    protected void onEditCanceled()
    {
        switchToLabel();
        fireEditCanceled();
    }

    protected void onEditBegin()
    {
        switchToEdit();
        fireEditBegun();
    }

    protected void onEditAccepted()
    {
        String oldValue = label.getText();
        label.setText(editor.getText());
        String newValue = label.getText();
        switchToLabel();
        fireEditAccepted(oldValue, newValue);
    }

    public EditableLabel()
    {
        this(new SuggestBox(), "");
    }

    public EditableLabel(String initialText)
    {
        this(new SuggestBox(), initialText);
    }

    public EditableLabel(SuggestBox editor, String initialText)
    {
        this.editor = editor;

        label.setText(initialText);

        ClickHandler labelClickHandler = new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                onEditBegin();
            }
        };
        label.addClickHandler(labelClickHandler);
        empty.addClickHandler(labelClickHandler);

        editor.getTextBox().addBlurHandler(new BlurHandler()
        {
            @Override
            public void onBlur(BlurEvent event)
            {
                onEditCanceled();
            }
        });

        editor.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>()
        {
            @Override
            public void onSelection(SelectionEvent<Suggestion> event)
            {
            }
        });

        editor.getTextBox().addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event)
            {
                if (event.getCharCode() == KeyCodes.KEY_ENTER && event.isControlKeyDown())
                {
                    onEditAccepted();
                }
                else if (event.getCharCode() == KeyCodes.KEY_ESCAPE)
                {
                    onEditCanceled();
                }
            }
        });

        deckPanel.add(label);
        deckPanel.add(editor);
        deckPanel.add(empty);
        initWidget(deckPanel);

        setLabelStylePrimaryName("ts-editable-label");

        switchToLabel();
    }

    public void setLabelStylePrimaryName(String primaryName)
    {
        label.setStylePrimaryName(primaryName);
        empty.setStylePrimaryName(primaryName);
    }

    public void addStyleDependentName(String styleSuffix)
    {
        label.addStyleDependentName(styleSuffix);
        empty.addStyleDependentName(styleSuffix);
    }

    public void removeStyleDependentName(String styleSuffix)
    {
        label.removeStyleDependentName(styleSuffix);
        empty.removeStyleDependentName(styleSuffix);
    }

}
