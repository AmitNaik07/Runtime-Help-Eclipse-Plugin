package com.sohelper.ui;

import org.eclipse.jface.wizard.Wizard;

public class StackoverflowWizard extends Wizard {
    
    public StackoverflowWizard() {
        setWindowTitle("SO Wizard");
        setNeedsProgressMonitor(true);
    }

    public void addPages() {
        addPage(new QueryPage());
        addPage(new AnswerPage());
    }

    @Override
    public boolean performFinish() {
        return true;
    }

    @Override
    public boolean canFinish() {
        return getContainer().getCurrentPage() instanceof AnswerPage;
    }
}