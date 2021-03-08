import 'package:flutter/material.dart';

class FastVisibility extends StatelessWidget {
  final bool visible;
  final Widget child;

  const FastVisibility({Key key, this.visible, this.child}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return visible
        ? child
        : Container(
            width: 0,
            height: 0,
          );
  }
}
