package de.vorb.tesseract.gui.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.bridj.BridJ;
import org.bridj.Pointer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

import de.vorb.leptonica.LibLept;
import de.vorb.leptonica.Pix;
import de.vorb.swt.dialog.MessageDialog;
import de.vorb.swt.dialog.MessageDialog.Type;
import de.vorb.swt.dialog.Modality;
import de.vorb.tesseract.PageIteratorLevel;
import de.vorb.tesseract.gui.event.SettingsChangeListener;
import de.vorb.tesseract.gui.model.GlobalPrefs;
import de.vorb.tesseract.gui.model.PageModel;
import de.vorb.tesseract.gui.view.AboutDialog;
import de.vorb.tesseract.gui.view.NewProjectDialog;
import de.vorb.tesseract.gui.view.TesseractView;
import de.vorb.tesseract.gui.view.TesseractView.ProgressState;
import de.vorb.tesseract.tools.recognition.DefaultRecognitionConsumer;
import de.vorb.tesseract.tools.recognition.RecognitionState;
import de.vorb.tesseract.util.Box;
import de.vorb.tesseract.util.Languages;
import de.vorb.tesseract.util.Line;
import de.vorb.tesseract.util.Page;
import de.vorb.tesseract.util.Project;
import de.vorb.tesseract.util.Symbol;
import de.vorb.tesseract.util.Word;

import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

import akka.dispatch.ExecutionContexts;
import akka.dispatch.Futures;
import akka.dispatch.OnFailure;
import akka.dispatch.OnSuccess;

public class TesseractSWTController implements Listener, SettingsChangeListener {
    private final ExecutionContext ec = ExecutionContexts.global();

    private final TesseractView view;
    private PageLoader pageLoader = null;

    private Project currentProject = null;
    private Path currentPage = null;

    public TesseractSWTController(Display display) {
        view = new TesseractView(display);

        try {
            // initialize languages
            // get tessdata directory from preferences
            final Path tessdataDir = Paths.get(GlobalPrefs.getPrefs().get(
                    GlobalPrefs.TESSDATA_DIR,
                    GlobalPrefs.TESSDATA_DIR_DEFAULT));

            // get the user's default language preference
            final String defaultLanguage =
                    GlobalPrefs.getPrefs().get("lang.default", "eng");

            final List langList = view.getLanguageList();

            // fill language list
            for (String lang : Languages.getLanguageList(tessdataDir)) {
                langList.add(lang);

                // pre-select default language
                if (defaultLanguage.equals(lang)) {
                    langList.setSelection(langList.getItemCount() - 1);
                }
            }
        } catch (IOException e) {
            // when an error occurs, shut down the application with an error
            // message
            MessageDialog.show(
                    view,
                    MessageDialog.Type.WARNING,
                    "Language Data Missing",
                    "Tesseract failed to load the language files. Please review the settings.",
                    Modality.APPLICATION);
        }

        try {
            pageLoader = new PageLoader("deu-frak");
        } catch (Exception e) {
            e.printStackTrace();
        }

        view.getNewProjectWidget().addListener(SWT.Selection, this);
        view.getOpenProjectWidget().addListener(SWT.Selection, this);
        view.getAboutWidget().addListener(SWT.Selection, this);

        view.getPageList().addListener(SWT.Selection, this);

        view.open();
        view.layout();
    }

    public static void main(String[] args) {
        BridJ.setNativeLibraryFile("leptonica", new File("liblept170.dll"));
        BridJ.setNativeLibraryFile("tesseract", new File("libtesseract303.dll"));

        final Display display = Display.getDefault();
        final TesseractSWTController controller = new TesseractSWTController(
                display);

        // event loop
        while (!controller.view.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        display.dispose();
    }

    @Override
    public void handleEvent(Event evt) {
        switch (evt.type) {
        case SWT.Selection:
            if (evt.widget == view.getNewProjectWidget()) {
                handleNewProject();
            } else if (evt.widget == view.getOpenProjectWidget()) {
                handleOpenProject();
            } else if (evt.widget == view.getExitWidget()) {
                handleApplicationExit();
            } else if (evt.widget == view.getAboutWidget()) {
                handleAbout();
            } else if (evt.widget == view.getRefreshPageWidget()) {
                handleRefreshPage();
            } else if (evt.widget == view.getPageList()) {
                handlePageSelection();
            }

            break;
        }
    }

    private void handlePageSelection() {
        final List pageList = view.getPageList();
        final int selIndex = pageList.getSelectionIndex();
        final String fname = pageList.getItem(selIndex);

        currentPage = currentProject.getScanDir().resolve(fname);
        handleRefreshPage();
    }

    private void handleRefreshPage() {
        view.setProgress(ProgressState.INDETERMINATE, 0);

        if (currentPage == null) {
            // reset all views
            return;
        }

        // asynchronously load the page model
        final Future<PageModel> future = Futures.future(
                new Callable<PageModel>() {
                    @Override
                    public PageModel call() throws Exception {
                        return loadPageModel(currentPage);
                    }
                }, ec);

        // on success, show it
        future.onSuccess(new OnSuccess<PageModel>() {
            @Override
            public void onSuccess(final PageModel pageModel) throws Throwable {
                view.getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        view.getPageView().setPageModel(pageModel);
                        view.setProgress(ProgressState.DEFAULT, 0);
                    }
                });
            }
        }, ec);

        future.onFailure(new OnFailure() {
            @Override
            public void onFailure(final Throwable t) {
                view.getDisplay().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        MessageDialog.show(
                                view,
                                Type.ERROR,
                                "Error",
                                "Could not show the page.\n"
                                        + t.getLocalizedMessage(),
                                Modality.APPLICATION);
                    }
                });
            }
        }, ec);
    }

    private void handleNewProject() {
        final NewProjectDialog newProjDialog = new NewProjectDialog(view);
        final Project newProject = newProjDialog.open();

        // cancelled
        if (newProject == null) {
            return;
        } else {
            currentProject = newProject;

            // add pages
            for (final Path page : newProject.getPages()) {
                view.getPageList().add(page.getFileName().toString());
            }
        }
    }

    private void handleOpenProject() {
        final FileDialog fileDialog = new FileDialog(view);
        final String projFile = fileDialog.open();

        // cancelled
        if (projFile == null)
            return;

        final Path projPath = Paths.get(fileDialog.open());

        System.out.println(projPath);
    }

    private void handleCloseProject() {

    }

    private void handleApplicationExit() {
        // TODO Auto-generated method stub
    }

    private void handleAbout() {
        final AboutDialog aboutDialog = new AboutDialog(view);
        aboutDialog.open();
    }

    @Override
    public void settingsChanged() {

    }

    private PageModel loadPageModel(Path file) throws IOException {
        pageLoader.reset();

        final Vector<Line> lines = new Vector<Line>();

        final Pointer<LibLept.FILE> fp =
                LibLept.fopenReadStream(Pointer.pointerToCString(file.toAbsolutePath().toString()));

        final Pointer<Pix> ppix = LibLept.pixReadStreamPng(fp);

        // Get images
        pageLoader.setOriginalImage(ppix);

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

        final Pix pix = ppix.get();
        final Page page = new Page(file, pix.w(), pix.h(), 300, lines);

        LibLept.pixDestroy(Pointer.pointerToPointer(ppix));

        final PageModel model = new PageModel(page, file);

        // try {
        // page.writeTo(System.out);
        // } catch (JAXBException e) {
        // e.printStackTrace();
        // }

        return model;
    }
}
