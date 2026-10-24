package net.atomique.ksar.ui;

import static net.atomique.ksar.I18n.tr;

import javax.swing.DefaultComboBoxModel;

/**
 * An entry of the "Linux date format" combo box.
 *
 * <p>The {@link #getValue() value} is what gets stored in the preferences and compared by the
 * parser, so it must stay language independent. Only the {@link #toString() label} shown to the
 * user is localized.
 */
final class LinuxFormatItem {

  static final String AUTOMATIC_DETECTION = "Automatic Detection";
  static final String ALWAYS_ASK = "Always ask";

  private final String value;
  private final String label;

  private LinuxFormatItem(String value, String label) {
    this.value = value;
    this.label = label;
  }

  String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return label;
  }

  /**
   * Builds the list of selectable formats.
   *
   * @param withAlwaysAsk whether to offer "Always ask" (only meaningful in the preferences)
   */
  static DefaultComboBoxModel<LinuxFormatItem> createModel(boolean withAlwaysAsk) {
    DefaultComboBoxModel<LinuxFormatItem> model = new DefaultComboBoxModel<>();
    model.addElement(new LinuxFormatItem(AUTOMATIC_DETECTION, tr("linuxformat.automatic")));
    if (withAlwaysAsk) {
      model.addElement(new LinuxFormatItem(ALWAYS_ASK, tr("linuxformat.ask")));
    }
    model.addElement(plain("MM/DD/YY 23:59:59"));
    model.addElement(plain("MM/DD/YYYY 23:59:59"));
    model.addElement(plain("DD/MM/YY 23:59:59"));
    model.addElement(plain("DD/MM/YYYY 23:59:59"));
    model.addElement(plain("YYYY-MM-DD 23:59:59"));
    model.addElement(plain("MM/DD/YY 12:59:59 AM|PM"));
    model.addElement(plain("MM/DD/YYYY 12:59:59 AM|PM"));
    model.addElement(plain("DD/MM/YY 12:59:59 AM|PM"));
    model.addElement(plain("DD/MM/YYYY 12:59:59 AM|PM"));
    model.addElement(plain("YYYY-MM-DD 12:59:59 AM|PM"));
    model.addElement(plain("YYYY年MM月DD日 12時59分59秒"));
    return model;
  }

  /**
   * Selects the entry whose stored value equals {@code value}; leaves the selection untouched
   * when there is none.
   */
  static void select(DefaultComboBoxModel<LinuxFormatItem> model, String value) {
    for (int i = 0; i < model.getSize(); i++) {
      LinuxFormatItem item = model.getElementAt(i);
      if (item.value.equals(value)) {
        model.setSelectedItem(item);
        return;
      }
    }
  }

  /**
   * Returns the stored value of the selected entry.
   */
  static String selectedValue(DefaultComboBoxModel<LinuxFormatItem> model) {
    return ((LinuxFormatItem) model.getSelectedItem()).value;
  }

  private static LinuxFormatItem plain(String format) {
    return new LinuxFormatItem(format, format);
  }
}
