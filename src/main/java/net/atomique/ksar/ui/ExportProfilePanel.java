package net.atomique.ksar.ui;

import static net.atomique.ksar.I18n.tr;

import net.atomique.ksar.Config;
import net.atomique.ksar.ExportProfile;
import net.atomique.ksar.ExportProfileStore;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.Collection;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * The export settings (image size, PDF page format and PDF font) together with the export
 * profiles they can be saved as. Used by the export dialog.
 */
class ExportProfilePanel extends JPanel {

  /**
   * The graphs offered for export, as far as the profiles are concerned.
   */
  interface GraphSelectionView {

    /**
     * Returns the titles of the graphs that are currently not selected for export.
     */
    List<String> excludedGraphTitles();

    /**
     * Selects every graph except those named in {@code excluded}.
     */
    void applyExcludedGraphs(Collection<String> excluded);
  }

  private final JComboBox<String> profileCombo = new JComboBox<>();
  private final SpinnerNumberModel widthModel =
      new SpinnerNumberModel(Config.getImageWidth(), 1, null, 1);
  private final SpinnerNumberModel heightModel =
      new SpinnerNumberModel(Config.getImageHeight(), 1, null, 1);
  private final JComboBox<String> pageFormatCombo =
      new JComboBox<>(new String[] {"A4", "LETTER", "LEGAL"});
  private final JTextField fontField = new JTextField(Config.getPDFFontFile(), 18);
  private final GraphSelectionView graphSelection;
  private boolean loadingProfiles = false;

  ExportProfilePanel(GraphSelectionView graphSelection) {
    this.graphSelection = graphSelection;
    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    setBorder(BorderFactory.createTitledBorder(tr("export.settings")));

    JPanel profileRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel profileLabel = new JLabel(tr("pref.profile"));
    profileLabel.setLabelFor(profileCombo);
    profileRow.add(profileLabel);
    profileCombo.setPreferredSize(new Dimension(140, 27));
    profileCombo.addActionListener(evt -> profileSelected());
    profileRow.add(profileCombo);
    JButton saveButton = new JButton(tr("pref.profile.save"));
    saveButton.addActionListener(evt -> saveProfile());
    profileRow.add(saveButton);
    JButton deleteButton = new JButton(tr("pref.profile.delete"));
    deleteButton.addActionListener(evt -> deleteProfile());
    profileRow.add(deleteButton);
    add(profileRow);

    JPanel imageRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    imageRow.add(new JLabel(tr("pref.image")));
    imageRow.add(new JLabel(tr("pref.width")));
    imageRow.add(sizeSpinner(widthModel));
    imageRow.add(new JLabel(tr("pref.height")));
    imageRow.add(sizeSpinner(heightModel));
    add(imageRow);

    JPanel pageRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    pageRow.add(new JLabel(tr("pref.pdfpage")));
    pageFormatCombo.setSelectedItem(Config.getPDFPageFormat());
    pageRow.add(pageFormatCombo);
    add(pageRow);

    JPanel fontRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel fontLabel = new JLabel(tr("pref.pdffont"));
    fontLabel.setLabelFor(fontField);
    fontRow.add(fontLabel);
    fontField.setToolTipText(tr("pref.pdffont.hint"));
    fontRow.add(fontField);
    JButton browseButton = new JButton(tr("pref.pdffont.browse"));
    browseButton.addActionListener(evt -> {
      String chosen = chooseFontFile(this, fontField.getText());
      if (chosen != null) {
        fontField.setText(chosen);
      }
    });
    fontRow.add(browseButton);
    add(fontRow);

    loadProfiles();
  }

  private static JSpinner sizeSpinner(SpinnerNumberModel model) {
    JSpinner spinner = new JSpinner(model);
    spinner.setPreferredSize(new Dimension(80, 28));
    return spinner;
  }

