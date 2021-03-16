import 'dart:convert';import 'package:hawkeye/chimera/chimera.dart';
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
/// Invokes unregisterListener(...) on the Remote Gateway Service on the Chimera Network
static Future<bool> unregisterListener(ID id) async { try{ 
return await (ChimeraSocketHelper.invoke("unregisterListener",<dynamic>[id.toJson()]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
/// Invokes unregisterAll(...) on the Remote Gateway Service on the Chimera Network
static Future<int> unregisterAll() async { try{ 
return await (ChimeraSocketHelper.invoke("unregisterAll",<dynamic>[]))as int;}catch(e){ print('Failed to convert result -> int: (probably null) $e'); return null; }}
/// Invokes registerListener(...) on the Remote Gateway Service on the Chimera Network
static Future<ID> registerListener(ID target) async { try{ 
return WrappedObject.of(await ChimeraSocketHelper.invoke("registerListener",<dynamic>[target.toJson()])).get() as ID;}catch(e){ print('Failed to convert result -> ID: (probably null) $e'); return null; }}
/// Invokes ping(...) on the Remote Gateway Service on the Chimera Network
static Future<bool> ping() async { try{ 
return await (ChimeraSocketHelper.invoke("ping",<dynamic>[]))as bool;}catch(e){ print('Failed to convert result -> bool: (probably null) $e'); return null; }}
}
/// User is a ghost-copy of the Java Object (art.arcane.chimera.core.object.account.User) on Chimera
class User{
ID id;
String firstName;
String lastName;
String email;
int createdDate;
bool suspended;
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
/// ID is a ghost-copy of the Java Object (art.arcane.quill.collections.ID) on Chimera
class ID{
String value;
/// Converts JSON to ID
static ID fromJson(Map<String, dynamic> json){
return ID()..value = json['value']
;
}
/// Converts ID to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(value != null){
json['value'] = value;
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
/// ServiceJob is a ghost-copy of the Java Object (art.arcane.chimera.core.object.ServiceJob) on Chimera
class ServiceJob{
ID id;
String service;
String function;
String parameters;
int deadline;
int ttl;
/// Converts JSON to ServiceJob
static ServiceJob fromJson(Map<String, dynamic> json){
return ServiceJob()..id = ID.fromJson((json['id'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
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
json['id'] = id.toJson();
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
/// Session is a ghost-copy of the Java Object (art.arcane.chimera.core.object.Session) on Chimera
class Session{
ID id;
ID gateway;
ID token;
ID user;
int last;
/// Converts JSON to Session
static Session fromJson(Map<String, dynamic> json){
return Session()..id = ID.fromJson((json['id'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..gateway = ID.fromJson((json['gateway'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..token = ID.fromJson((json['token'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..user = ID.fromJson((json['user'] ?? Map<String,dynamic>()) as Map<String, dynamic>)
..last = json['last']
;
}
/// Converts Session to JSON 
Map<String, dynamic> toJson(){
Map<String, dynamic> json = Map<String, dynamic>();
if(id != null){
json['id'] = id.toJson();
}
if(gateway != null){
json['gateway'] = gateway.toJson();
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
if(t == 'User'){
return User.fromJson(j);
}
if(t == 'Listener'){
return Listener.fromJson(j);
}
if(t == 'AccessToken'){
return AccessToken.fromJson(j);
}
if(t == 'ID'){
return ID.fromJson(j);
}
if(t == 'ServiceJob'){
return ServiceJob.fromJson(j);
}
if(t == 'UserAuthentication'){
return UserAuthentication.fromJson(j);
}
if(t == 'Session'){
return Session.fromJson(j);
}
print('ERROR: UNKNOWN TYPE: $t');
return null;
}
/// Gets the type name of the object.
static String getTypeName(dynamic t){if(t is User){
return 'User';}if(t is Listener){
return 'Listener';}if(t is AccessToken){
return 'AccessToken';}if(t is ID){
return 'ID';}if(t is ServiceJob){
return 'ServiceJob';}if(t is UserAuthentication){
return 'UserAuthentication';}if(t is Session){
return 'Session';}print('ERROR: UNKNOWN DYNAMIC TYPE: ${t.runtimeType.toString()}');
return 'ERRORUnknownType';
}/// Unwraps the string into a real object.
static dynamic fromIdentifiedString(String t)=> WrappedObject.of(jsonDecode(t)).get();/// Checks if the given object is suppored for json & networking with chimera.
static bool isSupported(dynamic t)=> t is User|| t is Listener|| t is AccessToken|| t is ID|| t is ServiceJob|| t is UserAuthentication|| t is Session;
/// Checks if the given TYPE is suppored for json & networking with chimera.
static bool isSupportedType(dynamic t)=> t == User|| t == Listener|| t == AccessToken|| t == ID|| t == ServiceJob|| t == UserAuthentication|| t == Session;
/// Converts this object into a wrapped object the same way the chimera does it.
/// Great for storing in caches or files. Read with WhateverTypeYouUsed t = fromIdentifiedString(thisOutput)
static String toIdentifiedString(dynamic t){if(t is User){
return jsonEncode(WrappedObject.create('User', t.toJson()).toWrappedJson());}if(t is Listener){
return jsonEncode(WrappedObject.create('Listener', t.toJson()).toWrappedJson());}if(t is AccessToken){
return jsonEncode(WrappedObject.create('AccessToken', t.toJson()).toWrappedJson());}if(t is ID){
return jsonEncode(WrappedObject.create('ID', t.toJson()).toWrappedJson());}if(t is ServiceJob){
return jsonEncode(WrappedObject.create('ServiceJob', t.toJson()).toWrappedJson());}if(t is UserAuthentication){
return jsonEncode(WrappedObject.create('UserAuthentication', t.toJson()).toWrappedJson());}if(t is Session){
return jsonEncode(WrappedObject.create('Session', t.toJson()).toWrappedJson());}print('ERROR: UNKNOWN DYNAMIC TYPE: ${t.runtimeType.toString()}');
return null;
}}

