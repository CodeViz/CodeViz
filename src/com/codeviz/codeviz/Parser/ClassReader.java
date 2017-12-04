package com.codeviz.codeviz.Parser;

import java.util.Arrays;
import java.util.LinkedList;

public class ClassReader {

	public static void parseClass(String className) {
		JDTParser.parse(className);
	}


	public static String readParent() {
		return JDTParser.getParent();
	}

	public static LinkedList<String> readChildren() {
		return JDTParser.getChildren();
	}

	public static LinkedList<String> readInterfaces() {
		return JDTParser.getInterfaces();
	}

	public static LinkedList<String> readAssociations() {
		return JDTParser.getAssociations();
	}

	public static LinkedList<String> readAttributes() {
		return JDTParser.getAttributes();
	}

	public static LinkedList<String> readMethods() {
		return JDTParser.getMethods();
	}

	public static String getClassDetails(String className, boolean compact) {
		return JDTParser.getClassDetails(className, compact);
	}
	
	public static String getClassType(String className) {
		return JDTParser.getClassType(className);
	}
	
	
	public static <T> LinkedList<T> asLL(T[] array) {
		return new LinkedList<T>(Arrays.asList(array));
	}
}
