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
