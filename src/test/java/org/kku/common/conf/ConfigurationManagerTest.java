package org.kku.common.conf;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public class ConfigurationManagerTest
{
  @Test
  void saveAndGetConfiguration()
  {
    TestConfiguration testConfiguration;
    TestConfiguration testConfiguration2;
    TestConfigurationItem item1;
    byte[] bytes;
    byte[] bytes2;

    testConfiguration = createTestConfiguration();

    item1 = testConfiguration.getList().get(1);
    assertEquals(testConfiguration, item1.getParent());
    assertEquals(testConfiguration, item1.getRoot());
    assertEquals(item1, item1.getItem2().getParent());
    assertEquals(testConfiguration, item1.getItem2().getRoot());

    bytes = ConfigurationManager.getInstance().saveToBytes(testConfiguration);

    testConfiguration2 = ConfigurationManager.getInstance().get(TestConfiguration.class, bytes);

    assertNotNull(testConfiguration2);
    assertEquals(testConfiguration.getList().size(), testConfiguration2.getList().size());
    assertEquals(testConfiguration.getList().get(0).getId(), testConfiguration2.getList().get(0).getId());
    assertEquals(testConfiguration.getList().get(0).getName(), testConfiguration2.getList().get(0).getName());
    assertEquals(testConfiguration.getList().get(1).getId(), testConfiguration2.getList().get(1).getId());
    assertEquals(testConfiguration.getList().get(1).getName(), testConfiguration2.getList().get(1).getName());

    bytes2 = ConfigurationManager.getInstance().saveToBytes(testConfiguration2);
    assertArrayEquals(bytes, bytes2);

    item1 = testConfiguration2.getList().get(1);
    assertEquals(testConfiguration2, item1.getParent());
    assertEquals(testConfiguration2, item1.getRoot());
    assertEquals(item1, item1.getItem2().getParent());
    assertEquals(testConfiguration2, item1.getItem2().getRoot());
  }

  @Test
  void testListener()
  {
    TestConfiguration testConfiguration;
    AtomicInteger counter;
    TestConfigurationItem item1;

    counter = new AtomicInteger();

    testConfiguration = createTestConfiguration();
    testConfiguration.addConfigurationListener(() -> {
      counter.incrementAndGet();
    });

    item1 = testConfiguration.getList().get(1);

    assertEquals(0, counter.get());
    item1.setId(2);
    assertEquals(1, counter.get());
    item1.setName("Name2");
    assertEquals(2, counter.get());

    item1.getItem2().setId(3);
    assertEquals(3, counter.get());
    item1.getItem2().setName("Name3");
    assertEquals(4, counter.get());
  }

  private TestConfiguration createTestConfiguration()
  {
    TestConfiguration testConfiguration;

    testConfiguration = new TestConfiguration();
    testConfiguration.add(new TestConfigurationItem(1, "Test1"));
    testConfiguration.add(new TestConfigurationItem(2, "Test2"));

    return testConfiguration;
  }

  public static class TestConfiguration
    extends Configuration
  {
    @JsonManagedReference
    private List<TestConfigurationItem> m_testConfigurationItemList = new ArrayList<>();

    public TestConfiguration()
    {
    }

    public void add(TestConfigurationItem item)
    {
      item.setParent(this);
      m_testConfigurationItemList.add(item);
    }

    public List<TestConfigurationItem> getList()
    {
      return m_testConfigurationItemList;
    }
  }

  public static class TestConfigurationItem
    extends ConfigurationItem
  {
    private int mi_id;
    private String mi_name;
    @JsonManagedReference
    private TestConfigurationItem2 mi_item2;

    public TestConfigurationItem()
    {
    }

    public TestConfigurationItem(int id, String name)
    {
      mi_id = id;
      mi_name = name;

      mi_item2 = new TestConfigurationItem2(id, name);
      mi_item2.setParent(this);
    }

    public TestConfigurationItem2 getItem2()
    {
      return mi_item2;
    }

    public int getId()
    {
      return mi_id;
    }

    public void setId(int id)
    {
      mi_id = id;
      fireChanged();
    }

    public String getName()
    {
      return mi_name;
    }

    public void setName(String name)
    {
      mi_name = name;
      fireChanged();
    }
  }

  public static class TestConfigurationItem2
    extends ConfigurationItem
  {
    private int mi_id;
    private String mi_name;

    public TestConfigurationItem2()
    {
    }

    public TestConfigurationItem2(int id, String name)
    {
      mi_id = id;
      mi_name = name;
    }

    public int getId()
    {
      return mi_id;
    }

    public void setId(int id)
    {
      mi_id = id;
      fireChanged();
    }

    public String getName()
    {
      return mi_name;
    }

    public void setName(String name)
    {
      mi_name = name;
      fireChanged();
    }
  }
}
