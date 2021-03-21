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

class PaddingTop extends StatelessWidget {
  final double padding;
  final Widget child;

  const PaddingTop({Key key, this.padding, this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(top: padding),
      child: child,
    );
  }
}

class PaddingBottom extends StatelessWidget {
  final double padding;
  final Widget child;

  const PaddingBottom({Key key, this.padding, this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(bottom: padding),
      child: child,
    );
  }
}

class PaddingLeft extends StatelessWidget {
  final double padding;
  final Widget child;

  const PaddingLeft({Key key, this.padding, this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(left: padding),
      child: child,
    );
  }
}

class PaddingRight extends StatelessWidget {
  final double padding;
  final Widget child;

  const PaddingRight({Key key, this.padding, this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(right: padding),
      child: child,
    );
  }
}

class PaddingVertical extends StatelessWidget {
  final double padding;
  final Widget child;

  const PaddingVertical({Key key, this.padding, this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(top: padding, bottom: padding),
      child: child,
    );
  }
}

class PaddingHorizontal extends StatelessWidget {
  final double padding;
  final Widget child;

  const PaddingHorizontal({Key key, this.padding, this.child})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(left: padding, right: padding),
      child: child,
    );
  }
}

class PaddingBottomRight extends StatelessWidget {
  final double padding;
  final Widget child;

  const PaddingBottomRight({Key key, this.padding, this.child})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(bottom: padding, right: padding),
      child: child,
    );
  }
}

class PaddingBottomLeft extends StatelessWidget {
  final double padding;
  final Widget child;

  const PaddingBottomLeft({Key key, this.padding, this.child})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(bottom: padding, left: padding),
      child: child,
    );
  }
}

class PaddingTopLeft extends StatelessWidget {
  final double padding;
  final Widget child;

  const PaddingTopLeft({Key key, this.padding, this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(top: padding, left: padding),
      child: child,
    );
  }
}

class PaddingTopRight extends StatelessWidget {
  final double padding;
  final Widget child;

  const PaddingTopRight({Key key, this.padding, this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(top: padding, right: padding),
      child: child,
    );
  }
}

class PaddingAll extends StatelessWidget {
  final double padding;
  final Widget child;

  const PaddingAll({Key key, this.padding, this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.all(padding),
      child: child,
    );
  }
}
