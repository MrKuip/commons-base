package org.kku.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kku.common.util.Converters.Converter;

public class AppProperties
{
  private static final Map<String, AppProperties> m_propertiesByFileName = new HashMap<>();
  private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

  private final PropertyStore m_propertyStore;

  private AppProperties(String propertyFileName)
  {
    m_propertyStore = new PropertyStore(propertyFileName);
  }

  public static AppProperties get(Class<?> clazz)
  {
    return get(clazz.getSimpleName() + ".properties");
  }

  public static AppProperties get(String propertyFileName)
  {
    return m_propertiesByFileName.computeIfAbsent(propertyFileName, name -> new AppProperties(name));
  }

  public PropertyStore getStore()
  {
    return m_propertyStore;
  }

  public <T> AppPropertyType<T> createAppPropertyType(String name, Converter<T> converter)
  {
    return new AppPropertyType<>(name, converter);
  }

  public class AppPropertyType<T>
  {
    private final String mi_name;
    private final Converter<T> mi_converter;

    private AppPropertyType(String name, Converter<T> converter)
    {
      mi_name = name;
      mi_converter = converter;
    }

    public AppProperty<T> forSubject(Object subject)
    {
      return forSubject(subject, null);
    }

    public AppProperty<T> forSubject(Object subject, T defaultValue)
    {
      Class<?> subjectClass;

      subjectClass = subject instanceof Class ? (Class<?>) subject : subject.getClass();
      return new AppProperty<>(this, subjectClass.getSimpleName(), defaultValue);
    }

    public AppProperty<T> forSubject(String subject)
    {
      return forSubject(subject, null);
    }

    public AppProperty<T> forSubject(String subject, T defaultValue)
    {
      return new AppProperty<>(this, subject, defaultValue);
    }

    public String getName()
    {
      return mi_name;
    }

    public Converter<T> getConverter()
    {
      return mi_converter;
    }
  }

  public class AppProperty<T>
  {
    private final AppPropertyType<T> mi_type;
    private final String mi_subject;
    private final T mi_defaultValue;
    private final SimpleObjectProperty mi_property;

    public AppProperty(AppPropertyType<T> type, String subject, T defaultValue)
    {
      mi_type = type;
      mi_subject = subject;
      mi_defaultValue = defaultValue;
      mi_property = new SimpleObjectProperty(null, mi_type.mi_converter);

      initProperty();
    }

    private void initProperty()
    {
      T value;

      value = mi_type.mi_converter.fromString(m_propertyStore.getPropertyValue(getPropertyName()));
      if (value == null)
      {
        value = mi_defaultValue;
      }

      set(value);
    }

    public String getName()
    {
      return mi_type.getName();
    }

    private String getPropertyName()
    {
      return (mi_subject + "_" + getName()).toUpperCase().replace(' ', '_').replace('-', '_');
    }

    public T get()
    {
      return property().get();
    }

    public T get(T defaultValue)
    {
      T value;

      assert defaultValue != null;
      value = property().get();
      if (value == null)
      {
        value = defaultValue;
      }

      return value;
    }

    public void reset()
    {
      property().set(mi_defaultValue);
    }

    @SuppressWarnings("unchecked")
    public void set(T value)
    {
      if (value instanceof String)
      {
        value = (T) expandProperties((String) value);
      }

      property().set(value);
    }

    private String expandProperties(String value)
    {
      Matcher matcher;
      StringBuffer sb;

      sb = new StringBuffer();

      matcher = VARIABLE_PATTERN.matcher(value);
      while (matcher.find())
      {
        String key;
        String replacement;

        key = matcher.group(1);
        replacement = (String) System.getProperties().getOrDefault(key, "");

        matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
      }
      matcher.appendTail(sb);

      return sb.toString();
    }

    public void addListener(PropertyChangeListener<T> listener)
    {
      property().addListener(listener);
    }

    private SimpleObjectProperty property()
    {
      if (mi_property == null)
      {
        return mi_property;
      }

      return mi_property;
    }

