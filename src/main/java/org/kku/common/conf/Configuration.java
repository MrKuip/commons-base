package org.kku.common.conf;

import java.nio.file.Path;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Configuration
    implements ConfigurationParentIF
{
  @JsonIgnore
  private Path m_path;

  @Override
  public ConfigurationParentIF getParent()
  {
    return null;
  }

  public void addConfigurationListener(ConfigurationListenerIF listener)
  {
    getManager().addConfigurationListener(getClass(), listener);
  }

  public void removeConfigurationListener(ConfigurationListenerIF listener)
  {
    getManager().removeConfigurationListener(getClass(), listener);
  }

  public void fireChanged()
  {
    getManager().fireChanged(getClass());
  }

  public Path getPath()
  {
    return m_path;
  }

  public void setPath(Path path)
  {
    m_path = path;
  }

  private ConfigurationManager getManager()
  {
    return ConfigurationManager.getInstance();
  }
}
