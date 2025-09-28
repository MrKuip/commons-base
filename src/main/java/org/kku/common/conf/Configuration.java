package org.kku.common.conf;

public abstract class Configuration
    implements ConfigurationParentIF
{
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

  private ConfigurationManager getManager()
  {
    return ConfigurationManager.getInstance();
  }
}
