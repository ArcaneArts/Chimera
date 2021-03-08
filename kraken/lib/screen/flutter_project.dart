import 'package:flutter/material.dart';
import 'package:hawkeye/chimera/global/functions.dart';
import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/util/loader.dart';

class FlutterProject extends StatefulWidget {
  final String name;

  FlutterProject({this.name});

  @override
  State<StatefulWidget> createState() => FlutterProjectState();
}

class FlutterProjectState extends State<FlutterProject> {
  bool loadingAAB = false;
  bool loadingAPK = false;
  bool loadingXRC = false;
  bool loadingWEB = false;
  bool loadingClean = false;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      onTap: () {},
      leading: Icon(Icons.filter_tilt_shift_sharp),
      title: Text(
        widget.name,
        style: TextStyle(fontSize: 18),
      ),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          !loadingAAB
              ? IconButton(
                  icon: Text(
                    "AAB",
                    style: TextStyle(fontSize: 11),
                  ),
                  onPressed: () {
                    setState(() {
                      loadingAAB = true;
                      ChimeraParagon.flutterClean(widget.name).then((value) {
                        if (value == null) {
                          ChimeraClientFunctions.snack2(
                              "Failed to build aab " + widget.name,
                              "It may have timed out, either way, we cant know if we actually built aab for " +
                                  widget.name);
                        } else {
                          ChimeraClientFunctions.snack(
                              "Successfully Built AAB " + widget.name);
                        }
                        setState(() {
                          loadingAAB = false;
                        });
                      });
                    });
                  },
                  tooltip: "Artifact " + widget.name + " AAB",
                )
              : Loading.tiny(context),
          !loadingAPK
              ? IconButton(
                  icon: Text("APK", style: TextStyle(fontSize: 11)),
                  onPressed: () {
                    setState(() {
                      loadingAPK = true;
                      ChimeraParagon.flutterBuildAPK(widget.name).then((value) {
                        if (value == null) {
                          ChimeraClientFunctions.snack2(
                              "Failed to build apk " + widget.name,
                              "It may have timed out, either way, we cant know if we actually built apk for " +
                                  widget.name);
                        } else {
                          ChimeraClientFunctions.snack(
                              "Successfully Built APK " + widget.name);
                        }
                        setState(() {
                          loadingAPK = false;
                        });
                      });
                    });
                  },
                  tooltip: "Artifact " + widget.name + " APK",
                )
              : Loading.tiny(context),
          !loadingXRC
              ? IconButton(
                  icon: Text("XRC", style: TextStyle(fontSize: 11)),
                  onPressed: () {},
                  tooltip: "Artifact " + widget.name + " XARCHIVE",
                )
              : Loading.tiny(context),
          !loadingWEB
              ? IconButton(
                  icon: Text("WEB", style: TextStyle(fontSize: 11)),
                  onPressed: () {
                    setState(() {
                      loadingWEB = true;
                      ChimeraParagon.flutterBuildWeb(widget.name).then((value) {
                        if (value == null) {
                          ChimeraClientFunctions.snack2(
                              "Failed to build web " + widget.name,
                              "It may have timed out, either way, we cant know if we actually built web for " +
                                  widget.name);
                        } else {
                          ChimeraClientFunctions.snack(
                              "Successfully Built Web " + widget.name);
                        }
                        setState(() {
                          loadingWEB = false;
                        });
                      });
                    });
                  },
                  tooltip: "Artifact " + widget.name + " WEB",
                )
              : Loading.tiny(context),
          !loadingClean
              ? IconButton(
                  icon: Icon(Icons.local_fire_department_sharp),
                  onPressed: () {
                    setState(() {
                      loadingClean = true;
                      ChimeraParagon.flutterClean(widget.name).then((value) {
                        if (value == null) {
                          ChimeraClientFunctions.snack2(
                              "Failed to clean " + widget.name,
                              "It may have timed out, either way, we cant know if we actually cleaned " +
                                  widget.name);
                        } else {
                          ChimeraClientFunctions.snack(
                              "Successfully cleaned " + widget.name);
                        }
                        setState(() {
                          loadingClean = false;
                        });
                      });
                    });
                  },
                  tooltip: "Clean " + widget.name,
                )
              : Loading.tiny(context)
        ],
      ),
    );
  }
}
