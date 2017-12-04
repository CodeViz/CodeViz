package com.codeviz.codeviz.Parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class JDTParser {
	
	private static IType currentClass;
	
	public static void parse(String className){
		currentClass = JDTAdapter.getClassByName(className).getType(className);
		
	}
	
	public static String getParent() {
		try {
			String parent = currentClass.getSuperclassName();
			return parent == null? "" : parent;
		} catch (JavaModelException e) {}
		return "";
	}
	
	public static LinkedList<String> getInterfaces() {
		try {
			return ClassReader.asLL(currentClass.getSuperInterfaceNames());
		} catch (JavaModelException e) {}
		return new LinkedList<String>();
	}
	
	
	public static LinkedList<String> getChildren() {
		try {
			ITypeHierarchy subHierarchy = currentClass.newTypeHierarchy(null);
			IType[] children = subHierarchy.getAllSubtypes(currentClass);
			LinkedList<String> names = new LinkedList<>();
			for (IType child : children) {
				names.add(child.getElementName());
			}
			
			return names;
		} catch (JavaModelException e) {}
		
		return null;
	}
	
	public static LinkedList<String> getAssociations() {
		
		try {
			LinkedList<String> names = new LinkedList<>();
			
			
			for(IMethod method : currentClass.getMethods()){
				Set<IType> associations = getCallersOf(method);
				for (IType association : associations) {
					names.add(association.getElementName());
				}
			}
			
			Set<String> hs = new HashSet<>();
			hs.addAll(names);
			names.clear();
			names.addAll(hs);
			
			
			return names;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		
		return new LinkedList<>();
	}
	
	
	public static LinkedList<String> getMethods() {
		try {
			IMethod[] methods = currentClass.getMethods();
			LinkedList<String> names = new LinkedList<>();
			for (IMethod method : methods) {
				names.add(method.getElementName());
			}
			
			return names;
		} catch (JavaModelException e) {}
		
		return new LinkedList<>();
	}

	public static LinkedList<String> getAttributes() {
		try {
			IField[] fields = currentClass.getFields();
			LinkedList<String> names = new LinkedList<>();
			for (IField field : fields) {
				names.add(field.getElementName());
			}
			
			return names;
		} catch (JavaModelException e) {}
		
		return new LinkedList<>();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static HashSet<IType> getCallersOf(IMethod m) {

		CallHierarchy callHierarchy = CallHierarchy.getDefault();

		IMember[] members = { m };
		
		MethodWrapper[] methodWrappers = callHierarchy.getCallerRoots(members);
		HashSet<IType> callers = new HashSet<IType>();
		for (MethodWrapper mw : methodWrappers) {
			MethodWrapper[] mw2 = mw.getCalls(new NullProgressMonitor());
			HashSet<IType> temp = getIMethods(mw2);
			callers.addAll(temp);
		}
		
		methodWrappers = callHierarchy.getCallerRoots(members);
		for (MethodWrapper mw : methodWrappers) {
			MethodWrapper[] mw2 = mw.getCalls(new NullProgressMonitor());
			HashSet<IType> temp = getIMethods(mw2);
			callers.addAll(temp);
		}
		
		return callers;
	}

	static HashSet<IType> getIMethods(MethodWrapper[] methodWrappers) {
		HashSet<IType> c = new HashSet<IType>();
		for (MethodWrapper m : methodWrappers) {
			IType im = getIMethodFromMethodWrapper(m);
			if (im != null) {
				c.add(im);
			}
		}
		return c;
	}

	static IType getIMethodFromMethodWrapper(MethodWrapper m) {
		try {
			IMember im = m.getMember();
			if (im.getElementType() == IJavaElement.METHOD) {
				return m.getMember().getDeclaringType();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String getClassDetails(String className, boolean compact) {
		try{
			return JDTAdapter.getClassDetails(className, compact);
		} catch (JavaModelException e) {
			return className;
		}
	}
	
	public static String getClassType(String className) {
		return JDTAdapter.getClassType(className);
	}
	
	
	
}
