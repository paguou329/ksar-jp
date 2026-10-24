/*
 * Copyright 2018 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

public class ConfigFileTest {

  @TempDir
  File dir;

  @Test
  public void settingsSurviveSaveAndLoad() {
    File file = new File(dir, "ksar.properties");
    File backup = new File(dir, "backup.properties");
    Config.saveTo(backup);
    try {
      Config.setImageWidth(1234);
      Config.setImageHeight(567);
      Config.setPDFPageFormat("LEGAL");
      Config.setPDFFontFile("/fonts/ゴシック.ttf");
      Config.setExportProfileName("報告用");
      Config.setLinuxDateFormat("YYYY年MM月DD日 12時59分59秒");
      Config.saveTo(file);

      Config.setImageWidth(1);
      Config.setPDFFontFile("");
      Config.setExportProfileName("");
      assertTrue(Config.load(file));

      assertEquals(1234, Config.getImageWidth());
      assertEquals(567, Config.getImageHeight());
      assertEquals("LEGAL", Config.getPDFPageFormat());
      assertEquals("/fonts/ゴシック.ttf", Config.getPDFFontFile());
      assertEquals("報告用", Config.getExportProfileName());
      assertEquals("YYYY年MM月DD日 12時59分59秒", Config.getLinuxDateFormat());
    } finally {
      Config.load(backup);
    }
  }

  @Test
  public void missingFileGivesDefaults() {
    File backup = new File(dir, "backup.properties");
    Config.saveTo(backup);
    try {
      assertFalse(Config.load(new File(dir, "absent.properties")));

      assertEquals(800, Config.getImageWidth());
      assertEquals(600, Config.getImageHeight());
      assertEquals("A4", Config.getPDFPageFormat());
      assertEquals("", Config.getPDFFontFile());
      assertEquals("Always ask", Config.getLinuxDateFormat());
    } finally {
      Config.load(backup);
    }
  }

  @Test
  public void applyingAProfileChangesTheExportSettings() {
    File backup = new File(dir, "backup.properties");
    Config.saveTo(backup);
    try {
      Config.applyExportProfile(new ExportProfile("p", 320, 240, "LETTER", "/f.ttf"));

      assertEquals(320, Config.getImageWidth());
      assertEquals(240, Config.getImageHeight());
      assertEquals("LETTER", Config.getPDFPageFormat());
      assertEquals("/f.ttf", Config.getPDFFontFile());
      assertEquals("p", Config.getExportProfileName());
    } finally {
      Config.load(backup);
    }
  }
}
