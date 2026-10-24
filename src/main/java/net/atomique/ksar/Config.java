/*
 * Copyright 2017 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.UIManager;

/**
 * The user settings. They are kept in {@code ksar.properties} in the current directory (or in the
 * directory named by the {@code ksar.config.dir} system property), export profiles in the
 * {@code ksar-profiles} directory next to it.
 */
public class Config {

  private static final Logger log = LoggerFactory.getLogger(GlobalOptions.class);

  private static final String CONFIG_FILE_NAME = "ksar.properties";
  private static final String PROFILE_DIRECTORY_NAME = "ksar-profiles";

  // The fields below must be declared before "instance": creating it loads the settings into
  // them, and a later initializer would overwrite what was loaded.
  private static String landf;
  private static File lastReadDirectory;
  private static File lastExportDirectory;
  private static String lastCommand;
  private static int local_configfile = -1;
  private static ArrayList<String> host_history = new ArrayList<>();
  private static final Font DEFAULT_FONT = new Font("SansSerif", Font.BOLD, 18);

  private static String LinuxDateFormat;
  private static String PDFPageFormat;
  private static String PDFFontFile = "";
  private static String exportProfileName = "";
  private static int ImageWidth;
  private static int ImageHeight;

  private static Config instance = new Config();

  static Config getInstance() {
    return instance;
  }

  private Config() {

    log.trace("load Config");

    boolean known = load(getConfigFile());
    if (!known || local_configfile == -1) {
      // first start
      local_configfile = store_configdir();
      save();
    }
  }

  /**
   * Returns the directory holding the settings file and the profile directory.
   */
  static File getConfigDirectory() {
    String dir = System.getProperty("ksar.config.dir");
    return new File(dir != null && !dir.isEmpty() ? dir : System.getProperty("user.dir"));
  }

  static File getConfigFile() {
    return new File(getConfigDirectory(), CONFIG_FILE_NAME);
  }

  /**
   * Loads the settings from {@code file}; settings that are not stored get their default value.
   *
   * @return whether the file existed and could be read
   */
  static boolean load(File file) {
    Properties props = new Properties();
    boolean read = false;
    if (file.isFile()) {
      try (Reader in = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
        props.load(in);
        read = true;
      } catch (IOException | IllegalArgumentException e) {
        log.error("Unable to read {}", file, e);
      }
    }

    setLandf(props.getProperty("landf", UIManager.getLookAndFeel().getName()));
    setLastReadDirectory(props.getProperty("lastReadDirectory"));
    setLastExportDirectory(props.getProperty("lastExportDirectory"));

    setImageHeight(getInt(props, "ImageHeight", 600));
    setImageWidth(getInt(props, "ImageWidth", 800));
    setPDFPageFormat(props.getProperty("PDFPageFormat", "A4"));
    setPDFFontFile(props.getProperty("PDFFontFile", ""));
    setExportProfileName(props.getProperty("ExportProfile", ""));
    setLinuxDateFormat(props.getProperty("LinuxDateFormat", "Always ask"));

    host_history.clear();
    int count = getInt(props, "HostHistory", 0);
    for (int i = 0; i < count; i++) {
      String host = props.getProperty("HostHistory_" + i);
      if (host != null) {
        host_history.add(host);
      }
    }
    setLocal_configfile(getInt(props, "local_configfile", -1));
    return read;
  }

  public static void save() {
    saveTo(getConfigFile());
  }

  static void saveTo(File file) {
    Properties props = new Properties();
    props.setProperty("landf", landf);
    if (lastReadDirectory != null) {
      props.setProperty("lastReadDirectory", lastReadDirectory.toString());
    }
    if (lastExportDirectory != null) {
      props.setProperty("lastExportDirectory", lastExportDirectory.toString());
    }

    props.setProperty("ImageHeight", Integer.toString(ImageHeight));
    props.setProperty("ImageWidth", Integer.toString(ImageWidth));
    props.setProperty("PDFPageFormat", PDFPageFormat);
    props.setProperty("PDFFontFile", PDFFontFile);
    props.setProperty("ExportProfile", exportProfileName);
    props.setProperty("LinuxDateFormat", LinuxDateFormat);

    for (int i = 0; i < host_history.size(); i++) {
      props.setProperty("HostHistory_" + i, host_history.get(i));
    }
    props.setProperty("HostHistory", Integer.toString(host_history.size()));

    props.setProperty("local_configfile", Integer.toString(local_configfile));

    try {
      File parent = file.getAbsoluteFile().getParentFile();
      if (parent != null) {
        Files.createDirectories(parent.toPath());
      }
      try (Writer out = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
        props.store(out, "kSar settings");
      }
    } catch (IOException e) {
      log.error("Unable to write {}", file, e);
    }
  }

