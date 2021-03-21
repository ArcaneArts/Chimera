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

import 'package:animator/animator.dart';
import 'package:flutter/material.dart';

class Quick {
  /// If this build is called, it will fade out the old child and fade in the new child
  /// NOTE: Ensure your child widget has a different if you want it to change!
  static Widget fadeSwitch(
          {@required Widget child,
          int durationIn = 750,
          int durationOut = 750,
          Curve curveIn = Curves.easeInOutQuint,
          Curve curveOut = Curves.easeInOutQuint}) =>
      AnimatedSwitcher(
        child: child,
        switchInCurve: curveIn,
        switchOutCurve: curveOut,
        duration: Duration(milliseconds: durationIn),
        reverseDuration: Duration(milliseconds: durationOut),
      );

  static Widget fadeIn(
          {@required Widget child,
          int duration = 750,
          Curve curve = Curves.easeInOutQuint}) =>
      Animator<double>(
        key: UniqueKey(),
        tween: Tween<double>(begin: 0, end: 1),
        curve: curve,
        cycles: 1,
        builder: (c, s, h) => Opacity(
          opacity: s.value,
          child: h,
        ),
        child: child,
      );
}
