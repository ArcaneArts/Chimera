import 'package:application/chimera/functions.g.dart';
import 'package:hawkeye/chimera/global/functions.g.dart' as globals;
import 'package:hawkeye/hawkeye.dart';

class Chimera {
  static Future<bool> initialize() async {
    bool v = await DefaultInvoker.initialize(
        INTERNALChimeraClientFunctionInvoker(),
        globals.INTERNALChimeraClientFunctionInvoker());
    return v;
  }
}
