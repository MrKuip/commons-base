package org.kku.common.util;

import org.kku.common.util.AppProperties.AppPropertyType;
import org.kku.common.util.Converters.Converter;

public class AppSettings
{
  private final static String SETTINGS_FILE_NAME = "JDiskUsage.settings";

  public static final AppPropertyType<Double> WIDTH;
  public static final AppPropertyType<Double> HEIGHT;
  public static final AppPropertyType<Double> X;
  public static final AppPropertyType<Double> Y;
  public static final AppPropertyType<String> SELECTED_ID;

  static
  {
    WIDTH = createAppPropertyType("WIDTH", Converters.getDoubleConverter());
    HEIGHT = createAppPropertyType("HEIGHT", Converters.getDoubleConverter());
    X = createAppPropertyType("X", Converters.getDoubleConverter());
    Y = createAppPropertyType("Y", Converters.getDoubleConverter());
    SELECTED_ID = createAppPropertyType("SELECTED_ID", Converters.getStringConverter());
  }

  protected AppSettings()
  {
  }

  static public <T> AppPropertyType<T> createAppPropertyType(String name, Converter<T> converter)
  {
    return AppProperties.get(SETTINGS_FILE_NAME).createAppPropertyType(name, converter);
  }
}
