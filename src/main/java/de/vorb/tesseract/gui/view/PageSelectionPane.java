package de.vorb.tesseract.gui.view;

import java.awt.BorderLayout;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.vorb.tesseract.gui.event.LocaleChangeListener;
import de.vorb.tesseract.util.Project;

public class PageSelectionPane extends JPanel implements LocaleChangeListener {
  private static final long serialVersionUID = 1L;

  private final JList<Path> listPages;
  private Project model;
  private int lastIndex = 0;

  private boolean isFirstChange = true;

  public PageSelectionPane() {
    super();

    setLayout(new BorderLayout(0, 0));
    setBorder(new EmptyBorder(0, 2, 2, 2));

    JScrollPane scrollPane = new JScrollPane();
    add(scrollPane, BorderLayout.CENTER);

    listPages = new JList<Path>();
    listPages.setCellRenderer(new PageListCellRenderer());
    listPages.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent ev) {
        final int newIndex = Math.max(listPages.getSelectedIndex(), 0);

        if (!isFirstChange && newIndex == lastIndex) {
          return;
        } else {
          isFirstChange = false;
        }

        getModel().setSelectedPageIndex(newIndex);
        lastIndex = newIndex;
      }
    });
    scrollPane.setViewportView(listPages);

    listPages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    setModel(new Project(Paths.get(""), new LinkedList<Path>()));

    localeChanged();
  }

  public void setModel(Project model) {
    if (model == null)
      throw new IllegalArgumentException("model was null");

    this.isFirstChange = true;
    this.model = model;

    // initialize list
    final DefaultListModel<Path> pagesModel = new DefaultListModel<Path>();
    for (Path page : model.getPages()) {
      pagesModel.addElement(page);
    }

    listPages.setModel(pagesModel);
    listPages.setSelectedIndex(0);
  }

  public Project getModel() {
    return model;
  }

  @Override
  public void localeChanged() {
  }
}
