package com.codeviz.codeviz.queryParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

import com.codeviz.codeviz.Parser.JDTAdapter;
import com.codeviz.codeviz.views.DiagramView;
import com.codeviz.codeviz.views.VisualizerView;

public class QueryParser {
	private static String[] atomic_functions = {"zi","zo"};
	private static LinkedList<String> modifiers = new LinkedList<String>();
	public static String[] getClassNames() {
		return JDTAdapter.getProjectClasses();
	}
	
	public static String[] getFunctions(){
		return new String[] {"find", "count", "focus", "draw", "zi", "zo", "link", "help"};
	}
	
	public static String[] getProposals(){
		String[] classes = getClassNames();
		String[] functions = getFunctions();
		ArrayList<String> proposals = new ArrayList<String>();
		
		if(classes.length == 0) classes = new String[]{""};		
		
		for(String function: functions){
//			if(!Arrays.asList(atomic_functions).contains(function))
				for(String class_name: classes){
					proposals.add(function+' '+class_name);
				}
		}
		return  proposals.toArray(new String[]{});
	}
	
	public static String parseAction(String query){
		//Assuming Queries follow this grammar: function [parameter1,[parameter2,...]] [modifier, modifier, ...]
		
		StringTokenizer query_tokens = new StringTokenizer(query);
		String function = "";
		if(!query_tokens.hasMoreTokens())
			function = "help";
		else
			function = query_tokens.nextToken();
		
		if(!Arrays.asList(getFunctions()).contains(function)){
			return "Query function \""+function+"\" unrecognized: "+query;
		}
		
		
		if(function.equalsIgnoreCase("Draw")){
			String class_name = query_tokens.nextToken();
			if(!Arrays.asList(getClassNames()).contains(class_name))
				return "Query class \""+class_name+"\" unrecognized: "+query;
			
			setModifiers(new LinkedList<String>());
			while(query_tokens.hasMoreTokens())
				getModifiers().add(query_tokens.nextToken());
			
			
			JDTAdapter.openEditor(class_name);
			
			return "Successful Query: \""+query+"\"";
		}
		
		
		if(function.equalsIgnoreCase("zi")){
			DiagramView.getZoomInAction().run();
			return "Successful Query: \""+query+"\"";
		}
		
		
		if(function.equalsIgnoreCase("zo")){
			DiagramView.getZoomOutAction().run();
			return "Successful Query: \""+query+"\"";
		}
		
		
		if(function.equalsIgnoreCase("help")){
			String help = "Queries:\ndraw CLASSNAME\nlink ClassA ClassB\nzi\nzo\n";
			VisualizerView.getLabel().setText(help);
			return "Successful Query: \""+query+"\"";
		}
		
		
		if(function.equalsIgnoreCase("Link")){
			String class_a = query_tokens.nextToken();
			String class_b = query_tokens.nextToken();
			int threshold = DiagramView.getThreshold();
			if(query_tokens.hasMoreTokens())
				threshold = Integer.parseInt(query_tokens.nextToken());
			LinkedList<LinkedList<String>> paths = new LinkedList<LinkedList<String>>();
			
			
			for(int j = 0; j < 3; j++){
				paths.add(new LinkedList<String>());
				paths.get(j).add(class_a);
				String link = ""+j;
				paths.get(j).add("EnemyShipShooter");
				for( int i = j*10; i < j*10 + 5; i++)
					paths.get(j).add("C:"+i+" "+link);
				paths.get(j).add(class_b);
			}
			
			DiagramView.drawLinkDiagram(paths);
			//TODO call parser link method here
			return "Successful Query: \""+query+"\"";
		}
		
		return "Command Detected: \n"+query;
	}

	public static LinkedList<String> getModifiers() {
		return modifiers;
	}

	public static void setModifiers(LinkedList<String> modifiers) {
		QueryParser.modifiers = modifiers;
	}
}
