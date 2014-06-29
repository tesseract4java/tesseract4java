package de.vorb.tesseract.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bridj.BridJ;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.FilteredListModel;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.util.PageListLoader;
import de.vorb.tesseract.gui.util.PageRecognitionProducer;
import de.vorb.tesseract.gui.view.NewProjectDialog;
import de.vorb.tesseract.gui.view.NewProjectDialog.Result;
import de.vorb.tesseract.gui.view.TesseractFrame;
import de.vorb.tesseract.util.TrainingFiles;

public class TesseractController extends WindowAdapter implements
        ActionListener,
        ListSelectionListener {

    private final TesseractFrame view;
    private Optional<SwingWorker<PageModel, Void>> pageLoaderWorker;
    private final PageRecognitionProducer pageRecognitionProducer;

    private final Timer pageSelectionTimer = new Timer("PageSelectionTimer");
    private Optional<TimerTask> lastPageSelection = Optional.absent();

    public static void main(String[] args) {
        BridJ.setNativeLibraryFile("leptonica", new File("liblept170.dll"));
        BridJ.setNativeLibraryFile("tesseract", new File("libtesseract303.dll"));

        try {
            new TesseractController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TesseractController() throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fail silently
            // If the system LaF is not available, use whatever LaF is already
            // being used.
        }

        // create new tesseract frame
        view = new TesseractFrame();

        pageRecognitionProducer = new PageRecognitionProducer("deu-frak",
                TrainingFiles.getTessdataDir());

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

        // register listeners
        view.getMenuItemNewProject().addActionListener(this);
        view.getPageList().getList().addListSelectionListener(this);

        view.addWindowListener(this);

        view.setVisible(true);
    }

    public PageRecognitionProducer getPageRecognitionProducer() {
        return pageRecognitionProducer;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        final Object source = evt.getSource();
        if (source.equals(view.getMenuItemNewProject())) {
            handleNewProject();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        final Object source = evt.getSource();
        if (source.equals(view.getPageList().getList())) {
            handlePageSelection();
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

        final PageListLoader pageListLoader =
                new PageListLoader(projectConfig, pages);

        pageListLoader.execute();
    }

    private void handlePageSelection() {
        final PageThumbnail pt =
                view.getPageList().getList().getSelectedValue();

        // cancel the last page load if it is present
        if (lastPageSelection.isPresent()) {
            lastPageSelection.get().cancel();
        }

        // new task
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    final BufferedImage image =
                            ImageIO.read(pt.getFile().toFile());

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            view.getBoxEditor().getImage().setIcon(
                                    new ImageIcon(image));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        // run the page loader with a delay of 1 second
        pageSelectionTimer.schedule(task, 1000);

        // set as new timer task
        lastPageSelection = Optional.of(task);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        pageSelectionTimer.cancel();
    }

    public TesseractFrame getView() {
        return view;
    }
}
