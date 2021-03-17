import 'package:application/krakicon_icons.dart';
import 'package:flutter/material.dart';
import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/util/l.dart';
import 'package:hawkeye/util/paddings.dart';

class Home extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => HomeState();
}

class HomeState extends State<Home> {
  bool refreshing = false;
  String log = "";

  @override
  Widget build(BuildContext context) {
    ChimeraGateway.getSessionId()
        .then((value) => L.v("Connected. Session id is $value"));
    return Scaffold(
      appBar: AppBar(
        title: Row(
          children: [
            Icon(Krakicon.kraken),
            PaddingLeft(
              child: Text("Kraken"),
              padding: 7,
            )
          ],
        ),
        backgroundColor: Colors.deepPurple,
      ),
      body: Text("Example App"),
    );
  }
}
