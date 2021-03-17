import 'package:application/chimera/chimera.dart';
import 'package:application/screen/home.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:hawkeye/core/rebirth.dart';

void main() {
  Chimera.initialize().then((value) => runApp(Application()));
}

class Application extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return ApplicationState();
  }
}

class ApplicationState extends State<Application> {
  ApplicationState() {}

  @override
  Widget build(BuildContext context) {
    return RestartWidget(
        child: GetMaterialApp(
      home: Home(),
      debugShowCheckedModeBanner: false,
    ));
  }
}
