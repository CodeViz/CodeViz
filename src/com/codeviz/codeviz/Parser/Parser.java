package com.codeviz.codeviz.Parser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.github.javaparser.JavaParser;

public class Parser {

	private static String parent;

	private static LinkedList<String> interfaces = new LinkedList<>();
	private static LinkedList<String> children = new LinkedList<>();
	private static LinkedList<String> associations = new LinkedList<>();
	private static LinkedList<String> attributes = new LinkedList<>();
	private static LinkedList<String> methods = new LinkedList<>();
	
	private static String folder;
	
	private static Map<String, ParsedItem> classes = new HashMap<>();
	
	static ClassesOrInterfacesVisitor visitor;
	
	
	private Parser() {
		
	}
	
	public static LinkedList<String> readClasses(String folder){
		classes.clear();
		Parser.folder = folder;
		visitor = new ClassesOrInterfacesVisitor();
		
		Path path = Paths.get(folder);
		try {
			addTree(path, classes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new LinkedList<String>(classes.keySet());
	}
	
	static void addTree(Path directory, Map<String, ParsedItem> all)
	        throws IOException {
	    try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
	        for (Path child : ds) {
	        	if(child.toString().endsWith(".java")){
	        		ParsedItem item = new ParsedItem(child, JavaParser.parse(child));
	        		all.put(child.getFileName().toString().replace(".java", ""), item);
	        	} else if (Files.isDirectory(child)) {
	                addTree(child, all);
	            }
	        }
	    }
	}
	
	public static void parse(String className) {
		// reset
		reset();
		
		if(! classes.containsKey(className))
			throw new IllegalArgumentException("Passed className not found in package.");
		
		
		ParsedItem parsedItem = classes.get(className);
		
		visitor.visit(parsedItem.cu, parsedItem);
	}

	private static void reset() {
		parent = "";
		interfaces.clear();
		children.clear();
		associations.clear();
	}

	public static String getParent() {
		return parent;
	}

	public static LinkedList<String> getInterfaces() {
		return interfaces;
	}

	public static LinkedList<String> getChildren() {
		return children;
	}

	public static LinkedList<String> getAssociations() {
		return associations;
	}
	
	

	public static String getFolder() {
		return folder;
	}

	public static Map<String, ParsedItem> getClasses() {
		return classes;
	}


	public static void setFolder(String folder) {
		Parser.folder = folder;
	}

	protected static void setParent(String parent) {
		Parser.parent = parent;
	}

	protected static void setInterfaces(LinkedList<String> interfaces) {
		Parser.interfaces = interfaces;
	}

	protected static void setChildren(LinkedList<String> children) {
		Parser.children = children;
	}

	protected static void setAssociations(LinkedList<String> associations) {
		Parser.associations = associations;
	}

	public static void main(String[] args) {
		Parser.readClasses("C:\\Users\\alaa13212\\Google Drive\\KFUPM\\Senior Project 162\\Test cases\\Medium\\Space Shooter game\\src");
//		Parser.readClasses("C:\\Users\\alaa13212\\Google Drive\\KFUPM\\Senior Project 162\\Test cases\\Medium\\clock_");
//		Parser.readClasses("src/test1");
//		Parser.readClasses("src/test2");
		
		LinkedList<String> myClasses = new LinkedList<>(Parser.getClasses().keySet());
		
		long time = System.currentTimeMillis();
		
		for (String name : myClasses) {
			System.out.println(name);
			Parser.parse(name);
			
			if(! Parser.getParent().isEmpty())
				System.out.println("Parent: " + Parser.getParent());
			
			
			if(! Parser.getInterfaces().isEmpty()) {
				System.out.println("Interfaces: ");
				for (String i : Parser.getInterfaces()) {
					System.out.println("   - " + i);
				}
			}
			
			if(! Parser.getChildren().isEmpty()) {
				System.out.println("Children: ");
				for (String c : Parser.getChildren()) {
					System.out.println("   - " + c);
				}
			}
			if(! Parser.getAssociations().isEmpty()) {
				System.out.println("Associations: ");
				for (String a : Parser.getAssociations()) {
					System.out.println("   - " + a);
				}
			}
			
			System.out.println("========================================================");
		}
		
		System.out.println("Time: " + (System.currentTimeMillis() - time));
		
	}

	public static String getQualifiedName(String className) {
		if(! classes.containsKey(className))
			throw new IllegalArgumentException("Passed className "+className+" not found in package.");
		
		String prefix = "";
		if(classes.get(className).cu.getPackageDeclaration().isPresent())
			prefix = classes.get(className).cu.getPackageDeclaration().get().getNameAsString() + ".";
		
		return prefix + className;
	}

	public static LinkedList<String> getAttributes() {
		
		return attributes;
	}

	public static LinkedList<String> getMethods() {
		
		return methods;
	}

}