  /**
   * Lets the user pick a font file.
   *
   * @return the chosen path, or {@code null} if the dialog was cancelled
   */
  static String chooseFontFile(Component parent, String current) {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle(tr("pref.pdffont.choose"));
    chooser.setFileFilter(new FileNameExtensionFilter(tr("pref.pdffont.filter"), "ttf", "otf", "ttc"));
    if (current != null && !current.trim().isEmpty()) {
      chooser.setSelectedFile(new File(current.trim()));
    }
    if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
      return chooser.getSelectedFile().getAbsolutePath();
    }
    return null;
  }

  private void loadProfiles() {
    loadingProfiles = true;
    profileCombo.removeAllItems();
    profileCombo.addItem(tr("pref.profile.none"));
    for (String name : Config.getExportProfileStore().names()) {
      profileCombo.addItem(name);
    }
    profileCombo.setSelectedItem(Config.getExportProfileName());
    loadingProfiles = false;
  }

  private String selectedProfileName() {
    return profileCombo.getSelectedIndex() > 0 ? (String) profileCombo.getSelectedItem() : "";
  }

  private ExportProfile currentSettings(String name) {
    return new ExportProfile(name,
        widthModel.getNumber().intValue(),
        heightModel.getNumber().intValue(),
        pageFormatCombo.getSelectedItem().toString(),
        fontField.getText().trim(),
        graphSelection.excludedGraphTitles());
  }

  /**
   * Applies the graph selection of the remembered profile, if there is one. Called when the
   * dialog opens, after the graphs have been added to it.
   */
  void applyRememberedSelection() {
    String name = selectedProfileName();
    ExportProfile profile = name.isEmpty() ? null : Config.getExportProfileStore().load(name);
    if (profile != null) {
      graphSelection.applyExcludedGraphs(profile.excludedGraphs());
    }
  }

  /**
   * Copies the settings of the chosen profile into the form.
   */
  private void profileSelected() {
    String name = selectedProfileName();
    if (loadingProfiles || name.isEmpty()) {
      return;
    }
    ExportProfile profile = Config.getExportProfileStore().load(name);
    if (profile == null) {
      showError(name);
      return;
    }
    widthModel.setValue(profile.imageWidth());
    heightModel.setValue(profile.imageHeight());
    pageFormatCombo.setSelectedItem(profile.pdfPageFormat());
    fontField.setText(profile.pdfFontFile());
    graphSelection.applyExcludedGraphs(profile.excludedGraphs());
  }

  /**
   * Stores the settings shown in the form as a profile.
   */
  private void saveProfile() {
    String current = selectedProfileName();
    String input = JOptionPane.showInputDialog(this, tr("pref.profile.name.prompt"), current);
    if (input == null) {
      return;
    }
    String name = input.trim();
    ExportProfileStore store = Config.getExportProfileStore();
    if (!ExportProfileStore.isValidName(name)) {
      JOptionPane.showMessageDialog(this, tr("pref.profile.invalid"), tr("pref.profile.title"),
          JOptionPane.ERROR_MESSAGE);
      return;
    }
    if (!name.equals(current) && store.names().contains(name)
        && JOptionPane.showConfirmDialog(this, tr("pref.profile.overwrite", name),
            tr("pref.profile.title"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
      return;
    }
    if (!store.save(currentSettings(name))) {
      showError(name);
      return;
    }
    loadProfiles();
    profileCombo.setSelectedItem(name);
  }

  private void deleteProfile() {
    String name = selectedProfileName();
    if (name.isEmpty()
        || JOptionPane.showConfirmDialog(this, tr("pref.profile.delete.confirm", name),
            tr("pref.profile.title"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
      return;
    }
    Config.getExportProfileStore().delete(name);
    loadProfiles();
  }

  private void showError(String name) {
    JOptionPane.showMessageDialog(this, tr("pref.profile.error", name), tr("pref.profile.title"),
        JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Makes the settings in the form the current export settings and remembers them.
   */
  void applyToConfig() {
    Config.applyExportProfile(currentSettings(selectedProfileName()));
    Config.save();
  }
}
