package org.kku.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionUtil
{
  private static VersionUtil m_instance = new VersionUtil();

  private Properties m_properties;

  private VersionUtil()
  {
  }

  public static VersionUtil getInstance()
  {
    return m_instance;
  }

  public String getVersion()
  {
    return (String) getProperties().get("version");
  }

  private Properties getProperties()
  {
    if (m_properties == null)
    {
      String versionResource;

      versionResource = "Version.properties";
      m_properties = new Properties();
      try
      {
        ResourceLoader.getInstance().getResources(versionResource).forEach(resource -> {
          try (InputStream is = resource.openStream())
          {
            m_properties.load(is);
          }
          catch (IOException e)
          {
            Log.log.error("Failed to load properties from resource: " + resource);
          }
        });
      }
      catch (IOException e)
      {
        Log.log.error(e, "Failed to get the resource %s", versionResource);
      }
    }

    return m_properties;
  }
}
