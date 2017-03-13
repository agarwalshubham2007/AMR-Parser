package asu.edu.event_separation;

import java.util.HashMap;

import asu.edu.util.TreeNode;
import asu.edu.util.UtilityFunctions;


public class AmrCoreference {
	public void resolveCorefInAmr(TreeNode root, HashMap<String, TreeNode> alias_TreeNodeHM){
		if(root == null || root.childEdge.size()==0)// if current node is null or it has no children -> return
			return;
		for(int i=0; i<root.childNode.size(); i++){
			if(isCoReferencee(root.childNode.get(i))){
				root.childNode.set(i, alias_TreeNodeHM.get(root.childNode.get(i).alias));
			}
			else
				resolveCorefInAmr(root.childNode.get(i), alias_TreeNodeHM);
		}
	}

	private boolean isCoReferencee(TreeNode node) {
		if(node.alias != null && node.word == null && !UtilityFunctions.isInteger(node.alias) && !node.alias.equals("-"))
			return true;
		return false;
	} 
}
