import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:griffin/chimera/chimera.dart';
import 'package:griffin/griffcon_icons.dart';
import 'package:griffin/screen/home.dart';
import 'package:hawkeye/core/authenticator.dart';
import 'package:hawkeye/core/rebirth.dart';

void main() {
  Chimera.initialize().then((value) => runApp(Griffin()));
}

class Griffin extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return GriffinState();
  }
}

class GriffinState extends State<Griffin> {
  GriffinState() {}

  @override
  Widget build(BuildContext context) {
    return RestartWidget(
        child: GetMaterialApp(
      home: Authenticator(
        icon: Griffcon.griffin,
        color: Colors.indigo,
        name: "Griffin",
        route: MaterialPageRoute(builder: (context) => Home()),
      ),
      debugShowCheckedModeBanner: false,
    ));
  }
}
