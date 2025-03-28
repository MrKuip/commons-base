package org.kku.common.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.kku.common.util.ResourceProviderIF;

public class TestResourceProvider
    implements ResourceProviderIF
{
  static private final TestResourceProvider m_instance = new TestResourceProvider();

  private Map<String, String> m_resourceMapByName = new HashMap<>();

  private TestResourceProvider()
  {
    init();
  }

  public static ResourceProviderIF getInstance()
  {
    return m_instance;
  }

  private void init()
  {
    m_resourceMapByName.put("/LanguageConfiguration.json", """
         {
          "languageList" : [ {
            "name" : "English",
            "language" : "en",
            "flag" : "GB-UKM",
            "default" : true
          }, {
            "name" : "Nederlands",
            "language" : "nl",
            "flag" : "NL",
            "default" : false
          } ]
        }
                      """);
  }

  @Override
  public InputStream getResourceAsStream(String resourceName)
  {
    String resource;

    resource = m_resourceMapByName.get(resourceName);
    if (resource != null)
    {
      return new ByteArrayInputStream(resource.getBytes());
    }

    return null;
  }
}
