/*
 * This file is part of Chimera by Arcane Arts.
 *
 * Chimera by Arcane Arts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Chimera by Arcane Arts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License in this package for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chimera.  If not, see <https://www.gnu.org/licenses/>.
 */

import 'package:flutter/material.dart';

typedef Future<List<T>> PaginatorDelegate<T>(int limit, int offset);

typedef Future<int> PaginatorDelegateCount();

class Paginator<T> {
  final PaginatorDelegate<T> pageDelegate;
  final PaginatorDelegateCount countDelegate;
  final int screenSize;
  final int bufferRadius;
  int _lastCount = -1;
  Map<int, T> _cache = Map<int, T>();

  Paginator(
      {@required this.pageDelegate,
      @required this.countDelegate,
      this.screenSize = 30,
      this.bufferRadius = 128});

  void cleanup(index) async => _cache.keys
      .where((v) => (v - index).abs() > bufferRadius)
      .forEach((i) => invalidate(i));

  void invalidate(int index) => _cache.remove(index);

  Future<int> getCount() async => _lastCount < 0 ? countDelegate() : _lastCount;

  Future<int> updateCount() async {
    _lastCount = -1;
    return await getCount();
  }

  Future<T> get(int index) async {
    int offset = (index ~/ screenSize) * screenSize;
    if (!_cache.containsKey(index)) {
      List<T> data = await pageDelegate(screenSize, offset);

      for (int i = 0; i < data.length; i++) {
        _cache[offset + i] = data[i];
      }
    }

    cleanup(index);

    return _cache[index];
  }
}
