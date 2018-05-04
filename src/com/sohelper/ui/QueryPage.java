package com.sohelper.ui;

import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class QueryPage extends WizardPage {
    private Composite container;
    private boolean isAcceptedOnly = false;
    private boolean isUpVotedOnly = false;
    public QueryPage() {
        super("File Page");
        setTitle("SO ready to help");
        setDescription("Performing Stackoverflow Search");
        setPageComplete(true);
    }
    
    @Override
    public void createControl(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 20;
        
        container = new Composite(parent, SWT.NONE);
        container.setLayout(layout);
        
        GridLayout optionsLayout = new GridLayout();
        optionsLayout.horizontalSpacing = 20;
        optionsLayout.numColumns = 2;
        
        GridData optionsData = new GridData(GridData.VERTICAL_ALIGN_END);
        optionsData.horizontalSpan = 2;
        optionsData.horizontalAlignment = GridData.FILL;
        Composite optionsCont = new Composite(container, SWT.NONE);
        
        optionsCont.setLayoutData(optionsData);
        optionsCont.setLayout(optionsLayout);
       
        setControl(container);
        setPageComplete(true);
        
        Button onlyAcceptedCheckbox = new Button(optionsCont, SWT.CHECK);
        Label onlyAcceptedLbl = new Label(optionsCont, SWT.NONE);
        onlyAcceptedLbl.setText("Accepted only");
        
        onlyAcceptedCheckbox.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                Button btn = (Button) event.getSource();
                isAcceptedOnly = btn.getSelection();
            }
        });
        
        onlyAcceptedCheckbox.setSelection(false); 
                
        Button votedUpCheckbox = new Button(optionsCont, SWT.CHECK);
        Label onlyVotedUpLbl = new Label(optionsCont, SWT.NONE);
        onlyVotedUpLbl.setText("Voted only");
        
        votedUpCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Button btn = (Button) event.getSource();
                isUpVotedOnly = btn.getSelection();
            }
        });
    }
    
    @Override
    public boolean canFlipToNextPage(){
        return true;
    }

    public IWizardContainer getContainer(){
        return super.getContainer();
    }
    
    public boolean isAcceptedOnly() {
        return isAcceptedOnly;
    }
    
    public boolean isUpVotedOnly() {
        return isUpVotedOnly;
    }
}