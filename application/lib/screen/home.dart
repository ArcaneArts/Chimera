import 'package:flutter/material.dart';
import 'package:hawkeye/util/paddings.dart';
import 'package:kraken/krakicon_icons.dart';

class Home extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => HomeState();
}

class HomeState extends State<Home> {
  bool refreshing = false;
  String log = "";

  @override
  Widget build(BuildContext context) {
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
