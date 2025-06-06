module org.kku.common
{
  requires transitive java.logging;
  requires com.fasterxml.jackson.databind;
  requires javafx.base;

  exports org.kku.common.util;
  exports org.kku.common.util.value;
  exports org.kku.common.conf;
  exports org.kku.common.conf.dao;
  exports org.kku.common.test;

  opens org.kku.common.conf to com.fasterxml.jackson.databind;
}
