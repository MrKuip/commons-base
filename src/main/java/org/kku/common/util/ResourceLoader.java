package org.kku.common.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;

public class ResourceLoader
{
  private final static ResourceLoader m_instance = new ResourceLoader();
  private List<ResourceProviderIF> m_registeredProviderList = new ArrayList<>();

  private ResourceLoader()
  {
  }

  public static ResourceLoader getInstance()
  {
    return m_instance;
  }

  public InputStream getResourceAsStream(String resourceName)
  {
    Optional<InputStream> is;
    String rn;

    rn = "/" + resourceName + ".json";
    is = m_registeredProviderList.stream().map(rp -> rp.getResourceAsStream(rn)).filter(Objects::nonNull).findFirst();
    if (is.isPresent())
    {
      return is.get();
    }

    return ServiceLoader.load(ResourceProviderIF.class).stream().map(rp -> {
      return rp.get().getResourceAsStream(rn);
    }).filter(Objects::nonNull).findFirst().get();
  }

  public void register(ResourceProviderIF resourceProvider)
  {
    m_registeredProviderList.add(resourceProvider);
  }
}
