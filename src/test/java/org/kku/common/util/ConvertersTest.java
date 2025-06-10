package org.kku.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.kku.common.conf.Language;
import org.kku.common.conf.LanguageConfiguration;
import org.kku.common.util.preferences.SizeSystem;

class ConvertersTest
{
  @Test
  void testDoubleConverter()
  {
    double value;
    String stringValue;

    value = 100.0;
    stringValue = "100.0";

    assertEquals(value, Converters.getDoubleConverter().fromString(stringValue));
    assertEquals(stringValue, Converters.getDoubleConverter().toString(value));
  }

  @Test
  void testIntegerConverter()
  {
    int value;
    String stringValue;

    value = 100;
    stringValue = "100";

    assertEquals(value, Converters.getIntegerConverter().fromString(stringValue));
    assertEquals(stringValue, Converters.getIntegerConverter().toString(value));
  }

  @Test
  void testLongConverter()
  {
    long value;
    String stringValue;

    value = 100l;
    stringValue = "100";

    assertEquals(value, Converters.getLongConverter().fromString(stringValue));
    assertEquals(stringValue, Converters.getLongConverter().toString(value));
  }

  @Test
  void testStringConverter()
  {
    String value;
    String stringValue;

    value = "100.0";
    stringValue = "100.0";

    assertEquals(value, Converters.getStringConverter().fromString(stringValue));
    assertEquals(stringValue, Converters.getStringConverter().toString(value));
  }

  @Test
  void testPathConverter()
  {
    Path value;
    String stringValue;

    value = Path.of("dir", "file");
    stringValue = "dir" + File.separator + "file";

    assertEquals(value, Converters.getPathConverter().fromString(stringValue));
    assertEquals(stringValue, Converters.getPathConverter().toString(value));
  }

  @Test
  void testEnumConverter()
  {
    SizeSystem value;
    String stringValue;

    value = SizeSystem.BINARY;
    stringValue = "BINARY";

    assertEquals(value, Converters.getEnumConverter(SizeSystem.class).fromString(stringValue));
    assertEquals(stringValue, Converters.getEnumConverter(SizeSystem.class).toString(value));
  }

  @Test
  void testLanguageConverter()
  {
    Language value;
    String stringValue;

    value = LanguageConfiguration.getInstance().getDefault();
    stringValue = value.getName();

    assertEquals(value, Converters.getLanguageConverter().fromString(stringValue));
    assertEquals(stringValue, Converters.getLanguageConverter().toString(value));
  }

  @Test
  void testBooleanConverter()
  {
    Boolean value;
    String stringValue;

    value = Boolean.TRUE;
    stringValue = "true";

    assertEquals(value, Converters.getBooleanConverter().fromString(stringValue));
    assertEquals(stringValue, Converters.getBooleanConverter().toString(value));
  }
}
