import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:hawkeye/core/authenticator.dart';
import 'package:hawkeye/core/rebirth.dart';
import 'package:phoenix/chimera/chimera.dart';
import 'package:phoenix/phoenicon_icons.dart';
import 'package:phoenix/screen/home.dart';

void main() {
  Chimera.initialize().then((value) => runApp(Phoenix()));
}

class Phoenix extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return PhoenixState();
  }
}

class PhoenixState extends State<Phoenix> {
  PhoenixState() {}

  @override
  Widget build(BuildContext context) {
    return RestartWidget(
        child: GetMaterialApp(
      home: Authenticator(
        icon: Phoenicon.phoenix,
        color: Colors.deepOrange,
        name: "Phoenix",
        route: MaterialPageRoute(builder: (context) => Home()),
      ),
      debugShowCheckedModeBanner: false,
    ));
  }
}
