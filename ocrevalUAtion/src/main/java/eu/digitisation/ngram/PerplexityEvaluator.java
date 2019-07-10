package eu.digitisation.ngram;

/**
 * Interface for perplexity evaluator. It calculates perplexity of characters in a given text.
 * @author tparkola
 *
 */
public interface PerplexityEvaluator {
	
	/**
	 * Calculates perplexity for each character of a given text.
	 * @param textToEvaluate perplexity of characters contained in this text is calculated 
	 * @param contextLength the length of character context that is considered when calculating perplexity
	 * @return array of perplexity values, each item in the array is a perplexity of corresponding character in the given text.
	 */
	public double[] calculatePerplexity(String textToEvaluate, int contextLength);
}
