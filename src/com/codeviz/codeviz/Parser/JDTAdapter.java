package com.codeviz.codeviz.Parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class JDTAdapter {
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";
	private static IJavaProject currentProject;
	private static Map<String, ICompilationUnit> projectClasses;
	
	
	public static void openEditor(String className) {
		IJavaProject javaProject = getCurrentProject();
		try {
			IType element;
			if(className.contains("."))
				element = javaProject.findType(className);
			else 
				element = projectClasses.get(className).getType(className);
			
			if (element != null) {
				JavaUI.openInEditor(element);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void setCurrentProject(IJavaProject project) {
		currentProject = project;
		findClasses();
	}
	
	private static void findClasses() {
		projectClasses = new HashMap<>();
		IPackageFragment[] packages;
		try {
			packages = currentProject.getPackageFragments();
		
			for (IPackageFragment mypackage : packages) {
				if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
					for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
						projectClasses.put(unit.getTypes()[0].getElementName(), unit);
			        }
				}
			}
			
		} catch (JavaModelException e) {}
	}
	
	
	public static ICompilationUnit getClassByName(String className) {
		return projectClasses.get(className);
	}

	public static IJavaProject getCurrentProject() {
		return currentProject;
	}
	
	
	public static String[] getProjectClasses() {
		if(currentProject == null) return new String[0];
		
		return projectClasses.keySet().toArray(new String[projectClasses.size()]);
	
	}


	public static String getClassType(String className) {
		if (!projectClasses.containsKey(className))
			return "s";
		
		
		try {
			if(projectClasses.get(className).getType(className).isInterface())
				return "i";
		} catch (JavaModelException e) {}
		
		return "c";
	}


	public static String getClassDetails(String className, boolean compact) throws JavaModelException{
		if (!projectClasses.containsKey(className))
			return className;
		
		
		IType parsedItem = projectClasses.get(className).getType(className);
		LinkedList<String> cAttributes = new LinkedList<>();
		LinkedList<String> cMethods = new LinkedList<>();
		
		for (IField	field : parsedItem.getFields()) {
			int state = field.getFlags();
			if ( Flags.isFinal(state) && Flags.isStatic(state) ) { // ignore final static attributes
				continue;
			}
			
			String name = field.getElementName();
			
			if(Flags.isPublic(state))			name  = "+" + name;
			else if(Flags.isPrivate(state))		name  = "-" + name;
			else if(Flags.isProtected(state))	name  = "#" + name;
			else								name  = "~" + name;
			
			if (!cAttributes.contains(name)) {
				cAttributes.add(name);
			}
			
		}
		
		
		out: for (IMethod method : parsedItem.getMethods()) {
			
			String attr = method.getElementName().replaceAll("^(set|get|is)", "");
			if(!attr.isEmpty()){
				attr = Character.toLowerCase(attr.charAt(0)) + (attr.length() > 1 ? attr.substring(1) : "");
				
				for (String attribute : cAttributes) {
					if(attribute.contains(attr))
						continue out;	// skip setters and getters
				}
			}
			
			String name = method.getElementName() + "(" + String.join(", ", method.getParameterNames()) +")";
			
			int state = method.getFlags();
			
			if(Flags.isPublic(state))			name  = "+" + name;
			else if(Flags.isPrivate(state))		name  = "-" + name;
			else if(Flags.isProtected(state))	name  = "#" + name;
			else								name  = "~" + name;
			
			if (!cMethods.contains(name)) {
				cMethods.add(name);
			}
			
		}
		
		
		String details, att, meth, line = "\n──────────────────────\n";
		
		att = String.join("\n", cAttributes);
		meth = String.join("\n", cMethods);
		
		
		
		if(compact)
			details = className;
		else
			details = className + line + att + line + meth;

		return details;
	}
	
	
	
}
