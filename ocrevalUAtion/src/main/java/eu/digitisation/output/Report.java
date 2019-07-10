/*
 * Copyright (C) 2013 IMPACT Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.output;

import eu.digitisation.distance.Aligner;
import eu.digitisation.distance.EdOpWeight;
import eu.digitisation.distance.EditDistance;
import eu.digitisation.distance.EditSequence;
import eu.digitisation.distance.OcrOpWeight;
import eu.digitisation.document.TermFrequencyVector;
import eu.digitisation.input.Batch;
import eu.digitisation.input.Parameters;
import eu.digitisation.input.SchemaLocationException;
import eu.digitisation.input.WarningException;
import eu.digitisation.log.Messages;
import eu.digitisation.math.Pair;
import eu.digitisation.text.CharFilter;
import eu.digitisation.text.StringNormalizer;
import eu.digitisation.text.Text;
import eu.digitisation.text.WordSet;
import eu.digitisation.xml.DocumentBuilder;

import java.io.File;
import java.nio.file.Paths;

import org.w3c.dom.Element;

/**
 * Create a report in HTML format
 *
 * @author R.C.C
 */
public class Report extends DocumentBuilder {

    Element head;
    Element body;
    private CharStatTable stats;

    /**
     * Initial settings: create an empty HTML document
     */
    private void init() {
        head = addElement("head");
        body = addElement("body");
        // metadata
        Element meta = addElement(head, "meta");
        meta.setAttribute("http-equiv", "content-type");
        meta.setAttribute("content", "text/html; charset=UTF-8");
    }

    /**
     * Insert a table at the end of the document body
     *
     * @param content
     *            the table content
     * @return the table element
     */
    private Element addTable(Element parent, String[][] content) {
        Element table = addElement(parent, "table");
        table.setAttribute("border", "1");
        for (String[] row : content) {
            Element tabrow = addElement(table, "tr");
            for (String cell : row) {
                addTextElement(tabrow, "td", cell);
            }
        }
        return table;
    }

    /**
     *
     * @param batch
     *            a batch of file pairs
     * @param pars
     *            input parameters
     * @throws eu.digitisation.input.WarningException
     * @throws eu.digitisation.input.SchemaLocationException
     */
    public Report(Batch batch, Parameters pars)
            throws WarningException, SchemaLocationException {
        super("html");
        init();

        File swfile = pars.swfile.getValue();
        EdOpWeight w = new OcrOpWeight(pars);
        stats = new CharStatTable();
        CharFilter filter;

        // optional eqfile
        if (pars.compatibility.getValue() != null) {
            filter = new CharFilter(pars.compatibility.getValue(),
                    pars.eqfile.getValue());
        } else {
            filter = new CharFilter(pars.compatibility.getValue());
        }

        Element summaryTab;
        int numwords = 0; // number of words in GT
        int wdist = 0; // word distances
        int bdist = 0; // bag-of-words distanbces

        addTextElement(body, "h2", "General results");
        summaryTab = addElement(body, "div");
        addTextElement(body, "h2", "Difference spotting");

        for (int n = 0; n < batch.size(); ++n) {
            Pair<File, File> input = batch.pair(n);
            Messages.info("Processing " + input.first.getName());
            Text gt = new Text(input.first);
            Text ocr = new Text(input.second);
            String gtref = pars.ignoreDiacritics.getValue() // remove spurious
                                                            // marks
            ? gt.toString(filter).replaceAll(
                    " \\p{InCombiningDiacriticalMarks}+", " ")
                    : gt.toString(filter);
            String ocrref = pars.ignoreDiacritics.getValue()
                    ? ocr.toString(filter) // remove spurious marks
                    .replaceAll(" \\p{InCombiningDiacriticalMarks}+", " ")
                    : ocr.toString(filter);
            String gts = StringNormalizer.canonical(gtref,
                    pars.ignoreCase.getValue(),
                    pars.ignoreDiacritics.getValue(),
                    false);
            String ocrs = StringNormalizer.canonical(ocrref,
                    pars.ignoreCase.getValue(),
                    pars.ignoreDiacritics.getValue(),
                    false);
            EditSequence eds = new EditSequence(gts, ocrs, w, 2000);
            TermFrequencyVector gtv = new TermFrequencyVector(gts);
            TermFrequencyVector ocrv = new TermFrequencyVector(ocrs);
            Element alitab = Aligner.bitext(input.first.getName(),
                    input.second.getName(), gtref, ocrref, w, eds);
            int[] wd = (swfile == null)
                    ? EditDistance.wordDistance(gts, ocrs, 1000)
                    : EditDistance.wordDistance(gts, ocrs, new WordSet(swfile),
                            1000);

            stats.add(eds.stats(gtref, ocrref, w));
            addTextElement(body, "div", " ");
            addElement(body, alitab);
            numwords += wd[0]; // length (words) in gts
            wdist += wd[2]; // word-based distance
            bdist += gtv.distance(ocrv);
        }
        // Summary table
        double cer = stats.cer();
        double accuracy = stats.accuracy();
        double wer = wdist / (double) numwords;
        double ber = bdist / (double) numwords;
        String[][] summaryContent = {
                { "CER %", String.format("%.2f", cer * 100) },
                // {"CER (with swaps)", String.format("%.2f", cerDL * 100)},
                { "WER %", String.format("%.2f", wer * 100) },
                { "WER % (order independent)", String.format("%.2f", ber * 100) },
                { "Character accuracy %", String.format("%.2f", accuracy * 100) }
        };
        addTable(summaryTab, summaryContent);
        // CharStatTable
        addTextElement(body, "h2", "Error rate per character and type");
        addElement(body, stats.asTable());
    }

    public CharStatTable getStats() {
        return stats;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: aligner file1 file2");
        } else {
            Element alitab =
                    Aligner.alignmentMap("", "", args[0], args[1], null);
        }
    }
}
