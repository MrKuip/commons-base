package org.kku.common.util.value;

public class MutableObject<T>
{
  private T value;

  public MutableObject(T value)
  {
    this.value = value;
  }

  public T get()
  {
    return value;
  }

  public void set(T value)
  {
    this.value = value;
  }
}