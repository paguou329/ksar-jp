/*
 * Copyright 2018 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ScrollSpeedTest {

  @Test
  public void plainPanelScrollsMoreThanOnePixelPerUnit() {
    JScrollPane pane = new JScrollPane(new JPanel());
    assertEquals(1, pane.getVerticalScrollBar().getUnitIncrement(1));

    ScrollSpeed.apply(pane);

    assertEquals(ScrollSpeed.UNIT_INCREMENT, pane.getVerticalScrollBar().getUnitIncrement(1));
    assertEquals(ScrollSpeed.UNIT_INCREMENT, pane.getVerticalScrollBar().getUnitIncrement(-1));
  }

  @Test
  public void applyReturnsTheSameScrollPane() {
    JScrollPane pane = new JScrollPane();

    assertSame(pane, ScrollSpeed.apply(pane));
  }
}
