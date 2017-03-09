package asu.edu.util;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class SentenceProperties {
	public HashMap<String, String> getSentencePOS(String sentence){
		HashMap<String, String> verbsMap = new HashMap<>();
		
		Properties props = new Properties();
        
        props.setProperty("annotators","tokenize, ssplit, pos, lemma");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation annotation = new Annotation(sentence);
        pipeline.annotate(annotation);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence1 : sentences) {
            for (CoreLabel token: sentence1.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                String lemma = token.getString(CoreAnnotations.LemmaAnnotation.class);
                System.out.println(lemma);
                System.out.println(word + "/" + pos);
                
                if(isPOSVerb(pos)){
                	verbsMap.put(word, null);
                }
            }
        }
        return verbsMap;
	}
	
	private boolean isPOSVerb(String pos) {
		if(pos=="VB" || pos=="VBD" || pos=="VBG" || pos=="VBN" || pos=="VBP" || pos=="VBZ")
			return true;
		else
			return false;
	}
}
