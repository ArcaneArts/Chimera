import 'dart:convert';import 'package:hawkeye/chimera/chimera.dart';
/// Represents the Remote Paragon Service on the Chimera Network
class ChimeraParagon
{
/// Invokes flutterClean(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> flutterClean(String project) async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("flutterClean",<dynamic>[project]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes gradleClean(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> gradleClean(String project) async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("gradleClean",<dynamic>[project]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes getFlutterProjects(...) on the Remote Paragon Service on the Chimera Network
static Future<List<String>> getFlutterProjects() async { try{ 
return WrappedObject.of(await ChimeraSocketHelper.invoke("getFlutterProjects",<dynamic>[])).get(listType: '!String') as List<String>;}catch(e){ print('Failed to convert result -> List<String>: (probably null) $e'); return null; }}
/// Invokes gradleArtifact(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> gradleArtifact(String project) async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("gradleArtifact",<dynamic>[project]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes getGradleProjects(...) on the Remote Paragon Service on the Chimera Network
static Future<List<String>> getGradleProjects() async { try{ 
return WrappedObject.of(await ChimeraSocketHelper.invoke("getGradleProjects",<dynamic>[])).get(listType: '!String') as List<String>;}catch(e){ print('Failed to convert result -> List<String>: (probably null) $e'); return null; }}
/// Invokes flutterUpgrade(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> flutterUpgrade() async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("flutterUpgrade",<dynamic>[]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes gradleBuild(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> gradleBuild(String project) async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("gradleBuild",<dynamic>[project]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes flutterBuildAAB(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> flutterBuildAAB(String project) async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("flutterBuildAAB",<dynamic>[project]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes flutterBuildWeb(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> flutterBuildWeb(String project) async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("flutterBuildWeb",<dynamic>[project]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes gitSync(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> gitSync() async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("gitSync",<dynamic>[]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes flutterPrecache(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> flutterPrecache() async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("flutterPrecache",<dynamic>[]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes gitPull(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> gitPull() async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("gitPull",<dynamic>[]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes flutterBuildAPK(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> flutterBuildAPK(String project) async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("flutterBuildAPK",<dynamic>[project]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes gitDiscard(...) on the Remote Paragon Service on the Chimera Network
static Future<bool> gitDiscard() async { try{ 
return await (ChimeraSocketHelper.invokeBigJob("gitDiscard",<dynamic>[]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
}
/// Represents the Remote Account Service on the Chimera Network
class ChimeraAccount
{
/// Invokes requestChangePassword(...) on the Remote Account Service on the Chimera Network
static Future<bool> requestChangePassword(String currentPassword,String newPassword) async { try{ 
return await (ChimeraSocketHelper.invoke("requestChangePassword",<dynamic>[currentPassword,newPassword]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes validateToken(...) on the Remote Account Service on the Chimera Network
static Future<bool> validateToken(AccessToken token) async { try{ 
return await (ChimeraSocketHelper.invoke("validateToken",<dynamic>[token.toJson()]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes getMe(...) on the Remote Account Service on the Chimera Network
static Future<User> getMe() async { try{ 
return WrappedObject.of(await ChimeraSocketHelper.invoke("getMe",<dynamic>[])).get() as User;}catch(e){ print('Failed to convert result -> User: (probably null) $e'); return null; }}
/// Invokes signUp(...) on the Remote Account Service on the Chimera Network
static Future<User> signUp(String firstName,String lastName,String email,String password) async { try{ 
return WrappedObject.of(await ChimeraSocketHelper.invoke("signUp",<dynamic>[firstName,lastName,email,password])).get() as User;}catch(e){ print('Failed to convert result -> User: (probably null) $e'); return null; }}
/// Invokes releaseToken(...) on the Remote Account Service on the Chimera Network
static Future<bool> releaseToken() async { try{ 
return await (ChimeraSocketHelper.invoke("releaseToken",<dynamic>[]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes aquireToken(...) on the Remote Account Service on the Chimera Network
static Future<AccessToken> aquireToken(String email,String password) async { try{ 
return WrappedObject.of(await ChimeraSocketHelper.invoke("aquireToken",<dynamic>[email,password])).get() as AccessToken;}catch(e){ print('Failed to convert result -> AccessToken: (probably null) $e'); return null; }}
/// Invokes getSomeStream(...) on the Remote Account Service on the Chimera Network
static Future<InputStream> getSomeStream() async { try{ 
return WrappedObject.of(await ChimeraSocketHelper.invoke("getSomeStream",<dynamic>[])).get() as InputStream;}catch(e){ print('Failed to convert result -> InputStream: (probably null) $e'); return null; }}
}
/// Represents the Remote Gateway Service on the Chimera Network
class ChimeraGateway
{
/// Invokes isRegistered(...) on the Remote Gateway Service on the Chimera Network
static Future<bool> isRegistered(ID id) async { try{ 
return await (ChimeraSocketHelper.invoke("isRegistered",<dynamic>[id.toJson()]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes unregisterAllWithTarget(...) on the Remote Gateway Service on the Chimera Network
static Future<int> unregisterAllWithTarget(ID target) async { try{ 
return await (ChimeraSocketHelper.invoke("unregisterAllWithTarget",<dynamic>[target.toJson()]))as int;}catch(e){ print('Failed to convert result -> int: (probably null) $e'); return null; }}
/// Invokes getSessionId(...) on the Remote Gateway Service on the Chimera Network
static Future<String> getSessionId() async { try{ 
return await (ChimeraSocketHelper.invoke("getSessionId",<dynamic>[]))as String;}catch(e){ print('Failed to convert result -> String: (probably null) $e'); return null; }}
/// Invokes ping(...) on the Remote Gateway Service on the Chimera Network
static Future<bool> ping() async { try{ 
return await (ChimeraSocketHelper.invoke("ping",<dynamic>[]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes registerListener(...) on the Remote Gateway Service on the Chimera Network
static Future<ID> registerListener(ID target) async { try{ 
return WrappedObject.of(await ChimeraSocketHelper.invoke("registerListener",<dynamic>[target.toJson()])).get() as ID;}catch(e){ print('Failed to convert result -> ID: (probably null) $e'); return null; }}
/// Invokes unregisterAll(...) on the Remote Gateway Service on the Chimera Network
static Future<int> unregisterAll() async { try{ 
return await (ChimeraSocketHelper.invoke("unregisterAll",<dynamic>[]))as int;}catch(e){ print('Failed to convert result -> int: (probably null) $e'); return null; }}
/// Invokes unregisterListener(...) on the Remote Gateway Service on the Chimera Network
static Future<bool> unregisterListener(ID id) async { try{ 
return await (ChimeraSocketHelper.invoke("unregisterListener",<dynamic>[id.toJson()]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
}
/// GuideData is a ghost-copy of the Java Object (art.arcane.chimera.core.object.guide.GuideData) on Chimera
class GuideData{
GuideComponent root;
/// Converts JSON to GuideData
static GuideData fromJson(Map<String, dynamic> json){
return GuideData()..root = GuideComponent.fromJson((json['root'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
;
}
/// Converts GuideData to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(root != null){
json['root'] = root.toJson();
}
return json;
}
@override
String toString() => jsonEncode(toJson());
}
/// Listener is a ghost-copy of the Java Object (art.arcane.chimera.core.object.Listener) on Chimera
class Listener{
ID id;
ID target;
String session;
int time;
/// Converts JSON to Listener
static Listener fromJson(Map<String, dynamic> json){
return Listener()..id = ID.fromJson((json['id'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..target = ID.fromJson((json['target'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..session = json['session']
..time = json['time']
;
}
/// Converts Listener to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(id != null){
json['id'] = id.toJson();
}
if(target != null){
json['target'] = target.toJson();
}
if(session != null){
json['session'] = session;
}
if(time != null){
json['time'] = time;
}
return json;
}
@override
String toString() => jsonEncode(toJson());
}
/// User is a ghost-copy of the Java Object (art.arcane.chimera.core.object.account.User) on Chimera
class User{
ID id;
String firstName;
String lastName;
String email;
int createdDate;
int suspended;
/// Converts JSON to User
static User fromJson(Map<String, dynamic> json){
return User()..id = ID.fromJson((json['id'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..firstName = json['firstName']
..lastName = json['lastName']
..email = json['email']
..createdDate = json['createdDate']
..suspended = json['suspended']
;
}
/// Converts User to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(id != null){
json['id'] = id.toJson();
}
if(firstName != null){
json['firstName'] = firstName;
}
if(lastName != null){
json['lastName'] = lastName;
}
if(email != null){
json['email'] = email;
}
if(createdDate != null){
json['createdDate'] = createdDate;
}
if(suspended != null){
json['suspended'] = suspended;
}
return json;
}
@override
String toString() => jsonEncode(toJson());
}
/// UserPersonal is a ghost-copy of the Java Object (art.arcane.chimera.core.object.account.UserPersonal) on Chimera
class UserPersonal{
ID id;
String phone;
String carrier;
/// Converts JSON to UserPersonal
static UserPersonal fromJson(Map<String, dynamic> json){
return UserPersonal()..id = ID.fromJson((json['id'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..phone = json['phone']
..carrier = json['carrier']
;
}
/// Converts UserPersonal to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(id != null){
json['id'] = id.toJson();
}
if(phone != null){
json['phone'] = phone;
}
if(carrier != null){
json['carrier'] = carrier;
}
return json;
}
@override
String toString() => jsonEncode(toJson());
}
/// InputStream is a ghost-copy of the Java Object (java.io.InputStream) on Chimera
class InputStream{
/// Converts JSON to InputStream
static InputStream fromJson(Map<String, dynamic> json){
return InputStream();
}
/// Converts InputStream to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
return json;
}
@override
String toString() => jsonEncode(toJson());
}
/// Shelf is a ghost-copy of the Java Object (art.arcane.chimera.core.object.guide.Shelf) on Chimera
class Shelf{
ID id;
String name;
int color;
ID parent;
/// Converts JSON to Shelf
static Shelf fromJson(Map<String, dynamic> json){
return Shelf()..id = ID.fromJson((json['id'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..name = json['name']
..color = json['color']
..parent = ID.fromJson((json['parent'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
;
}
/// Converts Shelf to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(id != null){
json['id'] = id.toJson();
}
if(name != null){
json['name'] = name;
}
if(color != null){
json['color'] = color;
}
if(parent != null){
json['parent'] = parent.toJson();
}
return json;
}
@override
String toString() => jsonEncode(toJson());
}
/// ServiceJob is a ghost-copy of the Java Object (art.arcane.chimera.core.object.ServiceJob) on Chimera
class ServiceJob{
String id;
String service;
String function;
String parameters;
int deadline;
int ttl;
/// Converts JSON to ServiceJob
static ServiceJob fromJson(Map<String, dynamic> json){
return ServiceJob()..id = json['id']
..service = json['service']
..function = json['function']
..parameters = json['parameters']
..deadline = json['deadline']
..ttl = json['ttl']
;
}
/// Converts ServiceJob to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(id != null){
json['id'] = id;
}
if(service != null){
json['service'] = service;
}
if(function != null){
json['function'] = function;
}
if(parameters != null){
json['parameters'] = parameters;
}
if(deadline != null){
json['deadline'] = deadline;
}
if(ttl != null){
json['ttl'] = ttl;
}
return json;
}
@override
String toString() => jsonEncode(toJson());
}
/// UserAuthentication is a ghost-copy of the Java Object (art.arcane.chimera.core.object.account.UserAuthentication) on Chimera
class UserAuthentication{
ID id;
String password;
String salt;
String pepper;
/// Converts JSON to UserAuthentication
static UserAuthentication fromJson(Map<String, dynamic> json){
return UserAuthentication()..id = ID.fromJson((json['id'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..password = json['password']
..salt = json['salt']
..pepper = json['pepper']
;
}
/// Converts UserAuthentication to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(id != null){
json['id'] = id.toJson();
}
if(password != null){
json['password'] = password;
}
if(salt != null){
json['salt'] = salt;
}
if(pepper != null){
json['pepper'] = pepper;
}
return json;
}
@override
String toString() => jsonEncode(toJson());
}
/// GuideComponent is a ghost-copy of the Java Object (art.arcane.chimera.core.object.guide.GuideComponent) on Chimera
class GuideComponent{
String name;
ID id;
String type;
/// Converts JSON to GuideComponent
static GuideComponent fromJson(Map<String, dynamic> json){
return GuideComponent()..name = json['name']
..id = ID.fromJson((json['id'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..type = json['type']
;
}
/// Converts GuideComponent to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(name != null){
json['name'] = name;
}
if(id != null){
json['id'] = id.toJson();
}
if(type != null){
json['type'] = type;
}
return json;
}
@override
String toString() => jsonEncode(toJson());
}
/// AccessToken is a ghost-copy of the Java Object (art.arcane.chimera.core.object.account.AccessToken) on Chimera
class AccessToken{
ID id;
ID account;
String type;
int lastUse;
/// Converts JSON to AccessToken
static AccessToken fromJson(Map<String, dynamic> json){
return AccessToken()..id = ID.fromJson((json['id'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..account = ID.fromJson((json['account'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..type = json['type']
..lastUse = json['lastUse']
;
}
/// Converts AccessToken to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(id != null){
json['id'] = id.toJson();
}
if(account != null){
json['account'] = account.toJson();
}
if(type != null){
json['type'] = type;
}
if(lastUse != null){
json['lastUse'] = lastUse;
}
return json;
}
@override
String toString() => jsonEncode(toJson());
}
/// ID is a ghost-copy of the Java Object (art.arcane.chimera.core.object.ID) on Chimera
class ID{
String i;
/// Converts JSON to ID
static ID fromJson(Map<String, dynamic> json){
return ID()..i = json['i']
;
}
/// Converts ID to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(i != null){
json['i'] = i;
}
return json;
}
/// Generates a new Spec ID
static ID random() => ID()..i=RNG.ss(64);
/// Creates an ID from string. This is not checked for validity!
static ID from(String v) => ID()..i=v;
@override
String toString() => i;
}
/// Session is a ghost-copy of the Java Object (art.arcane.chimera.core.object.Session) on Chimera
class Session{
String id;
String gateway;
ID token;
ID user;
int last;
/// Converts JSON to Session
static Session fromJson(Map<String, dynamic> json){
return Session()..id = json['id']
..gateway = json['gateway']
..token = ID.fromJson((json['token'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..user = ID.fromJson((json['user'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..last = json['last']
;
}
/// Converts Session to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(id != null){
json['id'] = id;
}
if(gateway != null){
json['gateway'] = gateway;
}
if(token != null){
json['token'] = token.toJson();
}
if(user != null){
json['user'] = user.toJson();
}
if(last != null){
json['last'] = last;
}
return json;
}
@override
String toString() => jsonEncode(toJson());
}
/// This is used for internal object discovery. DO NOT USE THIS.
class INTERNALChimeraObjectDiscovery {
static dynamic doubleBlindInstantiate(String t, dynamic j){
if(t == 'GuideData'){
return GuideData.fromJson(j);
}
if(t == 'Listener'){
return Listener.fromJson(j);
}
if(t == 'User'){
return User.fromJson(j);
}
if(t == 'UserPersonal'){
return UserPersonal.fromJson(j);
}
if(t == 'InputStream'){
return InputStream.fromJson(j);
}
if(t == 'Shelf'){
return Shelf.fromJson(j);
}
if(t == 'ServiceJob'){
return ServiceJob.fromJson(j);
}
if(t == 'UserAuthentication'){
return UserAuthentication.fromJson(j);
}
if(t == 'GuideComponent'){
return GuideComponent.fromJson(j);
}
if(t == 'AccessToken'){
return AccessToken.fromJson(j);
}
if(t == 'ID'){
return ID.fromJson(j);
}
if(t == 'Session'){
return Session.fromJson(j);
}
print('ERROR: UNKNOWN TYPE: $t');
return null;
}
/// Gets the type name of the object.
static String getTypeName(dynamic t){if(t is GuideData){
return 'GuideData';}if(t is Listener){
return 'Listener';}if(t is User){
return 'User';}if(t is UserPersonal){
return 'UserPersonal';}if(t is InputStream){
return 'InputStream';}if(t is Shelf){
return 'Shelf';}if(t is ServiceJob){
return 'ServiceJob';}if(t is UserAuthentication){
return 'UserAuthentication';}if(t is GuideComponent){
return 'GuideComponent';}if(t is AccessToken){
return 'AccessToken';}if(t is ID){
return 'ID';}if(t is Session){
return 'Session';}print('ERROR: UNKNOWN DYNAMIC TYPE: ${t.runtimeType.toString()}');
return 'ERRORUnknownType';
}/// Unwraps the string into a real object.
static dynamic fromIdentifiedString(String t)=> WrappedObject.of(jsonDecode(t)).get();/// Checks if the given object is suppored for json & networking with chimera.
static bool isSupported(dynamic t)=> t is GuideData|| t is Listener|| t is User|| t is UserPersonal|| t is InputStream|| t is Shelf|| t is ServiceJob|| t is UserAuthentication|| t is GuideComponent|| t is AccessToken|| t is ID|| t is Session;
/// Checks if the given TYPE is suppored for json & networking with chimera.
static bool isSupportedType(dynamic t)=> t == GuideData|| t == Listener|| t == User|| t == UserPersonal|| t == InputStream|| t == Shelf|| t == ServiceJob|| t == UserAuthentication|| t == GuideComponent|| t == AccessToken|| t == ID|| t == Session;
/// Converts this object into a wrapped object the same way the chimera does it.
/// Great for storing in caches or files. Read with WhateverTypeYouUsed t = fromIdentifiedString(thisOutput)
static String toIdentifiedString(dynamic t){if(t is GuideData){
return jsonEncode(WrappedObject.create('GuideData', t.toJson()).toWrappedJson());}if(t is Listener){
return jsonEncode(WrappedObject.create('Listener', t.toJson()).toWrappedJson());}if(t is User){
return jsonEncode(WrappedObject.create('User', t.toJson()).toWrappedJson());}if(t is UserPersonal){
return jsonEncode(WrappedObject.create('UserPersonal', t.toJson()).toWrappedJson());}if(t is InputStream){
return jsonEncode(WrappedObject.create('InputStream', t.toJson()).toWrappedJson());}if(t is Shelf){
return jsonEncode(WrappedObject.create('Shelf', t.toJson()).toWrappedJson());}if(t is ServiceJob){
return jsonEncode(WrappedObject.create('ServiceJob', t.toJson()).toWrappedJson());}if(t is UserAuthentication){
return jsonEncode(WrappedObject.create('UserAuthentication', t.toJson()).toWrappedJson());}if(t is GuideComponent){
return jsonEncode(WrappedObject.create('GuideComponent', t.toJson()).toWrappedJson());}if(t is AccessToken){
return jsonEncode(WrappedObject.create('AccessToken', t.toJson()).toWrappedJson());}if(t is ID){
return jsonEncode(WrappedObject.create('ID', t.toJson()).toWrappedJson());}if(t is Session){
return jsonEncode(WrappedObject.create('Session', t.toJson()).toWrappedJson());}print('ERROR: UNKNOWN DYNAMIC TYPE: ${t.runtimeType.toString()}');
return null;
}}

