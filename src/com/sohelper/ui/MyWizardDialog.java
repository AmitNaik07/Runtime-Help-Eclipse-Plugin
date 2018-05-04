package com.sohelper.ui;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.console.TextConsolePage;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import org.jsoup.Connection.Method;

import org.eclipse.debug.ui.console.IConsole;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.debug.core.model.IProcess;

//import org.eclipse.jface.text.TextSelection;

import com.sohelper.datatypes.GoogleResult;
import com.sohelper.datatypes.StackoverflowAnswer;
import com.sohelper.datatypes.StackoverflowPost;
import com.sohelper.fetchers.GoogleFetcher;
import com.sohelper.fetchers.StackoverflowFetcher;

@SuppressWarnings("unused")
public class MyWizardDialog extends WizardDialog {

    protected static String question;

    static public ISelection getConsoleSelection(IWorkbenchPart part) {
    ISelection selection = null;

    IConsole con = getConsole(part);

    if (con != null){
    selection = new StructuredSelection(con);
    }
    return selection;
    }

    public static IConsole getConsole(IWorkbenchPart part) {
        if(!(part instanceof IViewPart)){
            return null;
        }

        IViewPart vp =(IViewPart) part;
        //System.out.println("Viewpart"+vp);
        if (vp instanceof PageBookView) {
            IPage page = ((PageBookView) vp).getCurrentPage();
            ITextViewer viewer = getViewer(page);
            ISelection sel;
            sel = ((ISelectionProvider) viewer).getSelection();
            ITextSelection textSel = (ITextSelection) sel;
            System.out.println("Selected text :  "+textSel.getText());
            question = textSel.getText();
            //System.out.println("viewer"+viewer);
            
            if (viewer == null || viewer.getDocument() == null)
            return null;
        }

        IConsole con = null;
    try {
    con = (IConsole) ((IConsoleView)part).getConsole();
    } catch (Exception e) {

}
   
return con;
    }

    public static ITextViewer getViewer(IPage page) {
        if(page == null){
            return null;
        }
        if(page instanceof TextConsolePage) {
            return ((TextConsolePage)page).getViewer();
        }
        if(page.getClass().equals(MessagePage.class)){
            // empty page placeholder
            return null;
        }
        try {
            java.lang.reflect.Method method = page.getClass().getDeclaredMethod("getViewer", (Class<?>[])null);
            method.setAccessible(true);
            return (ITextViewer) method.invoke(page, (Object[])null);
        } catch (Exception e) {
            // AnyEditToolsPlugin.logError("Can't get page viewer from the console page", e);
        }
        return null;
    }

public MyWizardDialog(Shell parentShell, IWizard newWizard) {

        super(parentShell, newWizard);
        question="";
        System.out.println("-- getting answer -");
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        Button finish = getButton(IDialogConstants.NEXT_ID);
        finish.setText("Get answers!");
        setButtonLayoutData(finish);
    }
    @Override
    protected void nextPressed() {
        AnswerPage answerPage = (AnswerPage) getCurrentPage().getNextPage();
        System.out.println("-- getting answer --");

        if (getCurrentPage() instanceof QueryPage) {
        		QueryPage questionPage = (QueryPage) getCurrentPage();
        		System.out.println("-- getting question --");
        		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
        		IWorkbenchPart part = activePage.getActivePart();
        		ISelection sel = getConsoleSelection(part);
     
 try {

  questionPage.getContainer().run(false, true, (monitor) ->

  {                    

    monitor.beginTask("Getting answers from Stack Overflow...", 100);



    try {

        List<GoogleResult> googleResults = GoogleFetcher.getGoogleResults(question, monitor);
        List<StackoverflowPost> stackoverflowPosts = StackoverflowFetcher.getStackoverflowPosts(googleResults, monitor);
        List<StackoverflowAnswer> stackoverflowAnswers = StackoverflowFetcher.getStackoverflowAnswers(stackoverflowPosts, monitor, questionPage);
        answerPage.setAnswer(stackoverflowAnswers);
    } catch(IOException e) {
        e.printStackTrace();
    }
    monitor.done();
});
} catch (InvocationTargetException e) {

// TODO Auto-generated catch block

e.printStackTrace();

} catch (InterruptedException e) {

// TODO Auto-generated catch block

e.printStackTrace();

}

            }

        super.nextPressed();

    }

    @Override

    protected void cancelPressed() {

        this.close();

    }
}