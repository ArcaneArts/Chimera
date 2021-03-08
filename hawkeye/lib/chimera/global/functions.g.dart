import 'package:hawkeye/chimera/global/functions.dart';
import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/hawkeye.dart';
class INTERNALChimeraClientFunctionInvoker extends DefaultInvoker{
@override
dynamic invokeClientFunction(String func, List<dynamic> params){
if(func == 'targetUpdated'){
if(params.length != 1){
print('ERROR: Function $func requires 1 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.targetUpdated(params[0] as String);
}
if(func == 'snack'){
if(params.length != 1){
print('ERROR: Function $func requires 1 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.snack(params[0] as String);
}
if(func == 'snack2'){
if(params.length != 2){
print('ERROR: Function $func requires 2 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.snack2(params[0] as String, params[1] as String);
}
if(func == 'snackConnectionError'){
if(params.length != 2){
print('ERROR: Function $func requires 2 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.snackConnectionError(params[0] as String, params[1] as String);
}
if(func == 'signOut'){
if(params.length != 0){
print('ERROR: Function $func requires 0 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.signOut();
}
if(func == 'restart'){
if(params.length != 0){
print('ERROR: Function $func requires 0 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.restart();
}
if(func == 'ping'){
if(params.length != 1){
print('ERROR: Function $func requires 1 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.ping(params[0] as int);
}
if(func == 'isAndroid'){
if(params.length != 0){
print('ERROR: Function $func requires 0 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.isAndroid();
}
if(func == 'isIOS'){
if(params.length != 0){
print('ERROR: Function $func requires 0 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.isIOS();
}
if(func == 'isFuchsia'){
if(params.length != 0){
print('ERROR: Function $func requires 0 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.isFuchsia();
}
if(func == 'isLinux'){
if(params.length != 0){
print('ERROR: Function $func requires 0 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.isLinux();
}
if(func == 'isMacOS'){
if(params.length != 0){
print('ERROR: Function $func requires 0 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.isMacOS();
}
if(func == 'isWindows'){
if(params.length != 0){
print('ERROR: Function $func requires 0 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.isWindows();
}
if(func == 'isAndroidOrIOS'){
if(params.length != 0){
print('ERROR: Function $func requires 0 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.isAndroidOrIOS();
}
if(func == 'isDesktop'){
if(params.length != 0){
print('ERROR: Function $func requires 0 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.isDesktop();
}
if(func == 'isWeb'){
if(params.length != 0){
print('ERROR: Function $func requires 0 parameters. ${params.length} was provided instead.');
return null;}
return ChimeraClientFunctions.isWeb();
}
print('ERROR: Unknown hawkeye function: $func');
return null;
}
@override
bool hasFunction(String func){
if(func == 'targetUpdated'){return true;}
if(func == 'snack'){return true;}
if(func == 'snack2'){return true;}
if(func == 'snackConnectionError'){return true;}
if(func == 'signOut'){return true;}
if(func == 'restart'){return true;}
if(func == 'ping'){return true;}
if(func == 'isAndroid'){return true;}
if(func == 'isIOS'){return true;}
if(func == 'isFuchsia'){return true;}
if(func == 'isLinux'){return true;}
if(func == 'isMacOS'){return true;}
if(func == 'isWindows'){return true;}
if(func == 'isAndroidOrIOS'){return true;}
if(func == 'isDesktop'){return true;}
if(func == 'isWeb'){return true;}
return false;}
}

