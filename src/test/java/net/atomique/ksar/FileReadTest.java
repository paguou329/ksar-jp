/*
 * Copyright 2018 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class FileReadTest {

  @TempDir
  File dir;

  private static final String HEADER =
      "Linux 4.18.0-553.el8_10.x86_64 (host) \t%s \t_x86_64_\t(4 CPU)\n\n";
  private static final String STAT =
      "00:00:01        CPU      %%usr     %%nice      %%sys   %%iowait    %%steal      %%irq     %%soft    %%guest    %%gnice     %%idle\n"
      + "%s        all      1.91      0.00      0.85      3.63      0.00      0.07      0.07      0.00      0.00     93.48\n";

  private String write(String name, String date, String time) throws IOException {
    File file = new File(dir, name);
    Files.writeString(file.toPath(),
        String.format(HEADER, date) + String.format(STAT, time), StandardCharsets.UTF_8);
    return file.getAbsolutePath();
  }

  @Test
  public void filesAreSortedByName() {
    File[] files = {new File(dir, "sa03"), new File(dir, "sa01"), new File(dir, "sa02")};

    List<String> sorted = FileRead.sortedPaths(files);

    assertEquals(
        Arrays.asList(
            new File(dir, "sa01").getAbsolutePath(),
            new File(dir, "sa02").getAbsolutePath(),
            new File(dir, "sa03").getAbsolutePath()),
        sorted);
  }

  @Test
  public void severalFilesAreAppendedToTheSameData() throws IOException {
    Config.setLinuxDateFormat("Automatic Detection");
    String first = write("sa01", "2026-10-01", "00:01:01");
    String second = write("sa02", "2026-10-02", "23:59:01");
    kSar ksar = new kSar();

    new FileRead(ksar, Arrays.asList(first, second)).run();

    assertEquals(LocalDateTime.of(2026, 10, 1, 0, 0, 1), ksar.myparser.getStartOfGraph());
    assertEquals(LocalDateTime.of(2026, 10, 2, 23, 59, 1), ksar.myparser.getEndOfGraph());
  }

  @Test
  public void missingFileIsSkipped() throws IOException {
    Config.setLinuxDateFormat("Automatic Detection");
    String existing = write("sa01", "2026-10-01", "00:01:01");
    kSar ksar = new kSar();

    new FileRead(ksar, Arrays.asList(new File(dir, "absent").getAbsolutePath(), existing)).run();

    assertEquals(LocalDateTime.of(2026, 10, 1, 0, 0, 1), ksar.myparser.getStartOfGraph());
  }
}
