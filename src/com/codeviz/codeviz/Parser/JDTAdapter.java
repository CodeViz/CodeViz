package com.codeviz.codeviz.Parser;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;

public class JDTAdapter {
	private static final String JDT_NATURE = "org.eclipse.jdt.core.javanature";

	public static void openEditor(String className) {
		 IWorkspace workspace = ResourcesPlugin.getWorkspace();
         IWorkspaceRoot root = workspace.getRoot();
         IProject[] projects = root.getProjects();
         for (IProject project : projects) {
             try {
            	 if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
					IJavaProject javaProject = JavaCore.create(project);
					IType element = javaProject.findType(Parser.getQualifiedName(className));
					
					if(element != null){
						JavaUI.openInEditor(element);
						break;
					}
				}
             } catch (CoreException e) {}
         }
	}
}
