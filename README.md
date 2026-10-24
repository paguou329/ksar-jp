ksar-jp
=======

[日本語版 README](README.ja.md)

ksar-jp is a sar graphing tool that can graph Linux, macOS and Solaris sar output. The statistics
graphs can be exported to a PDF file.

**ksar-jp is a fork of [vlsi/ksar](https://github.com/vlsi/ksar)**, which is itself a fork of
[kSar on SourceForge](https://sourceforge.net/projects/ksar/) by Atomique.net. It adds a Japanese
user interface and support for sar files written in Japanese, and extends the export and loading
features. See [What ksar-jp adds](#what-ksar-jp-adds) for the details.

Repository: <https://github.com/paguou329/ksar-jp>

Version
-------

The version is the version of the original kSar that ksar-jp is based on, followed by `-jp`. For
example `6.1.0-jp` is based on kSar 6.1.0. Development builds carry a `-SNAPSHOT` suffix.

Quick Start
-----------

Prerequisite:

- Java 17 or later

Build the jar (see [Building from source](#building-from-source)) and start it:

```
$ java -jar build/libs/ksar-<version>-all.jar
```

What ksar-jp adds
-----------------

Compared with the original project, ksar-jp adds the following.

### Internationalization

- All menus, dialogs and messages are read from the resource bundles
  `src/main/resources/net/atomique/ksar/Language/Message*.properties`. The English text is the
  default and `Message_ja.properties` is the Japanese translation.
- The language follows the default locale of the JVM. Use `-Duser.language=en` or
  `-Duser.language=ja` to choose it explicitly. Another language can be added by adding a
  `Message_<language>.properties` file.
- Labels such as "Automatic Detection" are translated for display only; the values saved in the
  settings stay language independent, so existing settings keep working.
- The text written into PDF and CSV files is not translated.

### sar files in Japanese

- Dates such as `2026年10月02日` and times such as `13時05分09秒` are understood, also when a
  file mixes them with the usual `13:05:09` notation (the notation is decided per line).
- A Japanese date format has been added to the list of date formats.
- When the date format chosen in the settings does not fit the header of a file, the format is
  detected automatically instead of failing every line of the file.
- Lines that sysstat prints in Japanese for the average and the summary are skipped.

### Loading several files at once

- "Load from file(s)..." accepts a multiple selection. The files are read in the order of their
  names and appended to the same data, which is handy for daily files such as `sa01`, `sa02`, ...
- A file that cannot be read is skipped and the stop button also stops the remaining files.

### Export

- **PDF font**: a TrueType/OpenType/TrueType Collection font file can be chosen for the PDF
  export, so that Japanese text can be written. The font is embedded in the PDF. Without a font
  the built-in one is used.
- **Export profiles**: the export dialog has an "Export Settings" section where the settings can be
  saved under a name and chosen again. A profile contains
  - the width and height of exported JPG/PNG images,
  - the PDF page format,
  - the PDF font file, and
  - which graphs are left out of the export.

  Graphs that a profile has never seen (for example another host with more CPUs) are exported.

### Settings

- The settings are stored in `ksar.properties` in the current directory instead of the
  platform's preference store (the registry on Windows). Export profiles are stored in the
  `ksar-profiles` directory next to it, one `<name>.properties` file each. Both are UTF-8 text
  files that can be copied and edited. Use `-Dksar.config.dir=<directory>` to keep them elsewhere.
- Settings saved by the original kSar in the preference store are not imported.
- The settings that were loaded at start-up were overwritten by their defaults, so the PDF font
  and the host history were forgotten after a restart. This is fixed.

### User interface

- The mouse wheel scrolls a useful distance in the tree, the graph selection, the host
  information and the preferences. It was a few pixels per notch in some of them.
- The "Stop" button no longer makes the following loads stop immediately.
- The About dialog shows ksar-jp, its repository and the acknowledgements.

Building from source
--------------------

Prerequisite:

- JDK 17 or later

The following command would build and launch ksar-jp from sources:

```
$ ./gradlew runShadow
```

or

```
$ ./gradlew shadowJar
$ java -jar build/libs/ksar-<version>-all.jar
```

The tests are run with `./gradlew test`.

Acknowledgements
----------------

ksar-jp would not exist without the people who created and maintained kSar. Our heartfelt thanks
go to

- **Atomique.net**, who created kSar and published it under the BSD license,
- [vlsi/ksar](https://github.com/vlsi/ksar), which keeps kSar alive and up to date, and
- every developer listed in [CONTRIBUTORS.md](CONTRIBUTORS.md).

License
-------

ksar-jp is distributed under the BSD 3-Clause license of the original project. See
[LICENSE](LICENSE).
