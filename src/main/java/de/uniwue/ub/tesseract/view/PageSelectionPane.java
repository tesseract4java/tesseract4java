package de.uniwue.ub.tesseract.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.uniwue.ub.tesseract.event.LocaleChangeListener;
import de.uniwue.ub.tesseract.util.Project;
import de.uniwue.ub.tesseract.view.i18n.Labels;

public class PageSelectionPane extends JPanel implements LocaleChangeListener {
  private static final long serialVersionUID = 1L;

  private final JList<String> listPages;
  private final JButton btnPreviousPage;
  private final JButton btnNextPage;
  private Project model;
  private int lastIndex = 0;

  private boolean isFirstChange = true;

  private static final ImageIcon ARROW_LEFT_ICON = new ImageIcon(
      "src/main/resources/arrow-left.png", "left arrow");
  private static final ImageIcon ARROW_RIGHT_ICON = new ImageIcon(
      "src/main/resources/arrow-right.png", "left right");

  public PageSelectionPane() {
    super();

    setLayout(new BorderLayout(0, 0));

    JScrollPane scrollPane = new JScrollPane();
    add(scrollPane, BorderLayout.CENTER);

    listPages = new JList<String>();
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

    JPanel panel_1 = new JPanel();
    add(panel_1, BorderLayout.SOUTH);

    btnPreviousPage = new JButton(ARROW_LEFT_ICON);
    panel_1.add(btnPreviousPage);
    btnPreviousPage.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final int prev = Math.max(listPages.getSelectedIndex() - 1,
            getModel().getMinimumPageIndex());
        listPages.setSelectedIndex(prev);
      }
    });

    btnNextPage = new JButton(ARROW_RIGHT_ICON);
    btnNextPage.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent ev) {
        final int next = Math.min(listPages.getSelectedIndex() + 1,
            getModel().getMaximumPageIndex());
        listPages.setSelectedIndex(next);
      }
    });
    panel_1.add(btnNextPage);

    setModel(new Project(Paths.get(""), Paths.get(""), new LinkedList<String>()));

    localeChanged();
  }

  public void setModel(Project model) {
    if (model == null)
      throw new IllegalArgumentException("model was null");

    this.isFirstChange = true;
    this.model = model;

    // initialize list
    final DefaultListModel<String> pagesModel = new DefaultListModel<String>();
    for (String page : model.getPages()) {
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
    btnPreviousPage.setToolTipText(Labels.getLabel(getLocale(),
        "tooltip_prev_page"));
    btnNextPage.setToolTipText(Labels.getLabel(getLocale(), "tooltip_next_page"));
  }
}
