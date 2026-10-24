/*
 * Copyright 2018 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar.ui;

import static net.atomique.ksar.I18n.tr;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class GraphSelection extends javax.swing.JDialog
    implements ExportProfilePanel.GraphSelectionView {

  /**
   * Creates new form GraphSelection
   */
  public GraphSelection(java.awt.Frame parent, boolean modal, DataView myview) {
    super(parent, modal);
    this.myview = myview;
    initComponents();
    setLocationRelativeTo(parent);
    toFront();
  }

  /**
   * This method is called from within the constructor to
   * initialize the form.
   */
  @SuppressWarnings("unchecked")
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    jPanel3 = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    selectallButton = new javax.swing.JButton();
    unselectallButton = new javax.swing.JButton();
    jSeparator2 = new javax.swing.JSeparator();
    cancelButton = new javax.swing.JButton();
    okButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    jPanel1.setLayout(new java.awt.BorderLayout());

    jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 400));
    jScrollPane1.setRequestFocusEnabled(false);

    jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.PAGE_AXIS));
    jScrollPane1.setViewportView(jPanel3);
    ScrollSpeed.apply(jScrollPane1);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

    getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

    getContentPane().add(exportSettings, java.awt.BorderLayout.NORTH);

    selectallButton.setText(tr("graphselection.selectall"));
    selectallButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        selectallButtonActionPerformed(evt);
      }
    });
    jPanel2.add(selectallButton);

    unselectallButton.setText(tr("graphselection.unselectall"));
    unselectallButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        unselectallButtonActionPerformed(evt);
      }
    });
    jPanel2.add(unselectallButton);

    jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jPanel2.add(jSeparator2);

    cancelButton.setText(tr("common.cancel"));
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelButtonActionPerformed(evt);
      }
    });
    jPanel2.add(cancelButton);

    okButton.setText(tr("common.ok"));
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okButtonActionPerformed(evt);
      }
    });
    jPanel2.add(okButton);

    getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

    pack();
  }

  private void cancelButtonActionPerformed(
      java.awt.event.ActionEvent evt) {
    this.dispose();
  }

  private void okButtonActionPerformed(
      java.awt.event.ActionEvent evt) {
    exportSettings.applyToConfig();
    this.dispose();
    OkforExport = true;
  }

  private void selectallButtonActionPerformed(
      java.awt.event.ActionEvent evt) {
    toggle_checkbox(jPanel3, true);
  }

  private void unselectallButtonActionPerformed(
      java.awt.event.ActionEvent evt) {
    toggle_checkbox(jPanel3, false);
  }

  public void toggle_checkbox(JPanel panel, boolean checked) {
    Component[] list = panel.getComponents();
    for (int i = 0; i < list.length; i++) {
      Component tmp = list[i];
      if (tmp instanceof JPanel) {
        JPanel obj = (JPanel) tmp;
        toggle_checkbox(obj, checked);
      }
      if (tmp instanceof JCheckBox) {
        JCheckBox obj = (JCheckBox) tmp;
        obj.setSelected(checked);
      }
    }
  }

  @Override
  public void setVisible(boolean visible) {
    if (visible) {
      exportSettings.applyRememberedSelection();
    }
    super.setVisible(visible);
  }

  @Override
  public List<String> excludedGraphTitles() {
    List<String> excluded = new ArrayList<>();
    forEachCheckBox(jPanel3, box -> {
      if (!box.isSelected()) {
        excluded.add(box.getText());
      }
    });
    return excluded;
  }

  @Override
  public void applyExcludedGraphs(Collection<String> excluded) {
    forEachCheckBox(jPanel3, box -> box.setSelected(!excluded.contains(box.getText())));
  }

  private static void forEachCheckBox(Container container, Consumer<JCheckBox> action) {
    for (Component component : container.getComponents()) {
      if (component instanceof JCheckBox) {
        action.accept((JCheckBox) component);
      } else if (component instanceof Container) {
        forEachCheckBox((Container) component, action);
      }
    }
  }

  public void addPrintCheckBox(JCheckBox tmp) {
    jPanel3.add(tmp);
    jPanel1.validate();
  }

  public void addPrintCheckBox(JPanel tmp) {
    jPanel3.add(tmp);
    jPanel1.validate();
  }

  private javax.swing.JButton cancelButton;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSeparator jSeparator2;
  private javax.swing.JButton okButton;
  private javax.swing.JButton selectallButton;
  private javax.swing.JButton unselectallButton;
  private final ExportProfilePanel exportSettings = new ExportProfilePanel(this);
  private DataView myview = null;
  public boolean OkforExport = false;
}
