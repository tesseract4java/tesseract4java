package de.vorb.tesseract.gui.controller;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.bridj.BridJ;

import com.google.common.base.Optional;

import de.vorb.tesseract.PageIteratorLevel;
import de.vorb.tesseract.gui.model.FilteredListModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.util.ThumbnailLoader;
import de.vorb.tesseract.gui.view.NewProjectDialog;
import de.vorb.tesseract.gui.view.NewProjectDialog.Result;
import de.vorb.tesseract.gui.view.TesseractFrame;
import de.vorb.tesseract.tools.recognition.DefaultRecognitionConsumer;
import de.vorb.tesseract.tools.recognition.RecognitionState;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.TrainingFiles;
import de.vorb.tesseract.util.Word;

public class TesseractController implements ActionListener {

    private final TesseractFrame view;
    private SwingWorker<PageModel, Void> pageLoaderWorker = null;
    private PageLoader pageLoader = null;

    public static void main(String[] args) {
        BridJ.setNativeLibraryFile("leptonica", new File("liblept170.dll"));
        BridJ.setNativeLibraryFile("tesseract", new File("libtesseract303.dll"));

        final TesseractController controller = new TesseractController();
    }

    public TesseractController() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fail silently
            // If the system LaF is not available, use whatever LaF is already
            // being used.
        }

        // create new tesseract frame
        view = new TesseractFrame();

        try {
            pageLoader = new PageLoader("deu-frak");

            // init training files
            final List<String> trainingFiles = TrainingFiles.getAvailable();

            // prepare training file list model
            final DefaultListModel<String> trainingFilesModel =
                    new DefaultListModel<>();
            for (String trainingFile : trainingFiles) {
                trainingFilesModel.addElement(trainingFile);
            }

            // wrap it in a filtered model
            view.getTrainingFiles().getList().setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION);
            view.getTrainingFiles().getList().setModel(
                    new FilteredListModel<String>(trainingFilesModel));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // register listeners
        view.getMenuItemNewProject().addActionListener(this);

        view.setVisible(true);
    }

    private PageModel loadPageModel(Path scanFile) throws IOException {
        pageLoader.reset();

        final Vector<Line> lines = new Vector<Line>();

        // Get images
        final BufferedImage originalImg = ImageIO.read(scanFile.toFile());
        final BufferedImage thresholdedImg = pageLoader.getThresholdedImage();

        pageLoader.recognize(new DefaultRecognitionConsumer() {
            private ArrayList<Word> lineWords;
            private ArrayList<Symbol> wordSymbols;

            @Override
            public void lineBegin() {
                lineWords = new ArrayList<>();
            }

            @Override
            public void lineEnd() {
                final PageIteratorLevel level = PageIteratorLevel.TEXTLINE;
                lines.add(new Line(getState().getBoundingBox(level), lineWords,
                        getState().getBaseline(level)));
            }

            @Override
            public void wordBegin() {
                wordSymbols = new ArrayList<>();
            }

            @Override
            public void wordEnd() {
                final RecognitionState state = getState();
                final PageIteratorLevel level = PageIteratorLevel.WORD;
                final Box bbox = state.getBoundingBox(level);
                lineWords.add(new Word(wordSymbols, bbox,
                        state.getConfidence(level),
                        state.getBaseline(PageIteratorLevel.WORD),
                        state.getWordFontAttributes()));
            }

            @Override
            public void symbol() {
                final PageIteratorLevel level = PageIteratorLevel.SYMBOL;
                wordSymbols.add(new Symbol(getState().getText(level),
                        getState().getBoundingBox(level),
                        getState().getConfidence(level)));
            }
        });

        final Page page = new Page(scanFile, originalImg.getWidth(),
                originalImg.getHeight(), 300, lines);

        // try {
        // page.writeTo(System.out);
        // } catch (JAXBException e) {
        // e.printStackTrace();
        // }

        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();
        if (source.equals(view.getMenuItemNewProject())) {
            handleNewProject();
        }
    }

    private void handleNewProject() {
        final Optional<NewProjectDialog.Result> result =
                NewProjectDialog.showDialog(view);

        if (!result.isPresent())
            return;

        final Result projectConfig = result.get();

        final DefaultListModel<PageThumbnail> pages =
                (DefaultListModel<PageThumbnail>) view.getPageList()
                        .getList().getModel();

        final ThumbnailLoader thumbnailLoader = new ThumbnailLoader(
                projectConfig, pages);

        thumbnailLoader.execute();
    }
}
