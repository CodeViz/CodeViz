package com.codeviz.codeviz.queryParser;

import java.util.ArrayList;

import com.codeviz.codeviz.Parser.JDTAdapter;
import com.codeviz.codeviz.views.VisualizerView;

public class QueryParser {

	public static String[] getClassNames() {
		System.out.println("called: getClassNames()");
		return JDTAdapter.getProjectClasses();
	}
	
	public static String[] getFunctions(){
		return new String[] {"Find", "Count", "Focus"};
	}
	
	public static String[] getProposals(){
		String[] classes = getClassNames();
		String[] functions = getFunctions();
		ArrayList<String> proposals = new ArrayList<String>();
		for(String function: functions){
			for(String class_name: classes){
				proposals.add(function+' '+class_name);
			}
		}
		return  proposals.toArray(new String[]{});
	}
	
	public static String parseAction(String query){
		return "Command Detected: \n"+query;
	}
}
