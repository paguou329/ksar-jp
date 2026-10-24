package net.atomique.ksar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Keeps {@link ExportProfile export profiles} as one {@code <name>.properties} file each in a
 * directory.
 */
public class ExportProfileStore {

  private static final Logger log = LoggerFactory.getLogger(ExportProfileStore.class);

  private static final String EXTENSION = ".properties";
  private static final String ILLEGAL_CHARACTERS = "\\/:*?\"<>|";
  private static final int MAX_NAME_LENGTH = 64;

  private final File directory;

  public ExportProfileStore(File directory) {
    this.directory = directory;
  }

  /**
   * Tells whether {@code name} can be used as a profile name, i.e. as a file name.
   */
  public static boolean isValidName(String name) {
    if (name == null || name.isBlank() || name.length() > MAX_NAME_LENGTH
        || !name.equals(name.trim()) || name.startsWith(".")) {
      return false;
    }
    for (char c : name.toCharArray()) {
      if (c < ' ' || ILLEGAL_CHARACTERS.indexOf(c) >= 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the names of all stored profiles in alphabetical order.
   */
  public List<String> names() {
    List<String> names = new ArrayList<>();
    File[] files = directory.listFiles((dir, fileName) -> fileName.endsWith(EXTENSION));
    if (files != null) {
      for (File file : files) {
        String fileName = file.getName();
        String name = fileName.substring(0, fileName.length() - EXTENSION.length());
        if (isValidName(name)) {
          names.add(name);
        }
      }
    }
    Collections.sort(names);
    return names;
  }

  /**
   * Reads the profile called {@code name}, or returns {@code null} if it is missing or
   * unreadable. Values that are missing in the file fall back to the current settings.
   */
  public ExportProfile load(String name) {
    if (!isValidName(name)) {
      return null;
    }
    File file = fileOf(name);
    if (!file.isFile()) {
      return null;
    }
    Properties props = new Properties();
    try (Reader in = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
      props.load(in);
    } catch (IOException | IllegalArgumentException e) {
      log.error("Unable to read export profile {}", file, e);
      return null;
    }
    return new ExportProfile(name,
        positiveInt(props.getProperty("imageWidth"), Config.getImageWidth()),
        positiveInt(props.getProperty("imageHeight"), Config.getImageHeight()),
        props.getProperty("pdfPageFormat", Config.getPDFPageFormat()),
        props.getProperty("pdfFontFile", ""),
        readExcludedGraphs(props));
  }

  /**
   * Writes {@code profile}, replacing a stored profile of the same name.
   *
   * @return whether the profile was written
   */
  public boolean save(ExportProfile profile) {
    if (!isValidName(profile.name())) {
      return false;
    }
    Properties props = new Properties();
    props.setProperty("imageWidth", Integer.toString(profile.imageWidth()));
    props.setProperty("imageHeight", Integer.toString(profile.imageHeight()));
    props.setProperty("pdfPageFormat", profile.pdfPageFormat());
    props.setProperty("pdfFontFile", profile.pdfFontFile());
    props.setProperty("excludedGraphs", Integer.toString(profile.excludedGraphs().size()));
    for (int i = 0; i < profile.excludedGraphs().size(); i++) {
      props.setProperty("excludedGraph." + i, profile.excludedGraphs().get(i));
    }
    File file = fileOf(profile.name());
    try {
      Files.createDirectories(directory.toPath());
      try (Writer out = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
        props.store(out, "kSar export profile");
      }
      return true;
    } catch (IOException e) {
      log.error("Unable to write export profile {}", file, e);
      return false;
    }
  }

  /**
   * Removes the profile called {@code name}.
   *
   * @return whether a profile was removed
   */
  public boolean delete(String name) {
    return isValidName(name) && fileOf(name).delete();
  }

  private File fileOf(String name) {
    return new File(directory, name + EXTENSION);
  }

  private static List<String> readExcludedGraphs(Properties props) {
    List<String> titles = new ArrayList<>();
    int count = positiveInt(props.getProperty("excludedGraphs"), 0);
    for (int i = 0; i < count; i++) {
      String title = props.getProperty("excludedGraph." + i);
      if (title != null) {
        titles.add(title);
      }
    }
    return titles;
  }

  private static int positiveInt(String value, int fallback) {
    try {
      int parsed = Integer.parseInt(value.trim());
      return parsed > 0 ? parsed : fallback;
    } catch (NumberFormatException | NullPointerException e) {
      return fallback;
    }
  }
}
