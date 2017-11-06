package com.codeviz.codeviz.Parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;

public class JDTAdapter {
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";
	private static IJavaProject currentProject;

	public static void openEditor(String className) {
		IJavaProject javaProject = getCurrentProject();
		try {
			IType element = javaProject.findType(Parser.getQualifiedName(className));
			if (element != null) {
				JavaUI.openInEditor(element);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void setCurrentProject(IJavaProject project) {
		currentProject = project;
	}
	public static IJavaProject getCurrentProject() {
		return currentProject;
	}
	
	
	public static String[] getProjectClasses() {
		LinkedList<String> classes = new LinkedList<>();
		
		IJavaProject javaProject = getCurrentProject();
		
		if(javaProject == null) return new String[0];
		
		IPackageFragment[] packages;
		try {
			packages = javaProject.getPackageFragments();
		
			for (IPackageFragment mypackage : packages) {
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
					for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
						classes.add(unit.getElementName());

			        }
				}
			}
			
		} catch (JavaModelException e) {}
		
		return classes.toArray(new String[classes.size()]);
	
	}
	
}
