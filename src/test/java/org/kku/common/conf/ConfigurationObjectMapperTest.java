package org.kku.common.conf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.kku.common.test.BaseTest;
import org.kku.common.test.TestUtil;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class ConfigurationObjectMapperTest
  extends BaseTest
{
  @Test
  void testConvertFieldName()
  {
    Function<String, String> convertFieldName;

    convertFieldName = TestUtil.invokePrivateMethod(ConfigurationObjectMapper.class, "convertFieldName");

    assertEquals("test", convertFieldName.apply("test"));
    assertEquals("p_test", convertFieldName.apply("p_test"));
    assertEquals("test", convertFieldName.apply("m_test"));
    assertEquals("test", convertFieldName.apply("mi_test"));
    assertEquals("_test", convertFieldName.apply("mi__test"));
    assertEquals("_mi_test", convertFieldName.apply("_mi_test"));
    assertEquals("test", convertFieldName.apply("mii_test"));
    assertEquals("test", convertFieldName.apply("miii_test"));
    assertEquals("test", convertFieldName.apply("miiii_test"));
    assertEquals("test", convertFieldName.apply("miiiiiiiiiiiii_test"));
    assertEquals("miiip_test", convertFieldName.apply("miiip_test"));
  }

  @Test
  void saveAndGetConfiguration() throws Exception
  {
    TestConfiguration testConfiguration;
    TestConfiguration testConfiguration2;
    JsonMapper mapper;
    String text;
    String text2;

    testConfiguration = new TestConfiguration();
    testConfiguration.mi_string = "String1";
    testConfiguration.mi_int = 10;
    testConfiguration.mi_double = 11.0;

    mapper = ConfigurationObjectMapper.createMapper();
    text = mapper.writeValueAsString(testConfiguration);

    System.out.println(text);

    testConfiguration2 = mapper.readValue(text, TestConfiguration.class);

    assertNotNull(testConfiguration2);
    assertEquals(testConfiguration.mi_string, testConfiguration2.mi_string);
    assertEquals(testConfiguration.mi_int, testConfiguration2.mi_int);
    assertEquals(testConfiguration.mi_double, testConfiguration2.mi_double);

    text2 = mapper.writeValueAsString(testConfiguration2);
    assertEquals(text, text2);
  }

  public static class TestConfiguration
    extends Configuration
  {
    private String mi_string;
    private Integer mi_int = 1;
    private Double mi_double = 2.0;
  }
}
