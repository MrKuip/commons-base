package org.kku.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.kku.common.util.AppProperties.AppProperty;

class AppPreferencesTest
{
  @Test
  void testPreference()
  {
    AppProperty<String> pref1;
    AppProperty<String> pref2;
    String home;

    pref1 = AppPreferences.createPreference("test", "Test");
    assertEquals("Test", pref1.get());

    // Do not create new preference if it  already exist! 
    pref2 = AppPreferences.createPreference("test", "Test2");
    assertEquals("Test", pref2.get());

    home = System.getProperty("user.home");
    pref2 = AppPreferences.createPreference("test2", "${user.home}/1");
    assertEquals(home + "/1", pref2.get());

    pref2.set("${user.home}/2");
    assertEquals(home + "/2", pref2.get());
  }
}
