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
	
	public static String readParent(){
		return Parser.getParent();
	}
	
	public static LinkedList<String> readChildren(){
		return Parser.getChildren();
	}	
	
	public static LinkedList<String> readInterfaces(){
		return Parser.getInterfaces();
	}
	
	public static LinkedList<String> readAssociations(){
		return Parser.getAssociations();
	}

}
