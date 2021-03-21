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
import 'package:hawkeye/widget/quick_animator.dart';

class Fade {
  static bool fast = false;
  static int lc = 0;

  static bool isFast() {
    return fast;
  }

  static Widget fade(Widget child, {Key key}) {
    if (isFast()) {
      return child;
    }

    return AnimatedSwitcher(
      key: key,
      duration: Duration(milliseconds: 350),
      child: child,
    );
  }

  static Widget fadeFast(Widget child, {Key key}) {
    if (isFast()) {
      return child;
    }

    return AnimatedSwitcher(
      key: key,
      duration: Duration(milliseconds: 150),
      child: child,
    );
  }

  static Widget up(Widget child, int pos) {
    if (isFast()) {
      return child;
    }

    return Quick.fadeIn(child: child);
  }

  static Widget fadeSlow(Widget child, {Key key}) {
    if (isFast()) {
      return child;
    }

    return AnimatedSwitcher(
      key: key,
      duration: Duration(milliseconds: 1350),
      child: child,
    );
  }

  static Widget fadeNorm(Widget child, {Key key}) {
    if (isFast()) {
      return child;
    }

    return AnimatedSwitcher(
      key: key,
      duration: Duration(milliseconds: 350),
      child: child,
    );
  }
}

class Loading {
  static Widget full(BuildContext context, {Color c = Colors.black}) {
    return Center(
      child: Container(
        width: 80,
        height: 80,
        child: CircularProgressIndicator(
          valueColor: AlwaysStoppedAnimation<Color>(c),
        ),
      ),
    );
  }

  static Widget card(BuildContext context, {Color c = Colors.black}) {
    return Center(
      child: Container(
        width: 30,
        height: 30,
        child: CircularProgressIndicator(
          valueColor: AlwaysStoppedAnimation<Color>(c),
        ),
      ),
    );
  }

  static Widget tiny(BuildContext context, {Color c = Colors.black}) {
    return Center(
      child: Container(
        width: 15,
        height: 15,
        child: CircularProgressIndicator(
          valueColor: AlwaysStoppedAnimation<Color>(c),
        ),
      ),
    );
  }
}
