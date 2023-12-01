/*
This file is part of XMaintenance.
XMaintenance is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
XMaintenance is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with XMaintenance. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.fritx22.xmaintenance.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleInstanceRegistry<E> implements InstanceRegistry<E> {

  private final Map<Class<? extends E>, E> map = new HashMap<>();

  @SuppressWarnings("unchecked")
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
