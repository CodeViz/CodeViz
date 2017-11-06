package com.codeviz.codeviz.views;

import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.codeviz.codeviz.Parser.ClassReader;
import com.codeviz.codeviz.Parser.JDTAdapter;
import com.codeviz.codeviz.queryParser.QueryParser;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class VisualizerView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.codeviz.codeviz.views.VisualizerView";

	private IEventBroker eventBroker;

	private class PartListener implements IPartListener2 {
		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partOpened(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partHidden(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
			if (partRef.getPart(true) instanceof IEditorPart) {
				IEditorPart editor = (IEditorPart) partRef.getPart(true);
				IEditorInput input = editor == null ? null : editor.getEditorInput();
				IPath path = input instanceof IPathEditorInput ? ((IPathEditorInput) input).getPath() : null;
				if (path != null) {

					new Thread(() -> parseFile(path)).start();

				}

				/** Using Eclipse JDT */
				 IJavaElement javaElement =
				 JavaUI.getEditorInputJavaElement(input);
				 JDTAdapter.setCurrentProject(javaElement.getJavaProject());
				 autocomplete = new AutoCompleteField(query_bar, new TextContentAdapter(), QueryParser.getProposals());
				//
				// if(javaElement != null){
				// System.out.println(javaElement.getElementName());
				// } else {
				// System.out.println("null");
				// }
			}

		}
	}

	Text label;
	Text query_bar;
	AutoCompleteField autocomplete;
	String parsedSrc = null;

	private ISelectionListener mylistener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			if (sourcepart != VisualizerView.this) {
				System.out.println(selection);
			}
		}
	};

	public VisualizerView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1,false));
		query_bar = new Text(parent,SWT.BORDER | SWT.WRAP | SWT.LEFT);
		query_bar.setText("Enter Query here");
		FocusListener fl = new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				query_bar.setText("");
				query_bar.removeFocusListener(this);
			}

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		};
		query_bar.addFocusListener(fl);
		autocomplete = new AutoCompleteField(query_bar, new TextContentAdapter(), QueryParser.getProposals());
		
		
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		query_bar.setLayoutData(gridData);
		query_bar.addListener(SWT.Traverse, new Listener()
	    {
	        @Override
	        public void handleEvent(Event event)
	        {
	            if(event.detail == SWT.TRAVERSE_RETURN)
	            {
	                label.setText(QueryParser.parseAction(query_bar.getText().trim()));
	                
	                query_bar.setText("");
	                autocomplete = new AutoCompleteField(query_bar, new TextContentAdapter(), QueryParser.getProposals());
	            }	            
	        }

	    });
		label = new Text(parent, SWT.WRAP | SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		label.setText("Open a java file to parse it.");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		label.setLayoutData(gridData);

		eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);

		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		activePage.addPartListener(new PartListener());
	}

	private void parseFile(IPath path) {

		String src = path.toOSString().replaceAll("(.*src).*", "$1");
		// System.out.println(src);

		String className = path.segment(path.segmentCount() - 1).replace(".java", "");
		if (src.equals(parsedSrc)) {
			readClassInfo(className);
		} else {
			ClassReader.readClasses(src);
			parsedSrc = src;
			readClassInfo(className);
		}

	}

	public void readClassInfo(String class_name) {
		System.out.println("Class Detected: " + class_name);
		String text = "";
		String children = "";
		String associates = "";
		String interfaces = "";

		Display.getDefault().asyncExec(() -> label.setText("Parsing..."));
		ClassReader.parseClass(class_name);

		text += "Class: " + class_name + "\n===============\n";

		if (!ClassReader.readParent().isEmpty())
			text += "Parent: " + ClassReader.readParent() + "\n";

		for (String child : ClassReader.readChildren())
			children += "   - " + child + "\n";

		for (String intfc : ClassReader.readInterfaces())
			interfaces += "   - " + intfc + "\n";

		for (String associate : ClassReader.readAssociations())
			associates += "   - " + associate + "\n";

		if (!interfaces.isEmpty())
			text += "Interfaces: \n" + interfaces;

		if (!children.isEmpty())
			text += "Children: \n" + children;

		if (!associates.isEmpty())
			text += "Associates: \n" + associates;

		// System.out.println(text);
		final String fText = text;

		Display.getDefault().asyncExec(() -> label.setText(fText));
		eventBroker.post(EventTopic.PARSER_DONE, class_name);
	}

	@Override
	public void setFocus() {
		label.setFocus();
	}

	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(mylistener);
		super.dispose();
	}
	
	private void showMessage(String message) {
		//For Query Messages
		MessageDialog.openInformation(
			query_bar.getShell(),
			"Visualizer View",
			message);
	}

}
