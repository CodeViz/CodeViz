package com.codeviz.codeviz.views;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class DiagramView extends ViewPart implements Drawable {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.codeviz.codeviz.views.DiagramView";
	
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
					
					new Thread( () -> parseFile(path) ).start();
					
				}
				
				
				/** Using Eclipse JDT */
//					IJavaElement javaElement = JavaUI.getEditorInputJavaElement(input);
//					
//					if(javaElement != null){
//						System.out.println(javaElement.getElementName());
//					} else {
//						System.out.println("null");
//					}
			}
			
			
			
			
		}
	}

	Text label;
	String parsedSrc = null;
	
	
	
	private ISelectionListener mylistener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			if (sourcepart != DiagramView.this) {
				System.out.println(selection);
			}
		}
	};

	public DiagramView() {
	}
	
	@Override
	public void createPartControl(Composite parent) {
//		label = new Text(parent, SWT.WRAP);
//		label.setText("Hello World!, Really?!");
		// getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(mylistener);
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = activePage.getActiveEditor();
		IEditorInput input = editor == null ? null : editor.getEditorInput();
		IPath path = input instanceof IPathEditorInput ? ((IPathEditorInput) input).getPath() : null;
		if (path != null) {
			
			new Thread( () -> parseFile(path) ).start();
			
		}
		
		activePage.addPartListener(new PartListener());
		Canvas canvas = new Canvas(parent, SWT.NONE);
		

		  canvas.addPaintListener(new PaintListener()
		    {
		      @Override
		      public void paintControl(final PaintEvent event)
		      {
		        GC gc = event.gc;

		        gc.drawLine(0, 0, 100, 100);
		        gc.drawLine(100, 0, 0, 100);
		      }
		    });

		
	}
	
	
	private void parseFile(IPath path) {
		
		String src = path.toOSString().replaceAll("(.*src).*", "$1");
		System.out.println(src);
//		Display.getDefault().asyncExec(() -> label.setText(path.toString()));
		
		
		
//		Shell shell = Display.getDefault().getActiveShell(); 
//		shell.setLayout(new FillLayout());
//		final Canvas canvas = new Canvas(shell,SWT.NO_REDRAW_RESIZE);
//		GC gc = new GC(shell);
//		gc.drawLine(0, 0, 30, 40);
//
//		    canvas.addPaintListener(new PaintListener() { 
//		        public void paintControl(PaintEvent e) { 
//		            Rectangle clientArea = canvas.getClientArea(); 
////		            e.gc.setBackground(SWT.COLOR_CYAN); 
//		         e.gc.fillOval(0,0,10,10); 
//		        }
//
//				
//		    });
		
//		String className = path.segment(path.segmentCount()-1).replace(".java", "");
//		if(src.equals(parsedSrc)){
//			readClassInfo(className);
//		} else {
//			ClassReader.readClasses(src);
//			parsedSrc = src;
//			readClassInfo(className);
//		}
		
	}
	
	public  void readClassInfo(String class_name){
		System.out.println("Class Detected: "+class_name);
		String text = "";
		String children = "";
		String associates = "";
		String interfaces = "";
		
		ClassReader.parseClass(class_name);
		
		text += "Class: " + class_name + "\n===============\n";
		
		if(!ClassReader.readParent(class_name).isEmpty())
			text += "Parent: " + ClassReader.readParent(class_name) + "\n";
		
		for( String child : ClassReader.readChildren(class_name))
			children += "   - " + child + "\n";
		
		for( String intfc : ClassReader.readInterfaces(class_name))
			interfaces += "   - " + intfc + "\n";
		
		for ( String associate : ClassReader.readAssociations(class_name))
			associates += "   - " + associate+"\n";
		
		
		if(!interfaces.isEmpty())
			text += "Interfaces: \n" + interfaces;
		
		if(!children.isEmpty())
			text += "Children: \n" + children;
		
		if(!associates.isEmpty())
			text += "Associates: \n" + associates;
		
		System.out.println(text);
		final String fText = text;
		
		Display.getDefault().asyncExec(() -> label.setText(fText));
		
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

	@Override
	public long internal_new_GC(GCData data) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void internal_dispose_GC(long handle, GCData data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAutoScalable() {
		// TODO Auto-generated method stub
		return false;
	}

}
