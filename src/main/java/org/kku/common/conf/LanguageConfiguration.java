package org.kku.common.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public class LanguageConfiguration
  extends Configuration
{
  @JsonManagedReference
  private List<Language> m_languageList = new ArrayList<>();
  @JsonIgnore
  private Map<String, Language> m_languageByNameMap;

  public LanguageConfiguration()
  {
  }

  public static LanguageConfiguration getInstance()
  {
    return ConfigurationManager.getInstance().get(LanguageConfiguration.class);
  }

  public void add(Language language)
  {
    m_languageList.add(language);
    m_languageByNameMap = null;
  }

  public void remove(Language language)
  {
    m_languageList.remove(language);
    m_languageByNameMap = null;
  }

  public Language getDefault()
  {
    return getList().get(0);
  }

  public Language getLanguageById(String name)
  {
    return getLanguageByNameMap().get(name);
  }

  public Map<String, Language> getLanguageByNameMap()
  {
    if (m_languageByNameMap == null)
    {
      m_languageByNameMap = getList().stream().collect(Collectors.toMap(Language::getName, Function.identity()));
    }

    return m_languageByNameMap;
  }

  public List<Language> getList()
  {
    return m_languageList;
  }
}
