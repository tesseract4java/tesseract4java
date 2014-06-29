package de.vorb.tesseract.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bridj.BridJ;

import com.google.common.base.Optional;

import de.vorb.tesseract.gui.model.FilteredListModel;
import de.vorb.tesseract.gui.model.PageThumbnail;
import de.vorb.tesseract.gui.util.PageListLoader;
import de.vorb.tesseract.gui.util.PageModelLoader;
import de.vorb.tesseract.gui.util.PageRecognitionProducer;
import de.vorb.tesseract.gui.view.Dialogs;
import de.vorb.tesseract.gui.view.NewProjectDialog;
import de.vorb.tesseract.gui.view.NewProjectDialog.Result;
import de.vorb.tesseract.gui.view.TesseractFrame;
import de.vorb.tesseract.util.TrainingFiles;

public class TesseractController extends WindowAdapter implements
        ActionListener, ListSelectionListener {

    private final TesseractFrame view;
    private final PageRecognitionProducer pageRecognitionProducer;
    private Optional<PageModelLoader> pageModelLoader = Optional.absent();

    private final Timer pageSelectionTimer = new Timer("PageSelectionTimer");
    private Optional<TimerTask> lastPageSelection = Optional.absent();

    public static void main(String[] args) {
        BridJ.setNativeLibraryFile("leptonica", new File("liblept170.dll"));
        BridJ.setNativeLibraryFile("tesseract", new File("libtesseract303.dll"));

        new TesseractController();
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

        pageRecognitionProducer = new PageRecognitionProducer("deu-frak",
                TrainingFiles.getTessdataDir());

        try {
            pageRecognitionProducer.init();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // register listeners
        view.getMenuItemNewProject().addActionListener(this);
        view.getPageList().getList().addListSelectionListener(this);
        view.addWindowListener(this);

        view.setVisible(true);

        // init training files
        try {
            final List<String> trainingFiles = TrainingFiles.getAvailable();

            // prepare training file list model
            final DefaultListModel<String> trainingFilesModel =
                    new DefaultListModel<>();

            for (String trainingFile : trainingFiles) {
                trainingFilesModel.addElement(trainingFile);
            }

            final JList<String> trainingFilesList =
                    view.getTrainingFiles().getList();

            // wrap it in a filtered model
            trainingFilesList.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION);
            trainingFilesList.setModel(
                    new FilteredListModel<String>(trainingFilesModel));

            trainingFilesList.setSelectedValue("eng", true);
        } catch (IOException e) {
            Dialogs.showError(view, "Error",
                    "Training files could not be found.");
        }
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
                // cancel last task
                if (pageModelLoader.isPresent()) {
                    pageModelLoader.get().cancel(false);
                }

                // create swingworker to load model
                final PageModelLoader pml = new PageModelLoader(
                        TesseractController.this, pt.getFile());

                // save reference
                pageModelLoader = Optional.of(pml);

                // execute it
                pml.execute();
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
