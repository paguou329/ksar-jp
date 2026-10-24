package net.atomique.ksar;

import java.util.List;

/**
 * A named set of export settings: JPG/PNG size, PDF page format, PDF font and the graphs left out
 * of the export.
 *
 * @param name          profile name; also the name of the file it is stored in
 * @param imageWidth    width of exported JPG/PNG images
 * @param imageHeight   height of exported JPG/PNG images
 * @param pdfPageFormat PDF page format such as {@code A4}
 * @param pdfFontFile   font file for the PDF export, empty for the built-in font
 * @param excludedGraphs titles of the graphs that are not exported; every other graph, including
 *                      graphs the profile has never seen, is exported
 */
public record ExportProfile(
    String name, int imageWidth, int imageHeight, String pdfPageFormat, String pdfFontFile,
    List<String> excludedGraphs) {

  public ExportProfile {
    excludedGraphs = List.copyOf(excludedGraphs);
  }

  /**
   * Creates a profile that leaves no graph out.
   */
  public ExportProfile(
      String name, int imageWidth, int imageHeight, String pdfPageFormat, String pdfFontFile) {
    this(name, imageWidth, imageHeight, pdfPageFormat, pdfFontFile, List.of());
  }
}
