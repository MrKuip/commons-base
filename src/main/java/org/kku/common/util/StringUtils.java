package org.kku.common.util;

/**
 * A utility class providing various string manipulation and validation methods.
 */
public class StringUtils
{
  /**
   * Checks if the specified string is not null and not blank (contains at least one non-whitespace character).
   *
   * @param text the string to check
   * @return true if the string is not null and not blank, false otherwise
   */
  static public boolean isNotBlank(String text)
  {
    return text != null && !text.isBlank();
  }

  /**
   * Determines if the specified string consists entirely of letters.
   *
   * @param type the string to check
   * @return true if all characters in the string are letters, false otherwise
   */
  public static boolean isAllLetters(String type)
  {
    int length = type.length();

    for (int i = 0; i < length; i++)
    {
      if (!(Character.isLetter(type.charAt(i))))
      {
        return false;
      }
    }

    return true;
  }

  /**
   * Checks if the specified string is null or empty (contains no characters).
   *
   * @param string the string to check
   * @return true if the string is null or empty, false otherwise
   */
  public static boolean isEmpty(String string)
  {
    return string == null || string.isEmpty();
  }

  /**
   * Truncates the specified string to the given maximum length, appending "..." if the string is longer than the specified length.
   * If the string is null, an empty string is returned.
   *
   * @param label the string to truncate
   * @param maxLength the maximum length of the resulting string (including the "..." if appended)
   * @return the truncated string, or an empty string if the input is null
   */
  public static String truncate(String label, int maxLength)
  {
    if (label == null)
    {
      return "";
    }
    if (label.length() <= maxLength)
    {
      return label;
    }
    return label.substring(0, maxLength - 3) + "...";
  }

  public static String indent(int depth)
  {
    return " ".repeat(depth);
  }
}