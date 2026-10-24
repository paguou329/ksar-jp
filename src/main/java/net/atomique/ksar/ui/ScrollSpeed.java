package net.atomique.ksar.ui;

import javax.swing.JScrollPane;

/**
 * Makes the mouse wheel scroll a useful distance.
 *
 * <p>The distance of one wheel notch is the unit increment of the scroll bar times the system's
 * wheel setting (usually 3). A view that is not {@code Scrollable}, such as a plain panel, gets
 * an increment of one pixel, so the wheel hardly moves it at all.
 */
final class ScrollSpeed {

  /** Pixels per unit; with the usual 3 units per notch the view moves 72 pixels. */
  static final int UNIT_INCREMENT = 24;

  private ScrollSpeed() {
  }

  /**
   * Sets the vertical unit increment of {@code scrollPane} and returns it for chaining.
   */
  static JScrollPane apply(JScrollPane scrollPane) {
    scrollPane.getVerticalScrollBar().setUnitIncrement(UNIT_INCREMENT);
    return scrollPane;
  }
}
