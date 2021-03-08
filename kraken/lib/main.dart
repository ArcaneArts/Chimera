import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:hawkeye/core/authenticator.dart';
import 'package:hawkeye/core/rebirth.dart';
import 'package:kraken/chimera/chimera.dart';
import 'package:kraken/krakicon_icons.dart';
import 'package:kraken/screen/home.dart';

void main() {
  Chimera.initialize().then((value) => runApp(Kraken()));
}

class Kraken extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return KrakenState();
  }
}

class KrakenState extends State<Kraken> {
  KrakenState() {}

  @override
  Widget build(BuildContext context) {
    return RestartWidget(
        child: GetMaterialApp(
      home: Authenticator(
        icon: Krakicon.kraken,
        color: Colors.deepPurple,
        name: "Kraken",
        route: MaterialPageRoute(builder: (context) => Home()),
      ),
      debugShowCheckedModeBanner: false,
    ));
  }
}
