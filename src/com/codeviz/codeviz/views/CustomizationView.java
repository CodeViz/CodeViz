package com.codeviz.codeviz.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;

import com.codeviz.codeviz.views.DiagramView;

public class CustomizationView extends ViewPart {
	
	private Color color1 = DiagramView.getColor1();
    private Color color2 = DiagramView.getColor2();
    private Color color3 = DiagramView.getColor3();
    
    private Color colorP = DiagramView.getColorP();
    private Color colorA = DiagramView.getColorA();
    private Color colorC = DiagramView.getColorC();
    private Color colorI = DiagramView.getColorI();

	public CustomizationView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		createContents(parent);
//		Display display = parent.getDisplay();
//	    Shell shell = new Shell(parent);
//	    shell.setText("Color Chooser");
//	    createContents(shell);
//	    shell.pack();
//	    shell.open();
//	    while (!shell.isDisposed()) {
//	      if (!display.readAndDispatch()) {
//	        display.sleep();
//	      }
//	    }
//	    // Dispose the color we created for the Label
//	    if (color != null) {
//	      color.dispose();
//	    }
//	    display.dispose();

	}
	
	private void createContents(Composite parent) {
	    parent.setLayout(new GridLayout(2, false));

	    DiagramView.setColor1(new Color(parent.getDisplay(),DiagramView.getColor1().getRGB()));
	    DiagramView.setColor2(new Color(parent.getDisplay(),DiagramView.getColor2().getRGB()));
	    DiagramView.setColor3(new Color(parent.getDisplay(),DiagramView.getColor3().getRGB()));
	    
	    DiagramView.setColorP(new Color(parent.getDisplay(),DiagramView.getColorP().getRGB()));
	    DiagramView.setColorC(new Color(parent.getDisplay(),DiagramView.getColorC().getRGB()));
	    DiagramView.setColorA(new Color(parent.getDisplay(),DiagramView.getColorA().getRGB()));
	    DiagramView.setColorI(new Color(parent.getDisplay(),DiagramView.getColorI().getRGB()));


	    // Use a label full of spaces to show the color
	    final Label color1Label = new Label(parent, SWT.NONE);
	    color1Label.setText("                              ");
	    color1Label.setBackground(color1);
	    
	    Button button1 = new Button(parent, SWT.PUSH);
	    button1.setText("Java-Specific Class Color");
	    button1.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        // Create the color-change dialog
	        ColorDialog dlg = new ColorDialog(parent.getShell());

	        // Set the selected color in the dialog from
	        // user's selected color
	        dlg.setRGB(color1Label.getBackground().getRGB());

	        // Change the title bar text
	        dlg.setText("Choose a Color for Java-specific classes");

	        // Open the dialog and retrieve the selected color
	        RGB rgb = dlg.open();
	        if (rgb != null) {
	          // Dispose the old color, create the
	          // new one, and set into the label
	          color1.dispose();
	          color1 = new Color(parent.getDisplay(), rgb);
	          DiagramView.setColor1(color1);
	          color1Label.setBackground(color1);
	        }
	      }
	    });
	    
	    final Label color2Label = new Label(parent, SWT.NONE);
	    color2Label.setText("                              ");
	    color2Label.setBackground(color2);
	    
	    Button button2 = new Button(parent, SWT.PUSH);
	    button2.setText("Class Color");
	    button2.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        // Create the color-change dialog
	        ColorDialog dlg = new ColorDialog(parent.getShell());

	        // Set the selected color in the dialog from
	        // user's selected color
	        dlg.setRGB(color2Label.getBackground().getRGB());

	        // Change the title bar text
	        dlg.setText("Choose a Color for your classes");

	        // Open the dialog and retrieve the selected color
	        RGB rgb = dlg.open();
	        if (rgb != null) {
	          // Dispose the old color, create the
	          // new one, and set into the label
	          color2.dispose();
	          color2 = new Color(parent.getDisplay(), rgb);
	          DiagramView.setColor2(color2);
	          color2Label.setBackground(color2);
	        }
	      }
	    });
	    
	    final Label color3Label = new Label(parent, SWT.NONE);
	    color3Label.setText("                              ");
	    color3Label.setBackground(color3);
	    
	    Button button3 = new Button(parent, SWT.PUSH);
	    button3.setText("Interface Color");
	    button3.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        // Create the color-change dialog
	        ColorDialog dlg = new ColorDialog(parent.getShell());

	        // Set the selected color in the dialog from
	        // user's selected color
	        dlg.setRGB(color3Label.getBackground().getRGB());

	        // Change the title bar text
	        dlg.setText("Choose a Color for your interfaces");

	        // Open the dialog and retrieve the selected color
	        RGB rgb = dlg.open();
	        if (rgb != null) {
	          // Dispose the old color, create the
	          // new one, and set into the label
	          color3.dispose();
	          color3 = new Color(parent.getDisplay(), rgb);
	          DiagramView.setColor3(color3);
	          color3Label.setBackground(color3);
	        }
	      }
	    });
	    
	    
	    
	    
	    final Label colorPLabel = new Label(parent, SWT.NONE);
	    colorPLabel.setText("                              ");
	    colorPLabel.setBackground(colorP);
	    
	    Button buttonP = new Button(parent, SWT.PUSH);
	    buttonP.setText("Parent Class Link Color");
	    buttonP.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        // Create the color-change dialog
	        ColorDialog dlg = new ColorDialog(parent.getShell());

	        // Set the selected color in the dialog from
	        // user's selected color
	        dlg.setRGB(colorPLabel.getBackground().getRGB());

	        // Change the title bar text
	        dlg.setText("Choose a Color for your parent class link");

	        // Open the dialog and retrieve the selected color
	        RGB rgb = dlg.open();
	        if (rgb != null) {
	          // Dispose the old color, create the
	          // new one, and set into the label
	          colorP.dispose();
	          colorP = new Color(parent.getDisplay(), rgb);
	          DiagramView.setColorP(colorP);
	          colorPLabel.setBackground(colorP);
	        }
	      }
	    });
	    
	    final Label colorCLabel = new Label(parent, SWT.NONE);
	    colorCLabel.setText("                              ");
	    colorCLabel.setBackground(colorC);
	    
	    Button buttonC = new Button(parent, SWT.PUSH);
	    buttonC.setText("Child Class Link Color");
	    buttonC.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        // Create the color-change dialog
	        ColorDialog dlg = new ColorDialog(parent.getShell());

	        // Set the selected color in the dialog from
	        // user's selected color
	        dlg.setRGB(colorCLabel.getBackground().getRGB());

	        // Change the title bar text
	        dlg.setText("Choose a Color for your child class link");

	        // Open the dialog and retrieve the selected color
	        RGB rgb = dlg.open();
	        if (rgb != null) {
	          // Dispose the old color, create the
	          // new one, and set into the label
	          colorC.dispose();
	          colorC = new Color(parent.getDisplay(), rgb);
	          DiagramView.setColorC(colorC);
	          colorCLabel.setBackground(colorC);
	        }
	      }
	    });
	    
	    final Label colorALabel = new Label(parent, SWT.NONE);
	    colorALabel.setText("                              ");
	    colorALabel.setBackground(colorA);
	    
	    Button buttonA = new Button(parent, SWT.PUSH);
	    buttonA.setText("Associate Class Link Color");
	    buttonA.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        // Create the color-change dialog
	        ColorDialog dlg = new ColorDialog(parent.getShell());

	        // Set the selected color in the dialog from
	        // user's selected color
	        dlg.setRGB(colorALabel.getBackground().getRGB());

	        // Change the title bar text
	        dlg.setText("Choose a Color for your associate class link");

	        // Open the dialog and retrieve the selected color
	        RGB rgb = dlg.open();
	        if (rgb != null) {
	          // Dispose the old color, create the
	          // new one, and set into the label
	          colorA.dispose();
	          colorA = new Color(parent.getDisplay(), rgb);
	          DiagramView.setColorA(colorA);
	          colorALabel.setBackground(colorA);
	        }
	      }
	    });
	    
	    final Label colorILabel = new Label(parent, SWT.NONE);
	    colorILabel.setText("                              ");
	    colorILabel.setBackground(colorI);
	    
	    Button buttonI = new Button(parent, SWT.PUSH);
	    buttonI.setText("Interface Link Color");
	    buttonI.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        // Create the color-change dialog
	        ColorDialog dlg = new ColorDialog(parent.getShell());

	        // Set the selected color in the dialog from
	        // user's selected color
	        dlg.setRGB(colorILabel.getBackground().getRGB());

	        // Change the title bar text
	        dlg.setText("Choose a Color for your interface link");

	        // Open the dialog and retrieve the selected color
	        RGB rgb = dlg.open();
	        if (rgb != null) {
	          // Dispose the old color, create the
	          // new one, and set into the label
	          colorI.dispose();
	          colorI = new Color(parent.getDisplay(), rgb);
	          DiagramView.setColorI(colorI);
	          colorILabel.setBackground(colorI);
	        }
	      }
	    });
	  }

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
