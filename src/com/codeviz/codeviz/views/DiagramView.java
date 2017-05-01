package com.codeviz.codeviz.views;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.service.event.Event;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.codeviz.codeviz.Parser.ClassReader;
import com.codeviz.codeviz.Parser.JDTAdapter;


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
	
	public static final int CLASS_WIDTH = 100;
	public static final int CLASS_HEIGHT = 30;
	private static final int V_MARGIN = 4;
	private static final int H_MARGIN = 5;
	private Graph graph;
	
	
	private Map<String, GraphNode> nodesList = new HashMap<>();
	
	private static final Color color1 = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION);
	
	private IEventBroker eventBroker;
	
	String parsedSrc = "";

	private Canvas canvas;

	private String className = "";
	private String parent = "";
	private LinkedList<String> children = new LinkedList<>();
	private LinkedList<String> interfaces = new LinkedList<>();
	private LinkedList<String> associations = new LinkedList<>();

	private ScrolledComposite scrolledComposite;

	public DiagramView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		graph = new Graph(parent, SWT.NONE);
		
		graph.addSelectionListener(new SelectionAdapter() {
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	                System.out.println(e);
	        }
	
		});
		graph.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
				Object selectedItem = graph.getSelection().get(0);
				
				if(selectedItem instanceof GraphNode){
					GraphNode selectedNode = (GraphNode) selectedItem;
					String selectedClassName = selectedNode.getText();
					System.out.println(selectedClassName);
					
					JDTAdapter.openEditor(selectedClassName);
				}
				
				super.mouseDoubleClick(e);
			}
		});
		
		
		eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
		eventBroker.subscribe(EventTopic.PARSER_DONE, (e) -> prepareDiagram(e));
		
		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scrolledComposite.setExpandVertical(false);
		scrolledComposite.setExpandHorizontal(false);
		
		canvas = new Canvas(scrolledComposite, SWT.NONE);

		scrolledComposite.setContent(canvas);
		Point point = canvas.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		canvas.setSize(point);
		scrolledComposite.setMinSize(point);
		

		canvas.addPaintListener((e) -> paintControl(e));
		

	}
	public void prepareDiagram(Event e) {
		
		className = (String) e.getProperty(e.getPropertyNames()[0]);
		
		parent = ClassReader.readParent();

		children = ClassReader.readChildren();

		interfaces = ClassReader.readInterfaces();

		associations = ClassReader.readAssociations();
		
		int startX = 5, startY = 5;
		
		int width  = (Math.max(1 + interfaces.size(), children.size()) + 2) * (CLASS_WIDTH + H_MARGIN * 2) + startX;
		int height = (int) (Math.ceil(associations.size() / 2d) + 2) * (CLASS_HEIGHT + V_MARGIN * 2) + startY;
		
		canvas.setBounds(0, 0, width, height);
		
		Point point = new Point(width, height);
		canvas.setSize(point);
		scrolledComposite.setMinSize(point);
		
		canvas.redraw();
		zestDiagram();
	}
	
	public void paintControl(PaintEvent event) {
		
		GC gc = event.gc;
		
		
		
		if(className.isEmpty()) return;
		
		int startX = 5, startY = 5;
		
		int width  = (Math.max(1 + interfaces.size(), children.size()) + 2) * (CLASS_WIDTH + H_MARGIN * 2) + startX;
		int height = (int) (Math.ceil(associations.size() / 2.0) + 2) * (CLASS_HEIGHT + V_MARGIN * 2) + startY;
		
		
		int textHeight = gc.getFont().getFontData()[0].getHeight();
		
		gc.setBackground(color1);
		
		
		// Point of interest (class)
		int x = width / 2 - CLASS_WIDTH / 2, y = height / 2 - CLASS_HEIGHT / 2;
		gc.fillRoundRectangle(x, y, CLASS_WIDTH, CLASS_HEIGHT, 5, 5);
		gc.drawText(stringTrancate(className), x + 10, y + CLASS_HEIGHT / 2 - textHeight, true);
		
		
		// Parent and Interfaces
		x = CLASS_WIDTH + startX + H_MARGIN * 3; y = startY + V_MARGIN;
		if(parent.isEmpty()) {
			parent = "Object";
		}
		gc.fillRoundRectangle(x, y, CLASS_WIDTH, CLASS_HEIGHT, 5, 5);
		gc.drawText(stringTrancate(parent), x + 10, y + CLASS_HEIGHT / 2 - textHeight, true);
		
		for (String string : interfaces) {
			x += CLASS_WIDTH + H_MARGIN * 2;
			
			gc.fillRoundRectangle(x, y, CLASS_WIDTH, CLASS_HEIGHT, 5, 5);
			gc.drawText(stringTrancate(string), x + 10, y + CLASS_HEIGHT / 2 - textHeight, true);
		}
		
		
		
		// Associations
		x = startX + H_MARGIN; y += CLASS_HEIGHT + V_MARGIN * 2-1;
		int i = 0, l = (int) Math.ceil(associations.size() / 2.0) - 1;
		for (String string : associations) {
			gc.fillRoundRectangle(x, y, CLASS_WIDTH, CLASS_HEIGHT, 5, 5);
			gc.drawText(stringTrancate(string), x + 10, y + CLASS_HEIGHT / 2 - textHeight, true);
			
			y += CLASS_HEIGHT + V_MARGIN * 2;
			if(i == l){
				x = width - (CLASS_WIDTH + startX + H_MARGIN * 2);
				y = CLASS_HEIGHT + startY + V_MARGIN * 3;
			}
			i++;
		}
		
		if(associations.size() % 2 == 1)
			y += CLASS_HEIGHT + V_MARGIN * 2;
		
		
		// Children
		x = CLASS_WIDTH + startX + H_MARGIN * 2; // y += CLASS_HEIGHT + V_MARGIN * 2;
		for (String string : children) {
			gc.fillRoundRectangle(x, y, CLASS_WIDTH, CLASS_HEIGHT, 5, 5);
			gc.drawText(stringTrancate(string), x + 10, y + CLASS_HEIGHT / 2 - textHeight + 3 , true);
			
			x += CLASS_WIDTH + H_MARGIN * 2;
			
		}
		
	}
	
	private String stringTrancate(String str) {
		return stringTrancate(str, 11);
	}
	
	private String stringTrancate(String str, int size) {
		return str.length() > size? str.substring(0, size) + "…" : str;
	}
	
	private void clearGraph( Graph graph )
	{       
	    Object[] objects = graph.getConnections().toArray() ;           
	    for (int i = 0 ; i < objects.length; i++)
	    {
	        GraphConnection graCon = (GraphConnection) objects[i];
	        if(!graCon.isDisposed())
	            graCon.dispose();
	    }            

	    objects = graph.getNodes().toArray();       
	    for (int i = 0; i < objects.length; i++)
	    {
	        GraphNode graNode = (GraphNode) objects[i];
	        if(!graNode.isDisposed())
	            graNode.dispose();
	    }
	    
	    nodesList.clear();
	}
	
	private void zestDiagram(){
		//Create the Zest Diagram
		clearGraph(this.graph);
		GraphNode target_class = createNode(className);
		
		GraphNode parent_class = createNode(parent);
		
		GraphConnection target_parent_connection = new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, target_class, parent_class);
		target_parent_connection.setText("Parent");
		
		for( String associate_name: associations){
			GraphNode associate_class = createNode(associate_name);
			GraphConnection target_associate_connection = new GraphConnection(this.graph, ZestStyles.CONNECTIONS_SOLID, target_class, associate_class);
			target_associate_connection.setText("Association");
		}
		
		for( String child_name: children){
			GraphNode child_class = createNode(child_name);
			GraphConnection target_child_connection = new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED, child_class, target_class);
			target_child_connection.setText("Child");
		}
		
		for( String interface_name: interfaces){
			GraphNode interface_comp = createNode(interface_name);
			GraphConnection target_interface_connection = new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DASH_DOT, target_class, interface_comp);
			target_interface_connection.setText("Interface");
		}
		
		
		graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
				// Selection listener on graphConnect or GraphNode is not supported
				// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=236528
	}

	private GraphNode createNode(String className) {
		
		if(! nodesList.containsKey(className))
			nodesList.put(className, new GraphNode(this.graph, SWT.NONE, className));
		
		
		return nodesList.get(className);
	}
	
	@Override
	public void setFocus() {
		
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}
