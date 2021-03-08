import 'package:flutter/material.dart';

class RestartWidget extends StatefulWidget {
  static RestartWidgetState restarter = null;

  static void restartApp() {
    if (restarter != null) {
      Future.delayed(Duration(milliseconds: 5), () => restarter.restartApp());
    }
  }

  RestartWidget({this.child});

  final Widget child;

  @override
  RestartWidgetState createState() => RestartWidgetState();
}

class RestartWidgetState extends State<RestartWidget> {
  Key key = UniqueKey();

  void initState() {
    super.initState();
    RestartWidget.restarter = this;
  }

  void restartApp() {
    setState(() {
      key = UniqueKey();
    });
  }

  @override
  Widget build(BuildContext context) {
    return KeyedSubtree(
      key: key,
      child: widget.child,
    );
  }
}
