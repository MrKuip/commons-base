package org.kku.common.util;

import java.io.InputStream;

public interface ResourceProviderIF
{
  public InputStream getResourceAsStream(String name);
}
