package org.kku.common.util;

import org.kku.common.conf.Language;
import org.kku.common.conf.LanguageConfiguration;
import org.kku.common.util.AppProperties.AppProperty;
import org.kku.common.util.Converters.Converter;

public class AppPreferences
{
  public final static AppProperty<Language> languagePreference;

  static
  {
    languagePreference = createPreference("Language", Converters.getLanguageConverter(),
        LanguageConfiguration.getInstance().getDefault());
  }

  protected AppPreferences()
  {
  }

  static public <T> AppProperty<T> createPreference(String name, T defaultValue)
  {
    return createPreference(name, getDefaultConverter(defaultValue), defaultValue);
  }

  private static <T> Converter<T> getDefaultConverter(T defaultValue)
  {
    if (defaultValue instanceof Boolean)
    {
      return (Converter<T>) Converters.getBooleanConverter();
    }

    return null;
  }

  static public <T> AppProperty<T> createPreference(String name, Converter<T> converter, T defaultValue)
  {
    return AppProperties.get(Project.getInstance().getName() + ".preferences").createAppPropertyType(name, converter)
        .forSubject(AppPreferences.class, defaultValue);
  }
}
