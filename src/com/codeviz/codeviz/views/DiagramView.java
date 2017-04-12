package com.codeviz.codeviz.views;

import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
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

public class DiagramView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.codeviz.codeviz.views.DiagramView";

	String parsedSrc = "";

	private Canvas canvas;

	private String parent = "";
	private LinkedList<String> children = new LinkedList<>();
	private LinkedList<String> interfaces = new LinkedList<>();
	private LinkedList<String> associations = new LinkedList<>();

	public DiagramView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		canvas = new Canvas(parent, SWT.NONE);

		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(final PaintEvent event) {
				GC gc = event.gc;

			}
		});
		canvas.redraw();

	}

	public void prepareDiagram(String class_name) {

		parent = ClassReader.readParent(class_name);

		children = ClassReader.readChildren(class_name);

		interfaces = ClassReader.readInterfaces(class_name);

		associations = ClassReader.readAssociations(class_name);

	}

	@Override
	public void setFocus() {
		
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}
