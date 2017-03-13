package asu.edu.event_separation;

import asu.edu.util.Constants;
import asu.edu.util.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;

import asu.edu.app.event_separation_app;

public class ParseTree extends event_separation_app{
	
	// this hashmap stores alias as key and corresponding node in amr as value. I will use this to resolve co-references
	// in the amr graph
	public HashMap<String, TreeNode> alias_TreeNodeHM;
	
	public void makeParseTreeWrapper(TreeNode root, String amr, HashMap<String, TreeNode> alias_TreeNodeHM){
		this.alias_TreeNodeHM = alias_TreeNodeHM;
		this.makeParseTree(root, amr);
	}
	
	public void makeParseTree(TreeNode root, String amr) {
		while(rightPos<amr.length()){
			if(amr.charAt(leftPos) == '('){
				String temp;
				while(true){
					if(amr.charAt(rightPos) != '(' && amr.charAt(rightPos) != ')')
						rightPos++;
					else 
						break;
				}
				
				temp=amr.substring(leftPos+1, rightPos);
				
				if(amr.charAt(rightPos)=='('){
					String parts[] = temp.split(":");
					if(parts.length==2){
						//extract root node and fill the object fields
						root.word = parts[0];
						fillNodeObjectFields(root, root.word);
						
						root.childEdge.add(parts[1]);
						leftPos=rightPos;
						rightPos++;
						TreeNode node = new TreeNode();
						makeParseTree(node, amr);
						root.childNode.add(node);
					}
					else if(parts.length>=3){
						root.word = parts[0];
						fillNodeObjectFields(root, root.word);
						for(int i=1;i<=parts.length-2;i++){
							String subEdges[] = parts[i].split(" ");
							root.childEdge.add(subEdges[0]);
							root.childNode.add(makeCorefNode(subEdges[0], subEdges[1]));
						}
						root.childEdge.add(parts[parts.length-1]);
						leftPos=rightPos;
						rightPos++;
						TreeNode node = new TreeNode();
						makeParseTree(node, amr);
						root.childNode.add(node);
					}
				}
				else{
					String parts[] = temp.split(":");
					if(parts.length==1){
						root.word = amr.substring(leftPos+1, rightPos);
						fillNodeObjectFields(root, root.word);
						rightPos++;
						leftPos=rightPos;
						return;
					}
					else if(parts.length>=2){
						root.word = parts[0];
						fillNodeObjectFields(root, root.word);
						for(int i=1;i<=parts.length-1;i++){
							String subEdges[] = parts[i].split(" ");
							root.childEdge.add(subEdges[0]);
							root.childNode.add(makeCorefNode(subEdges[0], subEdges[1]));
						}
						rightPos++;
						leftPos=rightPos;
						return;
					}
				}
			}
			else if(amr.charAt(leftPos) == ')'){
				rightPos++;
				leftPos=rightPos;
				return;
			}
			else{
				String temp;
				while(amr.charAt(rightPos)!='(' && amr.charAt(rightPos)!=')')
					rightPos++;
				temp = amr.substring(leftPos, rightPos);
				if(amr.charAt(rightPos)=='('){
					String parts[] = temp.split(":");
					if(parts.length==2){
						root.childEdge.add(parts[1]);
						leftPos=rightPos;
						rightPos++;
						TreeNode node = new TreeNode();
						makeParseTree(node, amr);
						root.childNode.add(node);
					}
					else if(parts.length>=3){
						for(int i=1;i<=parts.length-2;i++){
							String subEdges[] = parts[i].split(" ");
							root.childEdge.add(subEdges[0]);
							root.childNode.add(makeCorefNode(subEdges[0], subEdges[1]));
						}
						root.childEdge.add(parts[parts.length-1]);
						leftPos=rightPos;
						rightPos++;
						TreeNode node = new TreeNode();
						makeParseTree(node, amr);
						root.childNode.add(node);
					}
				}
				else{
					String parts[] = temp.split(":");
					for(int i=1;i<parts.length;i++){
						String subEdges[] = parts[i].split(" ");
						root.childEdge.add(subEdges[0]);
						root.childNode.add(makeCorefNode(subEdges[0], subEdges[1]));
					}
					rightPos++;
					leftPos = rightPos;
					return;
				}
			}
		}
	}
	
