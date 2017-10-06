package com.codeviz.codeviz.views;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.internal.ZoomManager;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.osgi.service.event.Event;

import com.codeviz.codeviz.Parser.ClassReader;
import com.codeviz.codeviz.Parser.JDTAdapter;

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

public class DiagramView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.codeviz.codeviz.views.DiagramView";

	private Graph graph;
	private static GraphContainer target_class;

	private Map<String, GraphNode> nodesList = new HashMap<>();
	private IEventBroker eventBroker;

	String parsedSrc = "";

	private String className = "";
	private String parent = "";
	private LinkedList<String> children = new LinkedList<>();
	private LinkedList<String> interfaces = new LinkedList<>();
	private LinkedList<String> associations = new LinkedList<>();
	
	private MenuManager menuMgr;

	private static final Color color1 = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	private static final Color color2 = Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
	private static final Color color3 = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);

	public DiagramView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		
		Menu menu;
		graph = new Graph(parent, SWT.NONE);
		ZoomManager zoomManager = new ZoomManager(graph.getRootLayer(),graph.getViewport());
		try {
			menuMgr = new MenuManager();
//			menuMgr.add(new Separator("group.connect")); //$NON-NLS-1$
//			menuMgr.add(new Separator("group.launch")); //$NON-NLS-1$
//			menuMgr.add(new Separator("group.launch.rundebug")); //$NON-NLS-1$
//			menuMgr.add(new Separator("group.history")); //$NON-NLS-1$
//			menuMgr.add(new Separator("group.additions")); //$NON-NLS-1$
//			final IMenuService service = (IMenuService) serviceLocator.getService(IMenuService.class);
//			service.populateContributionManager(menuMgr, "menu:" + getId()); //$NON-NLS-1$
//			for (IContributionItem item : menuMgr.getItems()) {
//	            item.update();
//	           }
			menu = menuMgr.createContextMenu(parent);
//			Decorations parent_decoration = new Decorations(parent, SWT.BORDER);
			menu = new Menu(parent);
			
			MenuItem zoomin = new MenuItem(menu, SWT.CENTER);
			zoomin.setText("Zoom in");
			zoomin.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
//					zoomManager.setZoom(zoomManager.getZoom() + 50);
					zoomManager.zoomIn();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			});
//			Action zoomin_action = new Action("Zoom in") {
//				public void run(){
//						zoomManager.setZoom(zoomManager.getZoom() + 50);
//					
//				}
//			};
			
			MenuItem zoomout = new MenuItem(menu, SWT.CENTER);
			zoomout.setText("Zoom out");
			zoomout.addSelectionListener(new SelectionListener(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
//					zoomManager.setZoom(zoomManager.getZoom() - 50);
					zoomManager.zoomOut();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			});
//			Action zoomout_action = new Action("Zoom out") {
//				public void run(){
//					zoomManager.setZoom(zoomManager.getZoom() - 50);
//				}
//			};
		}
		catch (Exception e) {
//			if (Platform.inDebugMode()) {
//				Platform.getLog(UIPlugin.getDefault().getBundle()).log(StatusHelper.getStatus(e));
//			}
			System.out.println("Menu error");
			menuMgr = null;
			menu = null;
		}
		
		graph.setMenu(menu);
		

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

				if (selectedItem instanceof GraphNode) {
					GraphNode selectedNode = (GraphNode) selectedItem;
					String selectedClassName = selectedNode.getText();
					if (selectedClassName.contains("\n"))
						selectedClassName = selectedClassName.substring(0, selectedClassName.indexOf("\n"));

					System.out.println(selectedClassName);

					JDTAdapter.openEditor(selectedClassName);
				}
				

				super.mouseDoubleClick(e);
			}
			
		});
		
		graph.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseScrolled(MouseEvent e) {
				if((e.stateMask & SWT.CTRL) == 0){
					return;
				}
				if(e.count < 0)
					zoomManager.zoomOut();
				else if(e.count > 0)
					zoomManager.zoomIn();
				
			}
			
		});

		eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
		eventBroker.subscribe(EventTopic.PARSER_DONE, (e) -> prepareDiagram(e));

	}
	

	public void prepareDiagram(Event e) {

		className = (String) e.getProperty(e.getPropertyNames()[0]);

		parent = ClassReader.readParent();

		children = ClassReader.readChildren();

		interfaces = ClassReader.readInterfaces();

		associations = ClassReader.readAssociations();

		drawZestDiagram();
	}

	private void clearGraph(Graph graph) {

		Object[] objects = graph.getConnections().toArray();
		for (int i = 0; i < objects.length; i++) {
			GraphConnection graCon = (GraphConnection) objects[i];
			if (!graCon.isDisposed())
				graCon.dispose();
		}

		objects = graph.getNodes().toArray();
		for (int i = 0; i < objects.length; i++) {
			GraphNode graNode = (GraphNode) objects[i];
			String name = graNode.getText();

			if (name.contains("\n")) {
				name = name.substring(0, name.indexOf("\n"));
			}

			if (name.equals(className) || name.equals(parent) || associations.contains(name) || children.contains(name)
					|| interfaces.contains(name))
				continue;

			if (!graNode.isDisposed())
				graNode.dispose();
			nodesList.remove(name);
		}

		if (target_class != null)
			target_class.dispose();

	}

	private void drawZestDiagram() {
		// Create the Zest Diagram
		clearGraph(this.graph);

		GraphNode target_class = createNode(className);

		if (!parent.isEmpty()) {
			GraphNode parent_class = createNode(parent);
			GraphConnection target_parent_connection = new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED,
					target_class, parent_class);
			target_parent_connection.setText("Parent");
		}

		for (String associate_name : associations) {
			if (parent.equals(associate_name) || interfaces.contains(associate_name)
					|| children.contains(associate_name)) {
				continue;
			}

			GraphNode associate_class = createNode(associate_name);
			GraphConnection target_associate_connection = new GraphConnection(this.graph, ZestStyles.CONNECTIONS_SOLID,
					target_class, associate_class);
			target_associate_connection.setText("Association");
		}

		for (String child_name : children) {
			GraphNode child_class = createNode(child_name);
			GraphConnection target_child_connection = new GraphConnection(this.graph, ZestStyles.CONNECTIONS_DIRECTED,
					child_class, target_class);
			target_child_connection.setText("Child");
		}

		for (String interface_name : interfaces) {
			GraphNode interface_comp = createNode(interface_name);
			GraphConnection target_interface_connection = new GraphConnection(this.graph,
					ZestStyles.CONNECTIONS_DASH_DOT, target_class, interface_comp);
			target_interface_connection.setText("Interface");
		}

		graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
	}

	private GraphNode createNode(String className) {
		if (!nodesList.containsKey(className)) {
			nodesList.put(className, new GraphNode(this.graph, SWT.NONE, ClassReader.getClassDetails(className)));

			Color c = null;

			switch (ClassReader.getClassType(className)) {
			case "s":
				c = color1;
				nodesList.get(className).setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				break;
			case "c":
				c = color2;
				break;
			case "i":
				c = color3;
				break;
			}

			nodesList.get(className).setBackgroundColor(c);
			nodesList.get(className).setHighlightColor(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW));
		}

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
