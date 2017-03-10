package asu.edu.app;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import asu.edu.event_separation.AmrCoreference;
import asu.edu.event_separation.ParseTree;
import asu.edu.event_separation.SpanGraph;
import asu.edu.util.AmrProperties;
import asu.edu.util.Constants;
import asu.edu.util.SentenceProperties;
//import simplifiedAMR_EventSeparation.SimplifiedEvents;
import asu.edu.util.TreeNode;

public class event_separation_app {
	public static int leftPos = 0;
	public static int rightPos = 1;
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException{
		init();
	}

	private static void init() throws ParserConfigurationException, SAXException, IOException {
//    	File inputFile = new File("/Users/Shubham/Documents/workspace/AMR_Parser/Dataset/sample.xml");
    	File inputFile = new File("/Users/Shubham/Documents/workspace/AMR_Parser/Dataset/amr-bank-v1.6.xml");
    	
    	File amrAlignedFile = new File("/Users/Shubham/Documents/workspace/AMR_Parser/Dataset/amr-bank-v1.6-aligned.txt");
//    	File amrAlignedFile = new File("/Users/Shubham/Documents/workspace/AMR_Parser/Dataset/sample-aligned.txt");
    	FileInputStream fis = new FileInputStream(amrAlignedFile);
    	BufferedReader br = new BufferedReader(new InputStreamReader(fis));
    	
    	//test code section
    	
    	//test code section end
    	
    	
    	ObjectMapper mapper = new ObjectMapper();
        JsonNode sentencesJson = mapper.createArrayNode();
    	
    	
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
        NodeList nList = doc.getElementsByTagName("sntamr");
        
        // this set holds all the relations in the amr corpus
        HashSet<String> amrRelationSet = new HashSet<>();
        
        for (int temp = 0; temp < nList.getLength(); temp++) {
        	Node nNode = nList.item(temp);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
            	// resetting leftNode and rightNode values
            	leftPos = 0;
            	rightPos = 1;
            	
                Element eElement = (Element) nNode;
                
                // amr and sentence store the obvious entities respectively
                String sentence = eElement.getElementsByTagName("sentence").item(0).getTextContent();
                System.out.println("Sentence : " + sentence);
                
                String amr = eElement.getElementsByTagName("amr").item(0).getTextContent().trim().replaceAll("\n", "");
                amr = amr.replaceAll("(?m)(^ *| +(?= |$))", "").replaceAll("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+", "$1");

                System.out.println("AMR : " + amr);
                TreeNode root = new TreeNode();
                
                //key = alias ; value = corresponding TreeNode
                // I am using this HashMap for co-reference resolution in the amr graph during pre-processing
                HashMap<String, TreeNode> alias_TreeNodeHM = new HashMap<>();
                
                
                //make parse tree of AMR : ParseTree.java
                ParseTree ptObj = new ParseTree();
                ptObj.makeParseTreeWrapper(root, amr, alias_TreeNodeHM);
                
//                printParseTree(root);
//                System.exit(0);
                
//                AmrProperties amrPropObj = new AmrProperties();
//                amrPropObj.getAllAmrRelationsInCorpus(root, amrRelationSet);
                
                //resolve ARG-Of in parse tree
                //ParseTree.resolveArgOf(root);

                // printing the parsed tree before coref resolution
//                printParseTree(root);
//                for(String key : alias_TreeNodeHM.keySet())
//                	System.out.print(key +" : " +alias_TreeNodeHM.get(key).word + " | ");
                
                // coref resolution
                AmrCoreference amrCorefObj = new AmrCoreference();
                amrCorefObj.resolveCorefInAmr(root, alias_TreeNodeHM);
                
                //print parse tree after coref
                HashSet<TreeNode> visitedNodesSet = new HashSet<>();
                printParseTreeAfterCoref(root, null, visitedNodesSet);
                
                // storing verb POS in sentence
                // but it first finds all POS in the sentence and then filters the verbs
//                SentenceProperties spObj = new SentenceProperties();
//                spObj.getSentencePOS(sentence);
                
                // span graph
                ArrayList<String> instanceAlignmentText = new ArrayList<>();
                String line = null;
                while((line = br.readLine()) != null){
                	if(!line.equals("")){
                		if(line.startsWith(Constants.alignmentPrefixConstant))
                			instanceAlignmentText.add(line);
                	}
                	else{
                		SpanGraph sg = new SpanGraph();
                		sg.makeSpanGraph(root, instanceAlignmentText, sentence);
                		instanceAlignmentText.clear();
                		break;
                	}
                }
                
                // print span graph
                visitedNodesSet.clear();
            	printSpanGraph(root, visitedNodesSet);
                // end span graph block
                
                System.out.println("Continue(Y/N)");
//                System.in.read();
                
            }
        }
        
//        System.out.println(amrRelationSet);
                
	}
	
	private static void printSpanGraph(TreeNode root, HashSet<TreeNode> visitedNodesSet) {
		if(root==null || visitedNodesSet.contains(root))
			return;
		
		System.out.println("Node: "+ root.alias + " / " + root.word);
		System.out.println("Start-end Index : " + root.start_sent_ind + " -> " + root.end_sent_ind);
		System.out.println("# Children edges:"+ root.childEdge.size());
		for(int i=0;i<root.childEdge.size();i++)
        	System.out.println(root.childEdge.get(i));
		System.out.println("# Children nodes:"+ root.childNode.size());
		for(int i=0;i<root.childNode.size();i++)
        	System.out.println(root.childNode.get(i).word);
		
		System.out.println();
		visitedNodesSet.add(root);
		
		for(int i=0;i<root.childNode.size();i++)
        	printSpanGraph(root.childNode.get(i), visitedNodesSet);	
	}

	private static void printParseTreeAfterCoref(TreeNode curr, TreeNode prev, HashSet<TreeNode> visitedNodesSet) {
		if(visitedNodesSet.contains(curr)) {
			System.out.println("COREFERECE from " + prev.alias + " / " + prev.word + " to " + curr.alias + " / " + curr.word );
			return;
		}
		System.out.println("Node: "+ curr.alias + " / " + curr.word);
		System.out.println("# Children edges:"+ curr.childEdge.size());
		for(int i=0;i<curr.childEdge.size();i++)
        	System.out.println(curr.childEdge.get(i));
		System.out.println("# Children nodes:"+ curr.childNode.size());
		for(int i=0;i<curr.childNode.size();i++)
        	System.out.println(curr.childNode.get(i).word);
		
		System.out.println();
		
		visitedNodesSet.add(curr);
		
		for(int i=0;i<curr.childNode.size();i++)
        	printParseTreeAfterCoref(curr.childNode.get(i), curr, visitedNodesSet);
	}

	private static void printParseTree(TreeNode root) {
		if(root.word==null) return;
		System.out.println("Node: "+root.word);
		System.out.println("# Children edges:"+ root.childEdge.size());
		for(int i=0;i<root.childEdge.size();i++)
        	System.out.println(root.childEdge.get(i));
		System.out.println("# Children nodes:"+ root.childNode.size());
		for(int i=0;i<root.childNode.size();i++)
        	System.out.println(root.childNode.get(i).word);
		
		System.out.println();
		
		for(int i=0;i<root.childNode.size();i++)
        	printParseTree(root.childNode.get(i));
	}
}
