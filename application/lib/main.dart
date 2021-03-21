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

import 'package:application/chimera/chimera.dart';
import 'package:application/krakicon_icons.dart';
import 'package:application/screen/home.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:hawkeye/core/authenticator.dart';
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
      home: Authenticator(
        icon: Krakicon.kraken,
        color: Colors.deepPurple,
        name: "Application",
        route: MaterialPageRoute(builder: (context) => Home()),
      ),
      debugShowCheckedModeBanner: false,
    ));
  }
}
