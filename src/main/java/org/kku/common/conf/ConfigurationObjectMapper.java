package org.kku.common.conf;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.json.JsonMapper.Builder;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ConfigurationObjectMapper
{
  static JsonMapper createMapper()
  {
    Builder builder;
    JsonMapper mapper;

    builder = JsonMapper.builder();
    builder.enable(SerializationFeature.INDENT_OUTPUT);

    builder.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    builder.enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION);

    builder.disable(MapperFeature.AUTO_DETECT_CREATORS);
    builder.enable(MapperFeature.AUTO_DETECT_FIELDS);
    builder.disable(MapperFeature.AUTO_DETECT_GETTERS);
    builder.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
    builder.disable(MapperFeature.AUTO_DETECT_SETTERS);

    mapper = builder.build();
    mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
    mapper.setPropertyNamingStrategy(createNamingStrategy());
    mapper.registerModule(getConfigurationModule());

    return mapper;
  }

  /**
   * Function that converts a fieldName to the name in the json structure.
   * 
   * In the source code EVERY field is prefixed with m_ (for every inner class level an 'i' is inserted between the 'm' and the '_'.
   * 
   * @return the converted field name
   */
  static private Function<String, String> convertFieldName()
  {
    Pattern p;
    Matcher m;

    p = Pattern.compile("m[i]*_(.*)");
    m = p.matcher("");

    return fieldName -> {

      m.reset(fieldName);
      if (m.matches())
      {
        return m.group(1);
      }

      return fieldName;
    };
  }

  /**
   * Create a naming strategy that converts the field name according the method {@link #convertFieldName()}
   * 
   * @return a property naming strategy
   */
  private static PropertyNamingStrategy createNamingStrategy()
  {
    return new PropertyNamingStrategy()
    {
      private static final long serialVersionUID = 1L;

      private Function<String, String> mi_convertFieldName = convertFieldName();

      @Override
      public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName)
      {
        return mi_convertFieldName.apply(field.getName());
      }
    };
  }

  private static Module getConfigurationModule()
  {
    SimpleModule module;

    module = new SimpleModule("Configuration");

    return module;
  }
}
