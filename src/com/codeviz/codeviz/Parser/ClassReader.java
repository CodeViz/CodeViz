package com.codeviz.codeviz.Parser;

import java.util.LinkedList;

public class ClassReader {
	
	
	public static LinkedList<String> readClasses(String folder){
		return new LinkedList<>(Parser.readClasses(folder));
	}
	
	public static void parseClass(String className){
		Parser.parse(className);
	}
	
	
	public static LinkedList<String> onChanged(String folder){
		return new LinkedList<>(Parser.readClasses(folder));
	}
	
	public static String readParent(String class_name){
		return Parser.getParent();
	}
	
	public static LinkedList<String> readChildren(String class_name){
		return Parser.getChildren();
	}	
	
	public static LinkedList<String> readInterfaces(String class_name){
		return Parser.getInterfaces();
	}
	
	public static LinkedList<String> readAssociations(String class_name){
		return Parser.getAssociations();
	}

}
