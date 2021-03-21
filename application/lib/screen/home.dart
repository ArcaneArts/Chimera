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