    /*
     * Get a changelistener that will set the value of this property
     * 
     * WATCH OUT: This changelistener cannot be parameterized because for instance a
     * double property expects a Changelistener<? extends Number> and NOT
     * ChangeListener<Double>. This won't even compile! The FX team decided on this
     * because of lots of additional code. Now we are left with the baked pears!
     * 
     * @return
     */
    @SuppressWarnings(
    {
        "unchecked", "rawtypes"
    })
    public PropertyChangeListener getChangeListener()
    {
      return (a, newValue) -> {
        set((T) newValue);
      };
    }

    public class SimpleObjectProperty
    {
      private T mi_value;
      private Converter<T> mi_converter;
      private List<PropertyChangeListener<T>> mi_listenerList = new ArrayList<>();

      private SimpleObjectProperty(T value, Converter<T> converter)
      {
        mi_value = value;
        mi_converter = converter;
      }

      public void addListener(PropertyChangeListener<T> listener)
      {
        mi_listenerList.add(listener);
      }

      public T get()
      {
        return mi_value;
      }

      public void set(T value)
      {
        T mi_oldValue;

        mi_oldValue = mi_value;
        mi_value = value;
        m_propertyStore.putProperty(getPropertyName(), mi_converter.toString(value));
        mi_listenerList.forEach(listener -> {
          listener.changed(mi_oldValue, mi_value);
        });
      }
    }
  }

  @FunctionalInterface
  public static interface PropertyChangeListener<T>
  {
    void changed(T oldValue, T newValue);
  }

  public static class PropertyStore
  {
    private final String mi_fileName;
    private Properties mi_properties;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> mi_scheduledFuture;
    private boolean mi_sync;

    public PropertyStore(String fileName)
    {
      mi_fileName = fileName;
    }

    public void setSyncImmediately(boolean sync)
    {
      mi_sync = sync;
    }

    public void putProperty(String propertyName, String stringValue)
    {
      Log.log.fine("Mark properties[%s] dirty because property %s changed from %s to %s", getFilePath(), propertyName,
          getProperties().get(propertyName), stringValue);
      if (stringValue == null)
      {
        removeProperty(propertyName);
      }
      else
      {
        getProperties().put(propertyName, stringValue);
      }
      markDirty();
    }

    public void removeProperty(String propertyName)
    {
      Log.log.fine("Mark properties[%s] dirty because property %s is removed", getFilePath(), propertyName);
      getProperties().remove(propertyName);
      markDirty();
    }

    private String getPropertyValue(String propertyKey)
    {
      return (String) getProperties().get(propertyKey);
    }

    private Properties getProperties()
    {
      load();
      return mi_properties;
    }

    private Properties load()
    {
      if (mi_properties == null)
      {
        mi_properties = new Properties();

        Log.log.fine("Load properties[%s]", getFilePath());
        try (InputStream is = Files.newInputStream(getFilePath()))
        {
          mi_properties.load(is);
        }
        catch (FileNotFoundException e)
        {
          // This can happen! First time the application is started.
        }
        catch (NoSuchFileException e)
        {
          // This can happen! First time the application is started.
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }

      return mi_properties;
    }

    private void markDirty()
    {
      if (mi_sync)
      {
        save();
      }
      else
      {
        if (mi_scheduledFuture != null)
        {
          mi_scheduledFuture.cancel(false);
        }
        mi_scheduledFuture = scheduler.schedule(this::save, 1, TimeUnit.SECONDS);
      }
    }

    private synchronized void save()
    {
      Log.log.fine("Save properties[%s]", getFilePath());
      try (OutputStream os = Files.newOutputStream(getFilePath()))
      {
        getProperties().store(os, "store to properties file");
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }

    public Path getFilePath()
    {
      return Path.of(System.getProperty("user.home"), mi_fileName);
    }

    public void clear() throws IOException
    {
      Files.deleteIfExists(getFilePath());
      mi_properties = null;
    }

  }
}
