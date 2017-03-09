package asu.edu.event_separation;

import java.util.ArrayList;
import java.util.HashMap;

import asu.edu.util.TreeNode;

public class Aligner {
	public HashMap<String, String> parseAlignmentFromJamr(ArrayList<String> instanceAlignmentText){
		HashMap<String,String> alignment_WordWindowHM = new HashMap<>();
		
		for(String line : instanceAlignmentText){
			String[] splitLine = line.split("\\s+");
			String[] lastPart = splitLine[splitLine.length-1].split("-");
			if(lastPart.length==2 && isInteger(lastPart[0]) && isInteger(lastPart[1])){
				alignment_WordWindowHM.put(splitLine[2], lastPart[0] + " " + lastPart[1]);
			}
			else{
				alignment_WordWindowHM.put(splitLine[2], "-1 -1");
			}
		}
		
		return alignment_WordWindowHM;
	}
	
	private static boolean isInteger(String s) {
	    return isInteger(s,10);
	}
	
	private static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
}
