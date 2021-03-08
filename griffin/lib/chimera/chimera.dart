import 'package:hawkeye/hawkeye.dart';
import 'package:hawkeye/chimera/global/functions.g.dart' as globals;
import 'package:griffin/chimera/functions.g.dart';
class Chimera{
static Future<bool> initialize() async { bool v = await DefaultInvoker.initialize(INTERNALChimeraClientFunctionInvoker(),globals.INTERNALChimeraClientFunctionInvoker()); 
 return v;
}}

