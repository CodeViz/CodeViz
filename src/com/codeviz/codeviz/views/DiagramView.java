package com.codeviz.codeviz.views;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
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

	private static Graph graph;
	private static GraphContainer target_class;
	

	private static Action zoom_in;
	private static Action zoom_out;
	private static Action compact_mode_toggle;
	private static Action refresh;
	
	private static ZoomManager zoomManager;

	private static boolean compact_mode = true;
	
	private static Map<String, GraphNode> nodesList = new HashMap<>();
	private static IEventBroker eventBroker;

	String parsedSrc = "";

	private static String className = "";
	private static String parent = "";
	private static LinkedList<String> children = new LinkedList<>();
	private static LinkedList<String> interfaces = new LinkedList<>();
	private static LinkedList<String> associations = new LinkedList<>();
	
	private static MenuManager menuMgr;

	private static Color color1 = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	private static Color color2 = Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
	private static Color color3 = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
	
	private static Color colorP = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_MAGENTA);
	private static Color colorA = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE);
	private static Color colorC = Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA);
	private static Color colorI = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
	
	private static int threshold = 4;

	public DiagramView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		
		
		Menu menu;
		parent.setLayout(new FillLayout());
		graph = new Graph(parent, SWT.NONE);
		
		makeActions();
		hookContextMenu();
		contributeToActionBars();
		zoomManager = new ZoomManager(graph.getRootLayer(),graph.getViewport());
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
				}else if( selectedItem instanceof GraphConnection){
					GraphConnection selectedConnection = (GraphConnection) selectedItem;
					String selectedConnectionType = selectedConnection.getText();
					if(selectedConnectionType.equals("Association")){
						GraphNode source = selectedConnection.getSource();
						String source_name = source.getText();
						if(source_name.contains("\n"))
							source_name = source_name.substring(0, source_name.indexOf("\n"));
						
						GraphNode dest = selectedConnection.getDestination();
						String dest_name = source.getText();
						if(dest_name.contains("\n"))
							dest_name = dest_name.substring(0, dest_name.indexOf("\n"));
						
						//TODO call parser link method here.
					}
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
	

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				DiagramView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(graph);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(zoom_in);
		manager.add(new Separator());
		manager.add(zoom_out);
		manager.add(new Separator());
		manager.add(compact_mode_toggle);
		manager.add(new Separator());
		manager.add(refresh);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(zoom_in);
		manager.add(zoom_out);
		manager.add(compact_mode_toggle);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(zoom_in);
		manager.add(zoom_out);
		manager.add(compact_mode_toggle);
		manager.add(refresh);
	}

	private void makeActions() {
		zoom_in = new Action() {
			public void run() {
				zoomManager.zoomIn();
			}
		};
		zoom_in.setText("Zoom in");
		zoom_in.setToolTipText("Zooms in");
		zoom_in.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		zoom_out = new Action() {
			public void run() {
				zoomManager.zoomOut();
			}
		};
		zoom_out.setText("Zoom out");
		zoom_out.setToolTipText("Zooms out");
		zoom_out.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		compact_mode_toggle = new Action() {
			public void run() {
				compact_mode = !compact_mode;
				drawZestDiagram();
				
			}
		};
		compact_mode_toggle.setText(compact_mode? "show details" : "hide details" );
		compact_mode_toggle.setToolTipText(compact_mode? "show Variables and Methods of classes" : "hide Variables and Methods of classes");
		compact_mode_toggle.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
		refresh = new Action() {
			public void run() {
				drawZestDiagram();
			  }
			
			public void run(String[] modifiers) {
				drawZestDiagram(modifiers);
			  }
			};
			refresh.setText("Refresh Visualization");
			refresh.setToolTipText("Refresh/ Redraw Visualization");
			refresh.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		}
		
	

	public static void prepareDiagram(Event e) {

		className = (String) e.getProperty(e.getPropertyNames()[0]);

		parent = ClassReader.readParent();

		children = ClassReader.readChildren();

		interfaces = ClassReader.readInterfaces();

		associations = ClassReader.readAssociations();

		drawZestDiagram();
	}

	private static void clearGraph(Graph graph) {

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

//			if (name.equals(className) || name.equals(parent) || associations.contains(name) || children.contains(name)
//					|| interfaces.contains(name))
//				continue;

			if (!graNode.isDisposed())
				graNode.dispose();
			nodesList.remove(name);
		}

		if (target_class != null)
			target_class.dispose();

	}

	private static void drawZestDiagram() {
		clearGraph(graph);
		
		
		GraphNode target_class = createNode(className);
		
		if (!parent.isEmpty()) {
			GraphNode parent_class = createNode(parent);
			GraphConnection target_parent_connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
					target_class, parent_class);
			target_parent_connection.setLineColor(getColorP());
			target_parent_connection.setText("Parent");
		}

		for (String associate_name : associations) {
			if (parent.equals(associate_name) || interfaces.contains(associate_name)
					|| children.contains(associate_name)) {
				continue;
			}

			GraphNode associate_class = createNode(associate_name);
			GraphConnection target_associate_connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_SOLID,
					target_class, associate_class);
			target_associate_connection.setLineColor(getColorA());
			target_associate_connection.setText("Association");
		}

		for (String child_name : children) {
			GraphNode child_class = createNode(child_name);
			GraphConnection target_child_connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
					child_class, target_class);
			target_child_connection.setLineColor(getColorC());
			target_child_connection.setText("Child");
		}

		for (String interface_name : interfaces) {
			GraphNode interface_comp = createNode(interface_name);
			GraphConnection target_interface_connection = new GraphConnection(graph,
					ZestStyles.CONNECTIONS_DASH_DOT, target_class, interface_comp);
			target_interface_connection.setLineColor(getColorI());
			target_interface_connection.setText("Interface");
		}

		graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
