package eu.digitisation;

import eu.digitisation.input.Batch;
import eu.digitisation.input.Parameters;
import eu.digitisation.input.SchemaLocationException;
import eu.digitisation.input.WarningException;
import eu.digitisation.log.Messages;
import eu.digitisation.output.Report;
import java.io.File;
import java.io.InvalidObjectException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for ocrevalUAtion: version 0.92
 */
public class Main {

    static final String helpMsg = "Usage:\t"
            + "ocrevalUAtion -gt file1"
            + "-ocr file2"
            + "[-e encoding]"
            + " [-o output_file_or_dir ] [-r equivalences_file]"
            + " [-c] [-ic] [-id] [-ip]";

    private static void exit_gracefully() {
        System.err.println(helpMsg);
        System.exit(0);
    }

    /**
     * @param args the command line arguments
     * @throws eu.digitisation.input.WarningException
     */
    public static void main(String[] args) throws WarningException {
        Parameters pars = new Parameters();
        File workingDirectory;

        // Read parameters (String switch needs Java 1.7 or later)
        for (int n = 0; n < args.length; ++n) {
            String arg = args[n];
            if (arg.equals("-h")) {
                exit_gracefully();
            } else if (arg.equals("-gt")) {
                pars.gtfile.setValue(new File(args[++n]));
            } else if (arg.equals("-e")) {
                pars.encoding.setValue(args[++n]);
            } else if (arg.equals("-ocr")) {
                pars.ocrfile.setValue(new File(args[++n]));
            } else if (arg.equals("-r")) {
                pars.eqfile.setValue(new File(args[++n]));
            } else if (arg.equals("-o")) {
                pars.outfile.setValue(new File(args[++n]));
            } else if (arg.equals("-c")) {
                pars.compatibility.setValue(true);
            } else if (arg.equals("-ic")) {
                pars.ignoreCase.setValue(true);
            } else if (arg.equals("-id")) {
                pars.ignoreDiacritics.setValue(true);
            } else if (arg.equals("-ip")) {
                pars.ignorePunctuation.setValue(true);
            } else {
                System.err.println("Unrecognized option " + arg);
                exit_gracefully();
            }
        }

        if (pars.gtfile.getValue() == null || pars.ocrfile.getValue() == null) {
            System.err.println("Not enough arguments");
            exit_gracefully();
        }

        if (pars.outfile.getValue() == null) {
            String name = pars.ocrfile.getValue().getName().replaceAll("\\.\\w+", "")
                    + "_report.html";
            pars.outfile.setValue(new File(pars.ocrfile.getValue().getParent(), name));
        }

        try {
            Batch batch = new Batch(pars.gtfile.getValue(), pars.ocrfile.getValue());
            Report report = new Report(batch, pars);
            report.write(pars.outfile.getValue());
        } catch (InvalidObjectException ex) {
            Messages.info(Main.class.getName() + ": " + ex);
        } catch (SchemaLocationException ex) {
            Messages.info(Main.class.getName() + ": " + ex);
        }

    }
}
