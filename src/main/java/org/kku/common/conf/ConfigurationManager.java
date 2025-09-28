package org.kku.common.conf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.kku.common.util.Log;
import org.kku.common.util.ResourceLoader;
import org.kku.common.util.WeakHashSet;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class ConfigurationManager
{
  private final static ConfigurationManager m_instance = new ConfigurationManager();
  private final static int CONFIGURATION_MAX_BYTES = 1000000;

  private final Map<Class<? extends Configuration>, Configuration> configurationByClassMap = new HashMap<>();
  private final JsonMapper m_objectMapper = ConfigurationObjectMapper.createMapper();
  private final Map<String, WeakHashSet<ConfigurationListenerIF>> listenerMap = new HashMap<>();

  private ConfigurationManager()
  {
  }

  public static ConfigurationManager getInstance()
  {
    return m_instance;
  }

  @SuppressWarnings("unchecked")
  public <T extends Configuration> T get(Class<T> clazz)
  {
    return (T) configurationByClassMap.computeIfAbsent(clazz, this::loadConfiguration);
  }

  @SuppressWarnings("unchecked")
  public <T extends Configuration> T get(Class<T> clazz, byte[] bytes)
  {
    return (T) configurationByClassMap.computeIfAbsent(clazz, c -> loadConfiguration(c, bytes));
  }

  private Configuration loadConfiguration(Class<? extends Configuration> clazz)
  {
    String configurationName;

    configurationName = clazz.getSimpleName() + ".json";
    try (InputStream is = ResourceLoader.getInstance().getResourceAsStream(configurationName))
    {
      if (is != null)
      {
        byte[] bytes;

        bytes = is.readNBytes(CONFIGURATION_MAX_BYTES);
        return loadConfiguration(clazz, bytes);
      }
    }
    catch (Exception ex)
    {
      return null;
    }

    return null;
  }

  private Configuration loadConfiguration(Class<? extends Configuration> clazz, byte[] bytes)
  {
    String configurationName;
    Object value;

    configurationName = "/" + clazz.getSimpleName() + ".json";

    if (bytes.length == CONFIGURATION_MAX_BYTES)
    {
      throw new ConfigurationException("Refused to load: Configuration %s exceeds %s bytes", configurationName,
          CONFIGURATION_MAX_BYTES);
    }

    if (bytes.length == 0)
    {
      try
      {
        return clazz.getConstructor().newInstance();
      }
      catch (Throwable ex)
      {
        Log.log.error("Exception while loading: Configuration %s", configurationName);
        throw new ConfigurationException(ex, "Exception while loading: Configuration %s", configurationName);
      }
    }

    try
    {
      value = getMapper().readValue(bytes, clazz);

      return (Configuration) value;
    }
    catch (Throwable ex)
    {
      Log.log.error("Exception while reading value: Configuration %s", configurationName);
      throw new ConfigurationException(ex, "Exception while reading value: Configuration %s", configurationName);
    }
  }

  public <T extends Configuration> byte[] saveToBytes(T configuration)
  {
    ByteArrayOutputStream os;

    os = new ByteArrayOutputStream(1000);
    saveConfiguration(configuration, os);

    return os.toByteArray();
  }

  public <T extends Configuration> void saveToStdOut(T configuration)
  {
    saveConfiguration(configuration, System.out);
  }

  private void saveConfiguration(Configuration configuration, OutputStream os)
  {
    configurationByClassMap.remove(configuration.getClass());
    try
    {
      getMapper().writeValue(os, configuration);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  void addConfigurationListener(Class<? extends Configuration> clazz, ConfigurationListenerIF listener)
  {
    WeakHashSet<ConfigurationListenerIF> listeners;
    String key;

    key = clazz.getName();

    listeners = listenerMap.get(key);
    if (listeners == null)
    {
      listeners = new WeakHashSet<ConfigurationListenerIF>();
      listenerMap.put(key, listeners);
    }

    listeners.add(listener);
  }

  void removeConfigurationListener(Class<? extends Configuration> clazz, ConfigurationListenerIF listener)
  {
    Set<ConfigurationListenerIF> listeners;

    listeners = listenerMap.get(clazz.getName());
    if (listeners != null)
    {
      listeners.remove(listener);
    }
  }

  void fireChanged(Class<? extends Configuration> clazz)
  {
    Set<ConfigurationListenerIF> listeners;

    listeners = listenerMap.get(clazz.getName());
    if (listeners != null)
    {
      for (ConfigurationListenerIF listener : listeners)
      {
        listener.configurationChanged();
      }
    }
  }

  private JsonMapper getMapper()
  {
    return m_objectMapper;
  }
}
