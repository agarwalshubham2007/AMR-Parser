package asu.edu.event_separation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import asu.edu.util.TreeNode;

public class SpanGraph extends Aligner{
	private static String sentence;
	private HashMap<String, String> alignment_WordWindowHM;
	private HashSet<TreeNode> visitedNodesSet;
	
	public void makeSpanGraph(TreeNode spanGraphRoot, ArrayList<String> instanceAlignmentText, String sent){
		SpanGraph.sentence = sent;
		this.alignment_WordWindowHM = this.parseAlignmentFromJamr(instanceAlignmentText);
		this.visitedNodesSet = new HashSet<>();
		traverseParseGraph(spanGraphRoot, "0");
		System.out.println("DONE!!!!!!!!!!!!");
	}
	
	public void traverseParseGraph(TreeNode spanGraphCurr,  String currPosition){
		if(visitedNodesSet.contains(spanGraphCurr) || spanGraphCurr == null)
			return;
		System.out.println("currPosition: " + currPosition + " value : " + alignment_WordWindowHM.get(currPosition));
		try{
			String[] split = alignment_WordWindowHM.get(currPosition).split(" ");
			spanGraphCurr.setStartEndSentIndex(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
			visitedNodesSet.add(spanGraphCurr);
			
			for(int i=0, ctr=0;i<spanGraphCurr.childEdge.size();i++, ctr++){
				if(!visitedNodesSet.contains(spanGraphCurr.childNode.get(i)))
					traverseParseGraph(spanGraphCurr.childNode.get(i), currPosition+"."+Integer.toString(ctr));
				else
					ctr--;
			}
		}catch(NullPointerException e){
			System.out.println("EXCEPTION IN sentence : " + SpanGraph.sentence);
		}
		
	}
}
