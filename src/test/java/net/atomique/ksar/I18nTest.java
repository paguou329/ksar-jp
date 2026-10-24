/*
 * Copyright 2018 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nTest {

  private static final String BASE = "net/atomique/ksar/Language/Message";

  @Test
  public void japaneseBundleDefinesEveryKeyOfTheDefaultBundle() {
    ResourceBundle en = ResourceBundle.getBundle(BASE, Locale.ROOT);
    ResourceBundle ja = ResourceBundle.getBundle(BASE, Locale.JAPAN);

    assertEquals(en.keySet(), ja.keySet());
    for (String key : en.keySet()) {
      assertTrue(!ja.getString(key).isEmpty(), () -> "empty Japanese text for " + key);
    }
  }

  @Test
  public void japaneseBundleIsTranslated() {
    ResourceBundle en = ResourceBundle.getBundle(BASE, Locale.ROOT);
    ResourceBundle ja = ResourceBundle.getBundle(BASE, Locale.JAPAN);

    assertEquals("File", en.getString("menu.file"));
    assertEquals("ファイル", ja.getString("menu.file"));
    assertNotEquals(en.getString("menu.options"), ja.getString("menu.options"));
  }

  @Test
  public void aboutBoxNamesKsarJpAndThanksTheDevelopers() {
    for (Locale locale : new Locale[] {Locale.ROOT, Locale.JAPAN}) {
      ResourceBundle bundle = ResourceBundle.getBundle(BASE, locale);

      assertTrue(bundle.getString("about.version").contains("ksar-jp"), "version " + locale);
      assertTrue(bundle.getString("about.thanks").contains("Atomique.net"), "thanks " + locale);
    }
  }

  @Test
  public void trReplacesPlaceholders() {
    String text = I18n.tr("file.overwrite.message", "report.pdf");

    assertTrue(text.contains("report.pdf"), text);
  }

  @Test
  public void trReturnsPlainTextWithoutArguments() {
    assertEquals(
        ResourceBundle.getBundle(BASE).getString("common.ok"), I18n.tr("common.ok"));
  }
}