  private static int getInt(Properties props, String key, int defaultValue) {
    try {
      return Integer.parseInt(props.getProperty(key, "").trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public static String getLandf() {
    return landf;
  }

  public static void setLandf(String landf) {
    Config.landf = landf;
  }

  static File getLastReadDirectory() {
    return lastReadDirectory;
  }

  private static void setLastReadDirectory(String lastReadDirectory) {
    if (lastReadDirectory != null) {
      Config.lastReadDirectory = new File(lastReadDirectory);
    }
  }

  static void setLastReadDirectory(File lastReadDirectory) {
    Config.lastReadDirectory = lastReadDirectory;
  }

  public static File getLastExportDirectory() {
    return lastExportDirectory;
  }

  public static void setLastExportDirectory(String lastExportDirectory) {
    if (lastExportDirectory != null) {
      Config.lastExportDirectory = new File(lastExportDirectory);
    }
  }

  public static void setLastExportDirectory(File lastExportDirectory) {
    Config.lastExportDirectory = lastExportDirectory;
  }

  public static String getLastCommand() {
    return lastCommand;
  }

  public static void setLastCommand(String lastCommand) {
    Config.lastCommand = lastCommand;
  }

  public static ArrayList<String> getHost_history() {
    return host_history;
  }

  public static void addHost_history(String e) {
    host_history.add(e);
  }

  public static Font getDEFAULT_FONT() {
    return DEFAULT_FONT;
  }

  public static int getImageHeight() {
    return ImageHeight;
  }

  public static void setImageHeight(int ImageHeight) {
    Config.ImageHeight = ImageHeight;
  }

  public static int getImageWidth() {
    return ImageWidth;
  }

  public static void setImageWidth(int ImageWidth) {
    Config.ImageWidth = ImageWidth;
  }

  /**
   * Returns the font file (TTF/OTF/TTC) used for PDF export, or an empty string for the built-in
   * font.
   */
  public static String getPDFFontFile() {
    return PDFFontFile;
  }

  public static void setPDFFontFile(String PDFFontFile) {
    Config.PDFFontFile = PDFFontFile == null ? "" : PDFFontFile.trim();
  }

  public static String getPDFPageFormat() {
    return PDFPageFormat;
  }

  public static void setPDFPageFormat(String PDFPageFormat) {
    Config.PDFPageFormat = PDFPageFormat;
  }

  /**
   * Returns the store of the export profiles.
   */
  public static ExportProfileStore getExportProfileStore() {
    return new ExportProfileStore(new File(getConfigDirectory(), PROFILE_DIRECTORY_NAME));
  }

  /**
   * Returns the name of the export profile last chosen, or an empty string for none.
   */
  public static String getExportProfileName() {
    return exportProfileName;
  }

  public static void setExportProfileName(String exportProfileName) {
    Config.exportProfileName = exportProfileName == null ? "" : exportProfileName;
  }

  /**
   * Makes the settings of {@code profile} the current export settings.
   */
  public static void applyExportProfile(ExportProfile profile) {
    setImageWidth(profile.imageWidth());
    setImageHeight(profile.imageHeight());
    setPDFPageFormat(profile.pdfPageFormat());
    setPDFFontFile(profile.pdfFontFile());
    setExportProfileName(profile.name());
  }

  private static int store_configdir() {
    String userhome = System.getProperty("user.home") + File.separator;
    // mkdir userhome/.ksarcfg
    String buffer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n\n<ConfiG>\n</ConfiG>\n";
    boolean home = new File(userhome + ".ksarcfg").mkdir();
    if (!home) {
      return 0;
    }

    BufferedWriter out;
    try {
      out =
          new BufferedWriter(new FileWriter(userhome + ".ksarcfg" + File.separator + "Config.xml"));
      out.write(buffer);
      out.flush();
      out.close();
      return 1;
    } catch (IOException e) {
      return 0;
    }

  }

  public static int getLocal_configfile() {
    return local_configfile;
  }

  private static void setLocal_configfile(int local_configfile) {
    Config.local_configfile = local_configfile;
  }

  public static String getLinuxDateFormat() {
    return LinuxDateFormat;
  }

  public static void setLinuxDateFormat(String LinuxDateFormat) {
    Config.LinuxDateFormat = LinuxDateFormat;
  }

}
