package eu.digitisation.ngram;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class PerplexityEvaluatorAssesmentHelper {
	public static void main(String[] args) throws IOException {
		// langModel, OCRFile, contextLengthRange, resultFile
		File langModelFile = new File(args[0]);
		File OCRFile = new File(args[1]);
		File outputFile = new File(args[3]);

		String OCRText = extractString(OCRFile);

		NgramModel providedModel = new NgramModel(langModelFile);

		ContextLengthRange contextLengthRange = ContextLengthRange
				.parseContextLengthRange(args[2]);

		double[][] perplexities = new double[contextLengthRange.getEnd()
				- contextLengthRange.getStart() + 1][OCRText.length()];

		PerplexityEvaluator logPerplexityEvaluator = new NgramPerplexityEvaluator(
				providedModel);

		for (int i = contextLengthRange.getStart(); i <= contextLengthRange
				.getEnd(); i++) {
			perplexities[i - contextLengthRange.getStart()] = logPerplexityEvaluator.calculatePerplexity(
					OCRText, i);
		}
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
		
		bw.write(providedModel.order + "-gram model results:");
		bw.newLine();
		printPerplexities(contextLengthRange, OCRText, perplexities, bw);
	}

	private static void printPerplexities(
			ContextLengthRange contextLengthRange, String OCRText,
			double[][] perplexities, BufferedWriter bw) throws IOException {
//		System.out.print("Letter\t");
		bw.write("Letter\t");
		for (int i = contextLengthRange.getStart(); i <= contextLengthRange
				.getEnd(); i++) {
//			System.out.print("PC" + i + "\t");
			bw.write("PC" + i + "\t");
		}
//		System.out.println();
		bw.newLine();
		for (int j = 0; j < OCRText.length(); j++) {
//			System.out.print(OCRText.charAt(j) + "\t");
			bw.write(OCRText.charAt(j) + "\t");
			for (int i = 0; i < perplexities.length; i++) {
//				System.out.print(perplexities[i][j] + "\t");
				bw.write(perplexities[i][j] + "\t");
			}
//		System.out.println();
			bw.newLine();
		}
		bw.close();
	}

	private static String extractString(File OCRFile)
			throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(OCRFile));

		StringBuffer OCRFileText = new StringBuffer();

		String line = null;
		while ((line = reader.readLine()) != null) {
			OCRFileText.append(line + "\n");
		}

		reader.close();
		String OCRText = OCRFileText.toString();
		return OCRText;
	}
}