//		org.eclipse.swt.graphics.Rectangle r = graph.getClientArea();
//		graph.setSize(graph, r.width);
		
		graph.applyLayout();
	}
	
	private static void drawZestDiagram(String[] modifiers) {
		clearGraph(graph);
		
		/* Modifiers:
		 * 		-p to show parent
		 * 		-c to show children
		 * 		-a to show association
		 * 		-i to show interfaces
		 */
		
		
		GraphNode target_class = createNode(className);
		
		if (!parent.isEmpty() && Arrays.asList(modifiers).contains("-p")) {
			GraphNode parent_class = createNode(parent);
			GraphConnection target_parent_connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
					target_class, parent_class);
			target_parent_connection.setLineColor(getColorP());
			target_parent_connection.setText("Parent");
		}
		
		if(Arrays.asList(modifiers).contains("-a"))
			for (String associate_name : associations) {
				if (parent.equals(associate_name) || interfaces.contains(associate_name)
						|| children.contains(associate_name)) {
					continue;
				}
	
				GraphNode associate_class = createNode(associate_name);
				GraphConnection target_associate_connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_SOLID,
						target_class, associate_class);
				target_associate_connection.setLineColor(getColorA());
				target_associate_connection.setText("Association");
			}
		if(Arrays.asList(modifiers).contains("-c"))
			for (String child_name : children) {
				GraphNode child_class = createNode(child_name);
				GraphConnection target_child_connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED,
						child_class, target_class);
				target_child_connection.setLineColor(getColorC());
				target_child_connection.setText("Child");
			}
		if(Arrays.asList(modifiers).contains("-i"))
			for (String interface_name : interfaces) {
				GraphNode interface_comp = createNode(interface_name);
				GraphConnection target_interface_connection = new GraphConnection(graph,
						ZestStyles.CONNECTIONS_DASH_DOT, target_class, interface_comp);
				target_interface_connection.setLineColor(getColorI());
				target_interface_connection.setText("Interface");
			}

		graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		
		graph.applyLayout();
	}

	private static GraphNode createNode(String className) {
		if (!nodesList.containsKey(className)) {
			nodesList.put(className, new GraphNode(graph, SWT.NONE, ClassReader.getClassDetails(className, compact_mode)));

			Color c = null;

			switch (ClassReader.getClassType(className)) {
			case "s":
				c = getColor1();
				nodesList.get(className).setForegroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
				break;
			case "c":
				c = getColor2();
				break;
			case "i":
				c = getColor3();
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
	
	public static void updateDiagram(){
		drawZestDiagram();
	}
	
	public static void updateDiagram(String[] modifiers){
		drawZestDiagram(modifiers);
	}
	
	private void showMessage(String message) {
		MessageDialog.openInformation(
			graph.getShell(),
			"Diagram View",
			message);
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public static Color getColor1() {
		return color1;
	}

	public static void setColor1(Color color1) {
		DiagramView.color1 = color1;
	}

	public static Color getColor2() {
		return color2;
	}

	public static void setColor2(Color color2) {
		DiagramView.color2 = color2;
	}

	public static Color getColor3() {
		return color3;
	}

	public static void setColor3(Color color3) {
		DiagramView.color3 = color3;
	}

	public static Color getColorP() {
		return colorP;
	}

	public static void setColorP(Color colorP) {
		DiagramView.colorP = colorP;
	}

	public static Color getColorC() {
		return colorC;
	}

	public static void setColorC(Color colorC) {
		DiagramView.colorC = colorC;
	}

	public static Color getColorA() {
		return colorA;
	}

	public static void setColorA(Color colorA) {
		DiagramView.colorA = colorA;
	}

	public static Color getColorI() {
		return colorI;
	}

	public static void setColorI(Color colorI) {
		DiagramView.colorI = colorI;
	}
	
	public static Action getRefreshAction(){
		return refresh;
	}
	
	public static Action getZoomInAction(){
		return zoom_in;
	}
	
	public static Action getZoomOutAction(){
		return zoom_out;
	}

	public static int getThreshold() {
		return threshold;
	}

	public static void setThreshold(int threshold) {
		DiagramView.threshold = threshold;
	}

	

}
