/*
 * Copyright 2018 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.atomique.ksar.Config;
import net.atomique.ksar.ExportProfile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.List;

public class ExportProfilePanelTest {

  /** A graph selection that remembers what the panel applied to it. */
  private static class StubSelection implements ExportProfilePanel.GraphSelectionView {
    List<String> excluded = new ArrayList<>();

    @Override
    public List<String> excludedGraphTitles() {
      return excluded;
    }

    @Override
    public void applyExcludedGraphs(Collection<String> titles) {
      excluded = new ArrayList<>(titles);
    }
  }

  @Test
  public void appliedSettingsMatchTheCurrentOnesWhenNothingWasChanged() {
    Config.setImageWidth(640);
    Config.setImageHeight(480);
    Config.setPDFPageFormat("LEGAL");
    Config.setPDFFontFile("/fonts/gothic.ttf");
    Config.setExportProfileName("");

    new ExportProfilePanel(new StubSelection()).applyToConfig();

    assertEquals(640, Config.getImageWidth());
    assertEquals(480, Config.getImageHeight());
    assertEquals("LEGAL", Config.getPDFPageFormat());
    assertEquals("/fonts/gothic.ttf", Config.getPDFFontFile());
    assertEquals("", Config.getExportProfileName());
  }

  @Test
  public void rememberedProfileSelectsItsGraphsWhenTheDialogOpens() {
    String name = "panel-test";
    Config.getExportProfileStore()
        .save(new ExportProfile(name, 100, 100, "A4", "", Arrays.asList("CPU 1", "Swap")));
    try {
      Config.setExportProfileName(name);
      StubSelection selection = new StubSelection();

      new ExportProfilePanel(selection).applyRememberedSelection();

      assertEquals(Arrays.asList("CPU 1", "Swap"), selection.excluded);
    } finally {
      Config.getExportProfileStore().delete(name);
      Config.setExportProfileName("");
    }
  }

  @Test
  public void withoutAProfileTheGraphSelectionIsLeftAlone() {
    Config.setExportProfileName("");
    StubSelection selection = new StubSelection();
    selection.excluded = new ArrayList<>(Arrays.asList("Memory"));

    new ExportProfilePanel(selection).applyRememberedSelection();

    assertEquals(Arrays.asList("Memory"), selection.excluded);
  }
}
