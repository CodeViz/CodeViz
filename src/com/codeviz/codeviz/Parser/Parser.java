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
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

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

	public static LinkedList<String> readClasses(String folder) {
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

	static void addTree(Path directory, Map<String, ParsedItem> all) throws IOException {
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(directory)) {
			for (Path child : ds) {
				if (child.toString().endsWith(".java")) {
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

		if (!classes.containsKey(className))
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
		Parser.readClasses(
				"C:\\Users\\alaa13212\\Google Drive\\KFUPM\\Senior Project 162\\Test cases\\Medium\\Space Shooter game\\src");
		// Parser.readClasses(
		//		"C:\\Users\\alaa13212\\Google Drive\\KFUPM\\Senior Project 162\\Test cases\\Medium\\clock_");
		// Parser.readClasses("src/test1");
		// Parser.readClasses("src/test2");

		LinkedList<String> myClasses = new LinkedList<>(Parser.getClasses().keySet());

		long time = System.currentTimeMillis();

		for (String name : myClasses) {
			System.out.println(name);
			Parser.parse(name);

			if (!Parser.getParent().isEmpty())
				System.out.println("Parent: " + Parser.getParent());

			if (!Parser.getInterfaces().isEmpty()) {
				System.out.println("Interfaces: ");
				for (String i : Parser.getInterfaces()) {
					System.out.println("   - " + i);
				}
			}

			if (!Parser.getChildren().isEmpty()) {
				System.out.println("Children: ");
				for (String c : Parser.getChildren()) {
					System.out.println("   - " + c);
				}
			}
			if (!Parser.getAssociations().isEmpty()) {
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
		if (!classes.containsKey(className))
			throw new IllegalArgumentException("Passed className " + className + " not found in package.");

		String prefix = "";
		if (classes.get(className).cu.getPackageDeclaration().isPresent())
			prefix = classes.get(className).cu.getPackageDeclaration().get().getNameAsString() + ".";

		return prefix + className;
	}

	public static LinkedList<String> getAttributes() {

		return attributes;
	}

	public static LinkedList<String> getMethods() {

		return methods;
	}

	public static String getClassDetails(String className) {

		if (!classes.containsKey(className))
			return className;

		ParsedItem parsedItem = classes.get(className);
		LinkedList<String> cAttributes = new LinkedList<>();
		LinkedList<String> cMethods = new LinkedList<>();

		(new VoidVisitorAdapter<Object>() { // Attributes
			@Override
			public void visit(FieldDeclaration n, Object arg) {
				super.visit(n, arg);
				if (n.isFinal() && n.isStatic()) { // ignore final static
													// attributes
					return;
				}
				n.getElementType().toString();
				
				for (VariableDeclarator vd : n.getVariables()) {
					
					String name = n.getElementType().toString() + " " + vd.getNameAsString();
					
					if(n.isPublic())			name  = "+" + name;
					else if(n.isPrivate())		name  = "-" + name;
					else if(n.isProtected())	name  = "#" + name;
					else						name  = "~" + name;
					
					if (!cAttributes.contains(name)) {
						cAttributes.add(name);
					}
				}

			}

		}).visit(parsedItem.cu, parsedItem);
		
		(new VoidVisitorAdapter<Object>() { // Methods
			@Override
			public void visit(com.github.javaparser.ast.body.MethodDeclaration n, Object arg) {
				super.visit(n, arg);
				String attr = n.getNameAsString().replaceAll("^(set|get|is)", "");
				if(!attr.isEmpty()){
					attr = Character.toLowerCase(attr.charAt(0)) + (attr.length() > 1 ? attr.substring(1) : "");
					
					
					for (String attribute : cAttributes) {
						if(attribute.contains(attr))
							return;	// skip setters and getters
					}
				}
				
				String name = n.getDeclarationAsString(false, false);
				
				if(n.isPublic())	name  = "+" + name;
				if(n.isPrivate())	name  = "-" + name;
				if(n.isProtected())	name  = "#" + name;
				if(n.isDefault())	name  = "~" + name;
				
				
				
				if (!cMethods.contains(name)) {
					cMethods.add(name);
				}

			}
		}).visit(parsedItem.cu, parsedItem);
		
		

		String details = "", att = "", meth = "", line = "\n----------------------";
		for (String attribute : cAttributes) {
			att = att.concat("\n" + attribute);
		}
		for (String method : cMethods) {	
			meth = meth.concat("\n" + method);
		}

		details = className + line + att + line + meth;

		return details;
	}

}
