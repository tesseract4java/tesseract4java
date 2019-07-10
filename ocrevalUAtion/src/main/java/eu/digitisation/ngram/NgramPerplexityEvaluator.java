package eu.digitisation.ngram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Perplexity evaluator based on an n-gram model
 *
 */
public class NgramPerplexityEvaluator implements PerplexityEvaluator {

    NgramModel ngram;

    public NgramPerplexityEvaluator(NgramModel ngram) {
        this.ngram = ngram;
    }

    public NgramPerplexityEvaluator(File file) {
        ngram = new NgramModel(file);
    }

    /**
     * Calculates perplexity for each character of a given text.
     *
     * @param textToEvaluate perplexity of characters contained in this text is
     * calculated
     * @param contextLength the length of character context that is considered
     * when calculating perplexity
     * @return array of perplexity values, each item in the array is a
     * perplexity of corresponding character in the given text.
     */
    @Override
    public double[] calculatePerplexity(String textToEvaluate, int contextLength) {
        int textLen = textToEvaluate.length();
        double[] logprobs = new double[textLen];
        for (int pos = 0; pos < textLen; ++pos) {
            int beg = Math.max(0, pos - contextLength);
            String context = textToEvaluate.substring(beg, pos);
            logprobs[pos] = ngram.logProb(context, textToEvaluate.charAt(pos));
        }
        return logprobs;
    }

    public static void main(String[] args) throws FileNotFoundException {
        NgramModel model = new NgramModel(new File(args[0]));
        int contextLenght = Integer.parseInt(args[1]);
        InputStream is = (args.length == 3)
                ? new FileInputStream(new File(args[2]))
                : System.in;

        TextPerplexity result
                = new TextPerplexity(model, is, contextLenght);

        String text = result.getText();
        double[] perps = result.getPerplexities();

        for (int n = 0; n < text.length(); ++n) {
            System.out.println(text.charAt(n) + " " + perps[n]);
        }
    }
}
