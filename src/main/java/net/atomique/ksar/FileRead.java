/*
 * Copyright 2017 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar;

import static net.atomique.ksar.I18n.tr;

import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFileChooser;

public class FileRead extends Thread {

  private static final org.slf4j.Logger log = LoggerFactory.getLogger(FileRead.class);

  public FileRead(kSar hissar) {
    mysar = hissar;
    JFileChooser fc = new JFileChooser();
    fc.setMultiSelectionEnabled(true);
    if (Config.getLastReadDirectory() != null) {
      fc.setCurrentDirectory(Config.getLastReadDirectory());
    }
    int returnVal = fc.showDialog(GlobalOptions.getUI(), tr("file.open"));
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      File[] selected = fc.getSelectedFiles();
      if (selected.length == 0) {
        return;
      }
      sarfilenames = sortedPaths(selected);
      if (selected[0].isDirectory()) {
        Config.setLastReadDirectory(selected[0]);
      } else {
        Config.setLastReadDirectory(selected[0].getParentFile());
      }
      Config.save();
    }
  }

  /**
   * Returns the paths of the files ordered by name, so that daily sar files (sa01, sa02, ...) are
   * read in order whatever order the file chooser returns them in.
   */
  static List<String> sortedPaths(File[] files) {
    return Arrays.stream(files)
        .sorted(Comparator.comparing(File::getName))
        .map(File::getAbsolutePath)
        .collect(Collectors.toList());
  }

  public FileRead(kSar hissar, String filename) {
    this(hissar, List.of(filename));
  }

  /**
   * Reads the given files one after the other into the same data.
   */
  FileRead(kSar hissar, List<String> filenames) {
    mysar = hissar;
    sarfilenames = new ArrayList<>(filenames);
  }

  public String get_action() {
    if (sarfilenames.isEmpty()) {
      return null;
    }
    return sarfilenames.stream().map(name -> "file://" + name).collect(Collectors.joining(","));
  }

  public void run() {
    for (String name : sarfilenames) {
      if (mysar.isInterrupted()) {
        break;
      }
      log.debug("Reading {}", name);
      try (BufferedReader reader = new BufferedReader(new FileReader(name))) {
        mysar.parseStream(reader);
      } catch (IOException ex) {
        log.error("IO Exception", ex);
      }
    }
    mysar.finishParsing();
  }

  private kSar mysar = null;
  private List<String> sarfilenames = new ArrayList<>();
}
