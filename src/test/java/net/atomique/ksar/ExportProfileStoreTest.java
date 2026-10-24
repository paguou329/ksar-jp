/*
 * Copyright 2018 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.Arrays;

public class ExportProfileStoreTest {

  @TempDir
  File dir;

  @Test
  public void savedProfileCanBeLoaded() {
    ExportProfileStore store = new ExportProfileStore(new File(dir, "profiles"));
    ExportProfile profile = new ExportProfile("報告用", 1024, 768, "LETTER", "/fonts/gothic.ttf");

    assertTrue(store.save(profile));

    assertEquals(profile, store.load("報告用"));
  }

  @Test
  public void namesAreListedAlphabetically() {
    ExportProfileStore store = new ExportProfileStore(dir);
    store.save(new ExportProfile("b", 1, 1, "A4", ""));
    store.save(new ExportProfile("a", 1, 1, "A4", ""));

    assertEquals(Arrays.asList("a", "b"), store.names());
  }

  @Test
  public void deleteRemovesTheProfile() {
    ExportProfileStore store = new ExportProfileStore(dir);
    store.save(new ExportProfile("a", 1, 1, "A4", ""));

    assertTrue(store.delete("a"));

    assertNull(store.load("a"));
    assertFalse(store.delete("a"));
  }

  @Test
  public void invalidNamesAreRejected() {
    ExportProfileStore store = new ExportProfileStore(dir);

    for (String name : new String[] {null, "", " ", "a/b", "..", ".hidden", "a:b", " a", "x".repeat(65)}) {
      assertFalse(ExportProfileStore.isValidName(name), String.valueOf(name));
      assertFalse(name != null && store.save(new ExportProfile(name, 1, 1, "A4", "")));
    }
    assertTrue(store.names().isEmpty());
  }

  @Test
  public void missingDirectoryHasNoProfiles() {
    ExportProfileStore store = new ExportProfileStore(new File(dir, "none"));

    assertTrue(store.names().isEmpty());
    assertNull(store.load("a"));
  }

  @Test
  public void excludedGraphsAreStored() {
    ExportProfileStore store = new ExportProfileStore(dir);
    ExportProfile profile = new ExportProfile("g", 1, 1, "A4", "",
        Arrays.asList("CPU all", "ネットワーク eth0, eth1", "Swap"));

    store.save(profile);

    assertEquals(profile, store.load("g"));
    assertEquals(3, store.load("g").excludedGraphs().size());
  }

  @Test
  public void profileWithoutExcludedGraphsExportsEverything() {
    ExportProfileStore store = new ExportProfileStore(dir);
    store.save(new ExportProfile("all", 1, 1, "A4", ""));

    assertTrue(store.load("all").excludedGraphs().isEmpty());
  }
}
