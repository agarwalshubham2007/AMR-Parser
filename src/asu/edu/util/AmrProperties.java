package asu.edu.util;

import java.util.HashSet;

public class AmrProperties {
	public void getAllAmrRelationsInCorpus(TreeNode root, HashSet<String> amrRelationSet){
		if(root.word==null) return;
		for(int i=0;i<root.childEdge.size();i++)
        	amrRelationSet.add(root.childEdge.get(i));
		
		for(int i=0;i<root.childNode.size();i++)
        	getAllAmrRelationsInCorpus(root.childNode.get(i), amrRelationSet);
	}
}
