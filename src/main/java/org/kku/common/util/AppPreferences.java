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

  static public <T> void register(Class<T> clazz, Converter<T> converter)
  {
    AppProperties.register(clazz, converter);
  }

  static public <T> AppProperty<T> createPreference(String name, T defaultValue)
  {
    return createPreference(name, null, defaultValue);
  }

  static public <T> AppProperty<T> createPreference(String name, Converter<T> converter, T defaultValue)
  {
    return AppProperties.get(Project.getInstance().getName() + ".preferences")
        .createAppPropertyType(name, converter, defaultValue).forSubject(AppPreferences.class, defaultValue);
  }
}
