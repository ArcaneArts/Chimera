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
import 'package:hawkeye/chimera/chimera.dart';
import 'package:hawkeye/core/storage.dart';
import 'package:hawkeye/util/l.dart';

class DefaultInvoker {
  static bool isInitialized() =>
      ChimeraSocket.invoker != null && ChimeraSocket.globalInvoker != null;

  static Future<bool> initialize(
      DefaultInvoker invoker, DefaultInvoker global) async {
    WidgetsFlutterBinding.ensureInitialized();
    L.v("Starting Chimera <-> Hawkeye Interface Layer");
    if (!isInitialized()) {
      ChimeraSocket.invoker = invoker;
      ChimeraSocket.globalInvoker = global;
    }
    await Storage.init();
    L.v("Chimera <=> Hawkeye Interface Layer Online!");
    return true;
  }

  dynamic invokeClientFunction(String func, List<dynamic> params) => null;

  bool hasFunction(String func) => false;
}
