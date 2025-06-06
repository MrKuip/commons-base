package org.kku.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;

public class ResourceLoader
{
  private final static ResourceLoader m_instance = new ResourceLoader();

  private ResourceLoader()
  {
  }

  public static ResourceLoader getInstance()
  {
    return m_instance;
  }

  public InputStream getResourceAsStream(String resourceName) throws IOException
  {
    List<URL> list;

    list = getResources(resourceName);
    if (list.isEmpty())
    {
      return null;
    }

    return list.get(0).openStream();
  }

  public List<URL> getResources(String resourceName) throws IOException
  {
    resourceName = "module-resources/" + resourceName;
    resourceName = resourceName.replace("//", "/");

    System.out.println("getResources(" + resourceName);

    return Collections.list(getClass().getClassLoader().getResources(resourceName));
  }
}