	// this method checks from the list of relations(corefRelationConstants) that cannot contribute to co-reference in amr.
	private TreeNode makeCorefNode(String relation, String alias) {
		if(!Constants.corefRelationConstants.contains(relation) && !alias.startsWith("\"")){
			TreeNode node = new TreeNode();
			node.alias = alias;
			node.frameNum = -1;
			node.childrenCount = 0;
			node.childEdge = new ArrayList<String>();
			node.childNode = new ArrayList<TreeNode>();
			return node;
		}
		else{
			TreeNode node = new TreeNode();
			node.word = alias;
			node.frameNum = -1;
			node.childrenCount = 0;
			node.childEdge = new ArrayList<String>();
			node.childNode = new ArrayList<TreeNode>();
			return node;
		}
	}



	private void fillNodeObjectFields(TreeNode root, String word) {
		String parts[] = word.split("[/-]");
		if(parts.length==1){
			root.alias = parts[0].trim();
			root.word = parts[0].trim();
			populateAliasTreeNodeHM(parts[0].trim(), root); // populating the alias_TreeNodeHM for co-reference resolution
		}
		else if(parts.length==2){
			root.alias = parts[0].trim();
			root.word = parts[1].trim();
			populateAliasTreeNodeHM(parts[0].trim(), root); // populating the alias_TreeNodeHM for co-reference resolution
		}
		else if(parts.length >=3){
			root.alias = parts[0].trim();
			root.word = "";
			for(int i=1;i<parts.length-1;i++)
				root.word += parts[i].trim()+" ";
			
			String frameStr = parts[parts.length-1].trim();
			if(isNumber(parts[parts.length-1].trim().charAt(0)))
				root.frameNum = Integer.parseInt(frameStr);
			populateAliasTreeNodeHM(parts[0].trim(), root); // populating the alias_TreeNodeHM for co-reference resolution
		}
	}
	
	private void populateAliasTreeNodeHM(String alias, TreeNode root){
		if(!this.alias_TreeNodeHM.containsKey(alias))
			alias_TreeNodeHM.put(alias, root);
	}

	public static boolean isNumber(char charAt) {
		switch(charAt){
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9': return true;
		default: return false;
		}
	}
	
	public static void resolveArgOf(TreeNode root) {
		// if this is leaf node RETURN
		if(root.childEdge.size()==0) return;
		
		
		// check if this node has child ARG-Of
		for(int i=0;i<root.childEdge.size();i++){
			if(isEdgeArgOf(root, i)){	
				System.out.println("HOORAH " + root.childNode.get(i).word + " is ARG-OF node");
				//clone this node and make it child of ARG-of
				TreeNode cloneRoot = new TreeNode();
				cloneNodeForArgOf(root, cloneRoot);
				root.childNode.get(i).childNode.add(cloneRoot);
				root.childNode.get(i).childEdge.add(root.childEdge.get(i).substring(0, 4)+" ");
			}
			resolveArgOf(root.childNode.get(i));
		}
	}

	public static void cloneNodeForArgOf(TreeNode root, TreeNode cloneRoot) {
		if(root.childEdge.size()>0){
			cloneRoot.word = root.word;
			cloneRoot.alias = root.alias;
			cloneRoot.frameNum = root.frameNum;
			cloneRoot.childrenCount = root.childrenCount;
			for(int j=0;j<root.childEdge.size();j++){
				if(!isEdgeArgOf(root, j)){
					cloneRoot.childEdge.add(root.childEdge.get(j));
					cloneRoot.childNode.add(root.childNode.get(j));
//					cloneNodeForArgOf(root.childNode.get(j), cloneRoot.childNode.get(cloneRoot.childNode.size()-1));
				}
			}
		}
		else if(root.childEdge.size()==0){
			cloneRoot.word = root.word;
			cloneRoot.alias = root.alias;
			cloneRoot.frameNum = root.frameNum;
			cloneRoot.childrenCount = root.childrenCount;
		}
	}

	public static boolean isEdgeArgOf(TreeNode root, int i) {
		if(root.childEdge.get(i).substring(0, 3).equals("ARG") && root.childEdge.get(i).substring(root.childEdge.get(i).length()-3, root.childEdge.get(i).length()-1).equals("of")){
			System.out.println("ARGOF EDGE YES!!");
			return true;
		}
		else { 
			System.out.println("ARGOF EDGE NO!! -- " + root.childEdge.get(i) + " " + root.childEdge.get(i).length());
			return false;
		}
	}
	
}

