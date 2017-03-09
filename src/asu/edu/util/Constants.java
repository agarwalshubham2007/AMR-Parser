package asu.edu.util;

import java.util.Arrays;
import java.util.HashSet;

public class Constants {
	// these list of relations do not contribute to co-reference in amr graph because the following node is some constant. 
	// Example: :mode interrogative/imperative/expressive
	// for example on each one of these, look into the drive notes dated 23rd February 2017
	public static final HashSet<String> corefRelationConstants = 
			new HashSet<String>(Arrays.asList("mode", "polarity", "frequency", "value", "year", "quant", "op1", "op2", "op3","op4", "op6", "polite", "wiki"));
	public static final String alignmentPrefixConstant = "# ::node";
}
