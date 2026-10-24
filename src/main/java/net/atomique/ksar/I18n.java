package net.atomique.ksar;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Looks up user-visible strings in the {@code Message} resource bundle.
 *
 * <p>The bundle is resolved against the JVM default locale, so a Japanese environment gets
 * {@code Message_ja.properties} and every other environment falls back to the English
 * {@code Message.properties}.
 */
public final class I18n {

  private static final ResourceBundle BUNDLE =
      ResourceBundle.getBundle("net/atomique/ksar/Language/Message");

  private I18n() {
  }

  /**
   * Returns the localized text for {@code key}.
   */
  public static String tr(String key) {
    return BUNDLE.getString(key);
  }

  /**
   * Returns the localized text for {@code key} with {@link MessageFormat} placeholders
   * ({@code {0}}, {@code {1}}, ...) replaced by {@code args}.
   */
  public static String tr(String key, Object... args) {
    return MessageFormat.format(BUNDLE.getString(key), args);
  }
}
