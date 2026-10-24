/*
 * Copyright 2018 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar.ui;

import static net.atomique.ksar.I18n.tr;

import net.atomique.ksar.Config;
import net.atomique.ksar.GlobalOptions;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Preferences extends javax.swing.JDialog {

  /**
   * Creates new form Preferences
   */
  public Preferences(java.awt.Frame parent) {
    super(parent);
    initComponents();
    load_landf();
    load_pageformat();
    load_linuxformat();
    setLocationRelativeTo(parent);
    setModal(true);
    setVisible(true);
  }

  private void load_landf() {
    UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
    for (int i = 0, n = looks.length; i < n; i++) {
      String tmp = looks[i].getName();
      UI_lanf_model.addElement(tmp);
      if (Config.getLandf().equals(tmp)) {
        jComboBox1.setSelectedItem(tmp);
      }
    }
  }

  private void load_linuxformat() {
    LinuxFormatComboModel = LinuxFormatItem.createModel(true);
    jComboBox3.setModel(LinuxFormatComboModel);

    LinuxFormatItem.select(LinuxFormatComboModel, Config.getLinuxDateFormat());
  }

  private void load_pageformat() {
    PageFormatComboBox.addElement("A4");
    PageFormatComboBox.addElement("LETTER");
    PageFormatComboBox.addElement("LEGAL");
    jComboBox2.setSelectedItem(Config.getPDFPageFormat());

  }

  /**
   * This method is called from within the constructor to
   * initialize the form.
   */
  @SuppressWarnings("unchecked")
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    jPanel4 = new javax.swing.JPanel();
    jPanel5 = new javax.swing.JPanel();
    jLabel1 = new javax.swing.JLabel();
    jComboBox1 = new javax.swing.JComboBox();
    jPanel10 = new javax.swing.JPanel();
    jLabel7 = new javax.swing.JLabel();
    jComboBox3 = new javax.swing.JComboBox();
    jPanel6 = new javax.swing.JPanel();
    jLabel2 = new javax.swing.JLabel();
    jPanel8 = new javax.swing.JPanel();
    jLabel5 = new javax.swing.JLabel();
    jPanel7 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    jSpinner1 = new javax.swing.JSpinner();
    jLabel4 = new javax.swing.JLabel();
    jSpinner2 = new javax.swing.JSpinner();
    jPanel9 = new javax.swing.JPanel();
    jPanelFont = new javax.swing.JPanel();
    jLabelFont = new javax.swing.JLabel();
    jLabel6 = new javax.swing.JLabel();
    jComboBox2 = new javax.swing.JComboBox();
    jPanel3 = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    OkButton = new javax.swing.JButton();
    CancelButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    jPanel1.setPreferredSize(new java.awt.Dimension(430, 430));
    jPanel1.setLayout(new java.awt.BorderLayout());

    jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.PAGE_AXIS));

    jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    jLabel1.setText(tr("pref.laf"));
    jPanel5.add(jLabel1);

    jComboBox1.setModel(UI_lanf_model);
    jComboBox1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jComboBox1ActionPerformed(evt);
      }
    });
    jPanel5.add(jComboBox1);

    jPanel4.add(jPanel5);

    jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    jLabel7.setLabelFor(jComboBox3);
    jLabel7.setText(tr("pref.linuxformat"));
    jPanel10.add(jLabel7);

    jComboBox3.setModel(LinuxFormatComboModel);
    jPanel10.add(jComboBox3);

    jPanel4.add(jPanel10);

    jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

    jLabel2.setText(tr("pref.language"));
    jPanel6.add(jLabel2);

    jPanel4.add(jPanel6);


    jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    jLabel5.setText("JPG/PNG");
    jPanel8.add(jLabel5);

    jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    jLabel3.setText(tr("pref.width"));
    jPanel7.add(jLabel3);

    jSpinner1.setModel(imageWidthSpinner);
    jSpinner1.setMinimumSize(new java.awt.Dimension(80, 28));
    jSpinner1.setPreferredSize(new java.awt.Dimension(80, 28));
    jSpinner1.setValue(800);
    jPanel7.add(jSpinner1);

    jLabel4.setText(tr("pref.height"));
    jPanel7.add(jLabel4);

    jSpinner2.setModel(imageHeightSpinner);
    jSpinner2.setMinimumSize(new java.awt.Dimension(80, 28));
    jSpinner2.setPreferredSize(new java.awt.Dimension(80, 28));
    jPanel7.add(jSpinner2);

    jPanel8.add(jPanel7);

    jPanel4.add(jPanel8);

    jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    jLabel6.setText(tr("pref.pdfpage"));
    jPanel9.add(jLabel6);

    jComboBox2.setModel(PageFormatComboBox);
    jComboBox2.setMinimumSize(new java.awt.Dimension(100, 27));
    jComboBox2.setPreferredSize(new java.awt.Dimension(100, 27));
    jPanel9.add(jComboBox2);

    jPanel4.add(jPanel9);

    jPanelFont.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    jLabelFont.setLabelFor(pdfFontField);
    jLabelFont.setText(tr("pref.pdffont"));
    jPanelFont.add(jLabelFont);

    pdfFontField.setText(Config.getPDFFontFile());
    pdfFontField.setToolTipText(tr("pref.pdffont.hint"));
    jPanelFont.add(pdfFontField);

    pdfFontBrowseButton.setText(tr("pref.pdffont.browse"));
    pdfFontBrowseButton.addActionListener(evt -> browsePdfFont());
    jPanelFont.add(pdfFontBrowseButton);

    jPanel4.add(jPanelFont);

    jScrollPane1.setViewportView(jPanel4);
    ScrollSpeed.apply(jScrollPane1);

    jPanel1.add(jScrollPane1, java.awt.BorderLayout.NORTH);

    jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

    OkButton.setText(tr("common.ok"));
    OkButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        OkButtonActionPerformed(evt);
      }
    });
    jPanel2.add(OkButton);

    CancelButton.setText(tr("common.cancel"));
    CancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        CancelButtonActionPerformed(evt);
      }
    });
    jPanel2.add(CancelButton);

    jPanel3.add(jPanel2);

    jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

    getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

    pack();
  }

  private void jComboBox1ActionPerformed(
      java.awt.event.ActionEvent evt) {
    Object source = evt.getSource();
    String lafClassName = null;
    if (source == jComboBox1) {
      JComboBox comboBox = (JComboBox) source;
      lafClassName = (String) comboBox.getSelectedItem();
      if (lafClassName != null) {
        //String finalLafClassName = lafClassName;
        for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
          if (lafClassName.equals(laf.getName())) {
            try {
              UIManager.setLookAndFeel(laf.getClassName());
              SwingUtilities.updateComponentTreeUI(GlobalOptions.getUI());
            } catch (Exception e) {
            }

          }
        }
      }
    }

  }

  private void OkButtonActionPerformed(
      java.awt.event.ActionEvent evt) {
    Config.setLandf(jComboBox1.getSelectedItem().toString());
    Config.setPDFPageFormat(jComboBox2.getSelectedItem().toString());
    Config.setPDFFontFile(pdfFontField.getText());
    Config.setImageHeight((Integer) jSpinner2.getModel().getValue());
    Config.setImageWidth((Integer) jSpinner1.getModel().getValue());
    Config.setLinuxDateFormat(LinuxFormatItem.selectedValue(LinuxFormatComboModel));
    Config.save();
    dispose();
  }

  private void browsePdfFont() {
    String chosen = ExportProfilePanel.chooseFontFile(this, pdfFontField.getText());
    if (chosen != null) {
      pdfFontField.setText(chosen);
    }
  }

  private void CancelButtonActionPerformed(
      java.awt.event.ActionEvent evt) {
    dispose();
  }

  private javax.swing.JButton CancelButton;
  private javax.swing.JButton OkButton;
  private javax.swing.JComboBox jComboBox1;
  private javax.swing.JComboBox jComboBox2;
  private javax.swing.JComboBox jComboBox3;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel4;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel10;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JPanel jPanel6;
  private javax.swing.JPanel jPanel7;
  private javax.swing.JPanel jPanel8;
  private javax.swing.JPanel jPanel9;
  private javax.swing.JPanel jPanelFont;
  private javax.swing.JLabel jLabelFont;
  private final javax.swing.JTextField pdfFontField = new javax.swing.JTextField(18);
  private final javax.swing.JButton pdfFontBrowseButton = new javax.swing.JButton();
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JSpinner jSpinner1;
  private javax.swing.JSpinner jSpinner2;
  DefaultComboBoxModel<String> UI_lanf_model = new DefaultComboBoxModel<>();
  DefaultComboBoxModel<String> PageFormatComboBox = new DefaultComboBoxModel<>();
  DefaultComboBoxModel<LinuxFormatItem> LinuxFormatComboModel = new DefaultComboBoxModel<>();
  SpinnerNumberModel imageWidthSpinner =
      new javax.swing.SpinnerNumberModel(Config.getImageWidth(), 1, null,1 );
  SpinnerNumberModel imageHeightSpinner =
      new javax.swing.SpinnerNumberModel(Config.getImageHeight(), 1, null,1);
}
