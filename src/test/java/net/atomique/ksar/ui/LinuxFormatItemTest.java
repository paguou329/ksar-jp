/*
 * Copyright 2018 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import javax.swing.DefaultComboBoxModel;

public class LinuxFormatItemTest {

  @Test
  public void alwaysAskIsOnlyOfferedWhenRequested() {
    assertEquals(
        LinuxFormatItem.createModel(false).getSize() + 1,
        LinuxFormatItem.createModel(true).getSize());
  }

  @Test
  public void selectionReturnsTheStoredValueNotTheLabel() {
    DefaultComboBoxModel<LinuxFormatItem> model = LinuxFormatItem.createModel(true);

    LinuxFormatItem.select(model, LinuxFormatItem.AUTOMATIC_DETECTION);
    assertEquals("Automatic Detection", LinuxFormatItem.selectedValue(model));

    LinuxFormatItem.select(model, LinuxFormatItem.ALWAYS_ASK);
    assertEquals("Always ask", LinuxFormatItem.selectedValue(model));
  }

  @Test
  public void japaneseDateFormatIsSelectable() {
    DefaultComboBoxModel<LinuxFormatItem> model = LinuxFormatItem.createModel(false);

    LinuxFormatItem.select(model, "YYYY年MM月DD日 12時59分59秒");

    assertEquals("YYYY年MM月DD日 12時59分59秒", LinuxFormatItem.selectedValue(model));
  }

  @Test
  public void unknownValueKeepsTheCurrentSelection() {
    DefaultComboBoxModel<LinuxFormatItem> model = LinuxFormatItem.createModel(true);
    LinuxFormatItem.select(model, "DD/MM/YYYY 23:59:59");

    LinuxFormatItem.select(model, "no such format");

    assertEquals("DD/MM/YYYY 23:59:59", LinuxFormatItem.selectedValue(model));
    assertNotNull(model.getSelectedItem());
  }
}
