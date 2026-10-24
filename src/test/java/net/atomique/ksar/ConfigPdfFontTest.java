/*
 * Copyright 2018 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ConfigPdfFontTest {

  @Test
  public void pdfFontFileIsTrimmed() {
    Config.setPDFFontFile("  /fonts/msgothic.ttc  ");

    assertEquals("/fonts/msgothic.ttc", Config.getPDFFontFile());
  }

  @Test
  public void nullPdfFontFileMeansBuiltInFont() {
    Config.setPDFFontFile(null);

    assertEquals("", Config.getPDFFontFile());
  }
}
