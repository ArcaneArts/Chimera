import 'package:flutter/material.dart';
import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/util/loader.dart';
import 'package:hawkeye/util/paddings.dart';
import 'package:kraken/krakicon_icons.dart';
import 'package:kraken/screen/flutter_project.dart';
import 'package:kraken/screen/gradle_project.dart';

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
        actions: [
          refreshing
              ? PaddingRight(
                  child: Loading.card(context, c: Colors.white), padding: 9)
              : new IconButton(
                  tooltip: "Update Workspace",
                  icon: Icon(Icons.update),
                  onPressed: () {
                    setState(() {
                      log = "Discarding Paragon Git Changes";
                      refreshing = true;
                    });
                    ChimeraParagon.gitDiscard().then((f) {
                      setState(() {
                        log = "Pulling Upstream";
                      });
                      ChimeraParagon.gitPull().then((f) {
                        setState(() {
                          log = "Upgrading Flutter";
                        });
                        ChimeraParagon.flutterUpgrade().then((f) async {
                          List<String> flutterProjects =
                              await ChimeraParagon.getFlutterProjects();
                          List<String> gradleProjects =
                              await ChimeraParagon.getGradleProjects();

                          for (int i = 0; i < flutterProjects.length; i++) {
                            setState(() {
                              log = "Cleaning " + flutterProjects[i];
                            });
                            await ChimeraParagon.flutterClean(
                                flutterProjects[i]);
                          }
                          for (int i = 0; i < gradleProjects.length; i++) {
                            setState(() {
                              log = "Cleaning " + gradleProjects[i];
                            });
                            await ChimeraParagon.gradleClean(gradleProjects[i]);
                          }
                          for (int i = 0; i < gradleProjects.length; i++) {
                            setState(() {
                              log = "Building " + gradleProjects[i];
                            });
                            await ChimeraParagon.gradleBuild(gradleProjects[i]);
                          }
                          setState(() {
                            log = "Precaching Builds";
                          });
                          ChimeraParagon.flutterPrecache().then((f) {
                            setState(() {
                              log = "";
                              refreshing = false;
                            });
                          });
                        });
                      });
                    });
                  },
                )
        ],
        backgroundColor: Colors.deepPurple,
      ),
      body: refreshing
          ? Center(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Loading.full(context),
                  PaddingTop(
                    padding: 14,
                    child: Text(
                      log,
                      style: TextStyle(fontSize: 24),
                    ),
                  )
                ],
              ),
            )
          : ListView(
              children: [
                Card(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(
                        "Microservices",
                        style: TextStyle(fontSize: 28),
                      ),
                      FutureBuilder<List<String>>(
                        future: ChimeraParagon.getGradleProjects(),
                        builder: (context, s) {
                          if (s.hasData) {
                            return ListView.builder(
                              itemCount: s.data.length,
                              shrinkWrap: true,
                              itemBuilder: (context, pos) {
                                return GradleProject(name: s.data[pos]);
                              },
                            );
                          } else {
                            return Loading.card(context);
                          }
                        },
                      )
                    ],
                  ),
                ),
                Card(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(
                        "Applications",
                        style: TextStyle(fontSize: 28),
                      ),
                      FutureBuilder<List<String>>(
                        future: ChimeraParagon.getFlutterProjects(),
                        builder: (context, s) {
                          if (s.hasData) {
                            return ListView.builder(
                              itemCount: s.data.length,
                              shrinkWrap: true,
                              itemBuilder: (context, pos) {
                                return FlutterProject(name: s.data[pos]);
                              },
                            );
                          } else {
                            return Loading.card(context);
                          }
                        },
                      )
                    ],
                  ),
                )
              ],
            ),
    );
  }
}
