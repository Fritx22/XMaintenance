package io.github.fritx22.xmaintenance.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleInstanceRegistry<E> implements InstanceRegistry<E> {

  private final Map<Class<? extends E>, E> map = new HashMap<>();

  @Override
  public <S extends E> S getInstance(Class<S> clazz) {
    return (S) this.map.get(clazz);
  }

  @Override
  public <S extends E> void registerInstance(Class<S> clazz, S instance) {
    this.map.put(clazz, instance);
  }

  @Override
  public void forEachEntry(BiConsumer<Class<? extends E>, E> action) {
    this.map.forEach(action);
  }

  @Override
  public void forEachValue(Consumer<E> action) {
    this.map.values().forEach(action);
  }
}
