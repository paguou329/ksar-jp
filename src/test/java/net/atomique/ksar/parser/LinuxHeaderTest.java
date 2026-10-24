/*
 * Copyright 2018 The kSAR Project. All rights reserved.
 * See the LICENSE file in the project root for more information.
 */

package net.atomique.ksar.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import net.atomique.ksar.Config;
import net.atomique.ksar.kSar;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class LinuxHeaderTest {
  @ParameterizedTest
  @MethodSource("testValues")
  public void test(String header, String sarString, LocalDateTime expectedDate) {
    String[] columns = sarString.split("\\s+");
    kSar ksar = new kSar();
    Config.setLinuxDateFormat("Automatic Detection");
    Linux sut = new Linux();
    sut.init(ksar, header);
    sut.parse(sarString, columns);
    assertEquals(expectedDate, sut.getStartOfGraph(), () -> "header: " + header + ", sar string: " + sarString);
  }

  public static Stream<Arguments> testValues() {
    String str = "2016-03-28 09:10:01";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);

    return Stream.of(
        arguments("Linux 3.10.0-327.el7.x86_64 (hostname.example.com)  03/28/16  _x86_64_  (48 CPU)",
            "09:10:01          6      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00    100.00",
            dateTime),
        arguments("Linux 3.10.0-327.el7.x86_64 (hostname.example.com)  03/28/2016  _x86_64_  (48 CPU)",
            "09:10:01          6      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00    100.00",
            dateTime),
        arguments("Linux 3.10.0-327.el7.x86_64 (hostname.example.com)  20160328  _x86_64_  (48 CPU)",
            "09:10:01          6      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00    100.00",
            dateTime),
        arguments("Linux 3.10.0-327.el7.x86_64 (hostname.example.com)  2016-03-28  _x86_64_  (48 CPU)",
            "09:10:01          6      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00    100.00",
            dateTime),
        arguments("Linux 3.10.0-327.el7.x86_64 (hostname.example.com)  03/28/16  _x86_64_  (48 CPU)",
            "09:10:01    AM      6      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00    100.00",
            dateTime),
        arguments("Linux 3.10.0-327.el7.x86_64 (hostname.example.com)  03/28/2016  _x86_64_  (48 CPU)",
            "09:10:01    AM      6      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00    100.00",
            dateTime),
        arguments("Linux 3.10.0-327.el7.x86_64 (hostname.example.com)  20160328  _x86_64_  (48 CPU)",
            "09:10:01     AM     6      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00    100.00",
            dateTime),
        arguments("Linux 3.10.0-327.el7.x86_64 (hostname.example.com)  2016-03-28  _x86_64_  (48 CPU)",
            "09:10:01    AM      6      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00    100.00",
            dateTime));
  }

  @Test
  public void reDetectsDateFormatForEachHeader() {
    Config.setLinuxDateFormat("Automatic Detection");
    Linux sut = new Linux();

    String stat =
        "09:10:01          6      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00    100.00";

    // 03/04/16 is ambiguous; the latest non-future reading is dd/MM/yy -> 2016-04-03.
    sut.init(new kSar(), "Linux 3.10.0-327.el7.x86_64 (host)  03/04/16  _x86_64_  (48 CPU)");
    sut.parse(stat, stat.split("\\s+"));

    // 12/31/23 only parses as MM/dd/yy -> 2023-12-31. A formatter cached from the first
    // header (dd/MM/yy) would reject it, so the format must be detected again per header.
    sut.parse_header("Linux 3.10.0-327.el7.x86_64 (host)  12/31/23  _x86_64_  (48 CPU)");
    sut.parse(stat, stat.split("\\s+"));

    assertEquals(LocalDateTime.of(2016, 4, 3, 9, 10, 1), sut.getStartOfGraph());
    assertEquals(LocalDateTime.of(2023, 12, 31, 9, 10, 1), sut.getEndOfGraph());
  }

  @Test
  public void parsesJapaneseDateAndTimeWithAutomaticDetection() {
    Config.setLinuxDateFormat("Automatic Detection");
    Linux sut = new Linux();
    String stat =
        "09時10分01秒          6      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00    100.00";

    sut.init(new kSar(), "Linux 3.10.0-327.el7.x86_64 (host)  2016年03月28日  _x86_64_  (48 CPU)");
    sut.parse(stat, stat.split("\\s+"));

    assertEquals(LocalDateTime.of(2016, 3, 28, 9, 10, 1), sut.getStartOfGraph());
  }

  @Test
  public void parsesJapaneseDateAndTimeWithExplicitFormat() {
    Config.setLinuxDateFormat("YYYY年MM月DD日 12時59分59秒");
    Linux sut = new Linux();
    String stat =
        "09時10分01秒          6      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00      0.00    100.00";

    sut.init(new kSar(), "Linux 3.10.0-327.el7.x86_64 (host)  2016年03月28日  _x86_64_  (48 CPU)");
    sut.parse(stat, stat.split("\\s+"));

    assertEquals(LocalDateTime.of(2016, 3, 28, 9, 10, 1), sut.getStartOfGraph());
  }

  @Test
  public void parsesColonTimeWithJapaneseDateAndExplicitFormat() {
    Config.setLinuxDateFormat("YYYY年MM月DD日 12時59分59秒");
    Linux sut = new Linux();
    String stat =
        "23:59:01          1     21.04      0.00      0.00      0.00      0.00";

    sut.init(new kSar(), "Linux 3.10.0-327.el7.x86_64 (host)  2016年03月28日  _x86_64_  (48 CPU)");
    sut.parse(stat, stat.split("\\s+"));

    assertEquals(LocalDateTime.of(2016, 3, 28, 23, 59, 1), sut.getStartOfGraph());
  }

  @Test
  public void parsesColonTimeWithJapaneseDateAfterJapaneseTime() {
    Config.setLinuxDateFormat("Automatic Detection");
    Linux sut = new Linux();
    String jaTime = "09時10分01秒          6      0.00      0.00";
    String colonTime = "23:59:01          6      0.00      0.00";

    sut.init(new kSar(), "Linux 3.10.0-327.el7.x86_64 (host)  2016年03月28日  _x86_64_  (48 CPU)");
    sut.parse(jaTime, jaTime.split("\\s+"));
    sut.parse(colonTime, colonTime.split("\\s+"));

    assertEquals(LocalDateTime.of(2016, 3, 28, 9, 10, 1), sut.getStartOfGraph());
    assertEquals(LocalDateTime.of(2016, 3, 28, 23, 59, 1), sut.getEndOfGraph());
  }

  @Test
  public void fallsBackToAutomaticDetectionWhenExplicitDateFormatDoesNotMatch() {
    Config.setLinuxDateFormat("YYYY年MM月DD日 12時59分59秒");
    Linux sut = new Linux();
    String stat = "18:17:01          3     11.07      0.00      0.00      0.00      0.00";

    sut.init(new kSar(), "Linux 4.18.0-553.169.1.el8_10.x86_64 (host) 	2026-10-02 	_x86_64_	(4 CPU)");
    sut.parse(stat, stat.split("\s+"));

    assertEquals(LocalDateTime.of(2026, 10, 2, 18, 17, 1), sut.getStartOfGraph());
  }

}
