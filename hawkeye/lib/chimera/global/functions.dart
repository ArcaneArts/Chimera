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

import 'dart:io' show Platform;

import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/chimera/ripple/ripple.dart';
import 'package:hawkeye/core/rebirth.dart';
import 'package:hawkeye/core/storage.dart';
import 'package:hawkeye/util/snacker.dart';

class ChimeraClientFunctions {
  static bool targetUpdated(String target) {
    RippleNetwork.ripple(ID.from(target));
    return true;
  }

  static bool snack(String message) {
    Snacker.snack(message);
    return true;
  }

  static bool snack2(String title, String message) {
    Snacker.snack2(title, message);
    return true;
  }

  static bool snackConnectionError(String title, String message) {
    Snacker.snackConnectionError(title, message);
    return true;
  }

  static bool signOut() {
    Storage.getState().clear().then((value) => restart());
    return true;
  }

  static bool restart() {
    RestartWidget.restartApp();
    return true;
  }

  static int ping(int v) {
    return v;
  }

  static bool isAndroid() {
    return Platform.isAndroid;
  }

  static bool isIOS() {
    return Platform.isIOS;
  }

  static bool isFuchsia() {
    return Platform.isFuchsia;
  }

  static bool isLinux() {
    return Platform.isLinux;
  }

  static bool isMacOS() {
    return Platform.isMacOS;
  }

  static bool isWindows() {
    return Platform.isWindows;
  }

  static bool isAndroidOrIOS() {
    return isAndroid() || isIOS();
  }

  static bool isDesktop() {
    return isMacOS() || isWindows() || isLinux();
  }

  static bool isWeb() {
    return kIsWeb;
  }
}
