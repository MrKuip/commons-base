package org.kku.common.util;

public class Project
{
  private static Project m_instance;

  private final String m_name;

  private Project(String name)
  {
    m_name = name;
  }

  public static Project getInstance()
  {
    if (m_instance == null)
    {
      throw new RuntimeException("Please initialize the project first! (call Project.init())");
    }

    return m_instance;
  }

  public static void init(String name)
  {
    if (m_instance == null)
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
