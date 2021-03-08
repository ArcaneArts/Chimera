import 'package:phoenix/chimera/functions.dart';
import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/hawkeye.dart';
class INTERNALChimeraClientFunctionInvoker extends DefaultInvoker{
@override
dynamic invokeClientFunction(String func, List<dynamic> params){
print('ERROR: Unknown phoenix function: $func');
return null;
}
@override
bool hasFunction(String func){
return false;}
}

