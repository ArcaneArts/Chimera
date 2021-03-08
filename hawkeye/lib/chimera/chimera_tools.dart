import 'package:flutter/material.dart';
import 'package:hawkeye/chimera/chimera.dart';
import 'package:hawkeye/core/storage.dart';
import 'package:hawkeye/util/l.dart';

class DefaultInvoker {
  static bool isInitialized() =>
      ChimeraSocket.invoker != null && ChimeraSocket.globalInvoker != null;

  static Future<bool> initialize(
      DefaultInvoker invoker, DefaultInvoker global) async {
    WidgetsFlutterBinding.ensureInitialized();
    L.v("Starting Chimera <-> Hawkeye Interface Layer");
    if (!isInitialized()) {
      ChimeraSocket.invoker = invoker;
      ChimeraSocket.globalInvoker = global;
    }
    await Storage.init();
    L.v("Chimera <=> Hawkeye Interface Layer Online!");
    return true;
  }

  dynamic invokeClientFunction(String func, List<dynamic> params) => null;

  bool hasFunction(String func) => false;
}
