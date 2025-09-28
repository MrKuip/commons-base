package org.kku.common.conf;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class ConfigurationItem
    implements ConfigurationParentIF
{
  private ConfigurationParentIF m_parent;

  @Override
  @JsonBackReference
  public ConfigurationParentIF getParent()
  {
    return m_parent;
  }

  public void setParent(ConfigurationParentIF parent)
  {
    m_parent = parent;
  }

  @JsonIgnore
  protected Configuration getRoot()
  {
    ConfigurationParentIF parent;

    parent = this;
    do
    {
      if (parent instanceof Configuration)
      {
        return (Configuration) parent;
      }
    }
    while ((parent = parent.getParent()) != null);

    return null;
  }

  public void fireChanged()
  {
    getRoot().fireChanged();
  }
}
