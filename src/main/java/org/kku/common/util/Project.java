package org.kku.common.util;

public class Project
{
  private static Project m_defaultProject = new Project("default");
  private static Project m_instance = m_defaultProject;

  private final String m_name;

  private Project(String name)
  {
    m_name = name;
  }

  public static Project getInstance()
  {
    return m_instance;
  }

  public static void init(String name)
  {
    if (m_instance == m_defaultProject)
    {
      m_instance = new Project(name);

      Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
        Log.log.error(throwable, "%s %s", thread, throwable.getMessage());
      });
    }
    else
    {
      throw new RuntimeException("A project can only be initialized once!");
    }
  }

  public String getName()
  {
    return m_name;
  }
}
