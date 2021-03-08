import 'package:flutter/material.dart';
import 'package:hawkeye/chimera/global/functions.dart';
import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/util/loader.dart';

class GradleProject extends StatefulWidget {
  final String name;

  GradleProject({this.name});

  @override
  State<StatefulWidget> createState() => GradleProjectState();
}

class GradleProjectState extends State<GradleProject> {
  bool loadingBuild = false;
  bool loadingArt = false;
  bool loadingClean = false;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      onTap: () {},
      leading: Icon(Icons.storage),
      title: Text(
        widget.name,
        style: TextStyle(fontSize: 18),
      ),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          !loadingBuild
              ? IconButton(
                  icon: Icon(Icons.handyman_rounded),
                  onPressed: () {
                    setState(() {
                      loadingBuild = true;
                      ChimeraParagon.gradleBuild(widget.name).then((value) {
                        if (value == null) {
                          ChimeraClientFunctions.snack2(
                              "Failed to artifact " + widget.name,
                              "It may have timed out, either way, we cant know if we actually built " +
                                  widget.name);
                        } else {
                          ChimeraClientFunctions.snack(
                              "Successfully Built " + widget.name);
                        }
                        setState(() {
                          loadingBuild = false;
                        });
                      });
                    });
                  },
                  tooltip: "Build " + widget.name,
                )
              : Loading.tiny(context),
          !loadingArt
              ? IconButton(
                  icon: Icon(Icons.save),
                  onPressed: () {
                    setState(() {
                      loadingArt = true;
                      ChimeraParagon.gradleArtifact(widget.name).then((value) {
                        if (value == null) {
                          ChimeraClientFunctions.snack2(
                              "Failed to artifact " + widget.name,
                              "It may have timed out, either way, we cant know if we actually artifacted " +
                                  widget.name);
                        } else {
                          ChimeraClientFunctions.snack(
                              "Successfully Artifacted " + widget.name);
                        }
                        setState(() {
                          loadingArt = false;
                        });
                      });
                    });
                  },
                  tooltip: "Artifact " + widget.name,
                )
              : Loading.tiny(context),
          !loadingClean
              ? IconButton(
                  icon: Icon(Icons.local_fire_department_sharp),
                  onPressed: () {
                    setState(() {
                      loadingClean = true;
                      ChimeraParagon.gradleClean(widget.name).then((value) {
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
