/*
 * This file is part of Chimera by Arcane Arts.
 *
 * Chimera by Arcane Arts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Chimera by Arcane Arts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License in this package for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Chimera.  If not, see <https://www.gnu.org/licenses/>.
 */

import 'dart:convert';
import 'dart:math';

import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/hawkeye.dart';
import 'package:hawkeye/util/l.dart';

class ChimeraSocketHelper {
  static ChimeraSocket _sock;

  static Future<ChimeraSocket> _newConnection() async {
    ChimeraSocket sock = ChimeraSocket();
    bool got = await sock.connect("ws://localhost:8585/");

    if (got) {
      _sock = sock;
      return sock;
    }

    _sock = null;
    return null;
  }

  static Future<ChimeraSocket> getConnection() async =>
      _sock != null && _sock.isConnected() ? _sock : await _newConnection();

  static Future<dynamic> invoke(String name, List<dynamic> args) async {
    ChimeraSocket sock = (await getConnection());

    if (sock == null) {
      print("Error failed to connect! Cannot invoke " + name + "!");
      return null;
    }

    return sock.invokeRemoteFunction(name, args);
  }

  static Future<dynamic> invokeBigJob(String name, List<dynamic> args) async {
    ChimeraSocket sock = (await getConnection());

    if (sock == null) {
      print("Error failed to connect! Cannot invoke " + name + "!");
      return null;
    }

    return sock.invokeRemoteFunction(name, args, bigJob: true);
  }

  static void invokeBlind(String name, List<dynamic> args) async {
    getConnection().then((value) {
      if (value == null) {
        print("Error failed to connect! Cannot invoke " + name + " blindly!");
        return;
      }

      value.invokeRemoteFunctionBlindly(name, args);
    });
  }
}

class ChimeraSocket {
  static DefaultInvoker invoker;
  static DefaultInvoker globalInvoker;
  bool connected = false;
  AbstractSocket socket;

  List<String> futures = List<String>();
  Map<String, GatewayMessage> futuresCompleted = Map<String, GatewayMessage>();

  Future<bool> connect(String url) async {
    if (!DefaultInvoker.isInitialized()) {
      print("You must run Chimera.initialize() before connecting to sockets");
      return false;
    }

    print("Connecting to Socket " + url);
    connected = true;
    socket = AbstractSocket()
      ..connect(url)
      ..getStream().listen((event) => _receive(event));
    return ping().then((value) {
      if (value) {
        print("Connected to socket " + url);
        return true;
      } else {
        print("Failed to connect to socket " + url);
        return false;
      }
    });
  }

  void _receive(String raw) {
    print("[Client <- Server]: " + raw);
    GatewayMessage message = GatewayMessage.from(raw);
    receive(message);
  }

  dynamic _executeFunction(GatewayMessage message) {
    FunctionReference fr =
        FunctionReference.fromJson(message.data as Map<String, dynamic>);
    List<dynamic> realizedParams = List<dynamic>();

    fr.params.forEach((element) {
      if (element is int ||
          element is bool ||
          element is double ||
          element is String) {
        realizedParams.add(element);
      } else {
        realizedParams.add(WrappedObject.of(element).get());
      }
    });

    if (invoker.hasFunction(fr.function)) {
      return invoker.invokeClientFunction(fr.function, realizedParams);
    }
    if (globalInvoker.hasFunction(fr.function)) {
      return globalInvoker.invokeClientFunction(fr.function, realizedParams);
    }

    L.f("Unknown Function: " + fr.function);
    return null;
  }

  Future<bool> ping() async {
    if (!isConnected()) {
      return false;
    }

    GatewayMessage g = new GatewayMessage()..type = "pin";
    futures.add(g.id);
    send(g);
    GatewayMessage message = await waitFor(g.id);
    return message != null && message.type == "pon";
  }

  Future<GatewayMessage> waitFor(String id, {int left = 60}) async {
    if (futuresCompleted.containsKey(id)) {
      GatewayMessage m = futuresCompleted.remove(id);
      return m;
    } else if (left <= 0) {
      print("Timed out waiting for reply for id $id");
      ping().then((value) {
        if (!value) {
          disconnect();
          ChimeraSocketHelper._sock = null;
        }
      });
      return null;
    } else {
      return Future.delayed(
          Duration(milliseconds: 50), () => waitFor(id, left: left - 1));
    }
  }

  void receive(GatewayMessage message) {
    if (futures.contains(message.id)) {
      futuresCompleted[message.id] = message;
      futures.remove(message.id);
    }

    if (message.type == "pin") {
      send(message.reply()..type = "pon");
    }

    if (message.type == 'fun') {
      send(message.reply()
        ..type = 'ret'
        ..data = _executeFunction(message));
    }

    if (message.type == 'fuv') {
      _executeFunction(message);
    }
  }

  void invokeRemoteFunctionBlindly(
      String function, List<dynamic> params) async {
    send(GatewayMessage()
      ..type = 'fuv'
      ..data = FunctionReference.as(function, params).toJson());
  }

  Future<dynamic> invokeRemoteFunction(String function, List<dynamic> params,
      {bool bigJob = false}) async {
    GatewayMessage msg = await sendAndReceive(
        GatewayMessage()
          ..type = "fun"
          ..data = FunctionReference.as(function, params).toJson(),
        bigJob: bigJob);

    if (msg == null) {
      return null;
    }

    return msg.data;
  }

  Future<GatewayMessage> sendAndReceive(GatewayMessage message,
      {bool bigJob = false}) async {
    if (!isConnected()) {
      print("Failed to send rt NOT CONNECTED");
      return null;
    }

    futures.add(message.id);
    send(message);
    return await waitFor(message.id, left: bigJob ? 12000 : 60);
  }

  void send(GatewayMessage message) {
    if (!isConnected()) {
      print("Failed to send msg NOT CONNECTED");
      return;
    }

    _send(message.toJsonString());
  }

  void _send(String raw) {
    if (!isConnected()) {
      print("Failed to send " + raw + " NOT CONNECTED");
      return;
    }

    print('[Client -> Server]: $raw');
    socket.send(raw);
  }

  void disconnect() {
    socket.disconnect();
  }

  bool isConnected() {
    return socket != null && socket.isConnected();
  }
}

class RNG {
  static final String _ch =
      '1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';

  static String ss(int l) {
    String r = '';
    for (int i = 0; i < l; i++) {
      int m = Random.secure().nextInt(_ch.length);
      r += _ch.substring(m, m + 1);
    }

    return r;
  }
}

class FunctionReference {
  String function;
  List<dynamic> params;

  FunctionReference() {
    params = List<dynamic>();
  }

  static FunctionReference as(String name, List<dynamic> d) {
    return FunctionReference()
      ..function = name
      ..params = d;
  }

  static FunctionReference from(String s) {
    Map<String, dynamic> j = jsonDecode(s);
    return FunctionReference()
      ..function = (j["function"] ?? "?") as String
      ..params = (j["params"] ?? List<dynamic>()) as List<dynamic>;
  }

  static FunctionReference fromJson(Map<String, dynamic> j) {
    return FunctionReference()
      ..function = (j["function"] ?? "?") as String
      ..params = (j["params"] ?? List<dynamic>()) as List<dynamic>;
  }

  Map<String, dynamic> toJson() {
    Map<String, dynamic> js = Map<String, dynamic>();
    js["function"] = function;
    js["params"] = params;

    return js;
  }

  String toJsonString() {
    return jsonEncode(toJson());
  }
}

class WrappedObject {
  String type;
  dynamic object;

  static WrappedObject create(String type, Map<String, dynamic> json) {
    return WrappedObject()
      ..type = type
      ..object = json;
  }

  Map<String, dynamic> toWrappedJson() {
    Map<String, dynamic> map = Map<String, dynamic>();
    map["type"] = type;
    map["object"] = object;

    return map;
  }

  static WrappedObject of(dynamic v) {
    Map<String, dynamic> m = v as Map<String, dynamic>;

    return WrappedObject()
      ..type = m["type"] ?? "?"
      ..object = (m["object"] ?? Map<String, dynamic>()) as dynamic;
  }

  dynamic get({String listType}) {
    if (type == "ArrayList" || type == "KList" || type == "List") {
      if (listType.startsWith("!")) {
        String rt = listType.substring(1);

        if (rt == "String") {
          List<String> s = List<String>();
          (object as List<dynamic>).forEach((v) => s.add(v.toString()));
          return s;
        }

        if (rt == "int") {
          List<int> s = List<int>();
          (object as List<dynamic>)
              .forEach((v) => s.add(int.tryParse(v.toString()) ?? 0));
          return s;
        }

        if (rt == "double") {
          List<double> s = List<double>();
          (object as List<dynamic>)
              .forEach((v) => s.add(double.tryParse(v.toString()) ?? 0));
          return s;
        }

        if (rt == "bool") {
          List<bool> s = List<bool>();
          (object as List<dynamic>).forEach(
              (v) => s.add(v.toString() == "true" || v.toString() == "1"));
          return s;
        }

        L.w("Unknown PRIMATIVE List Type: " + rt);
        return object as List<dynamic>;
      }

      List<dynamic> list = List<dynamic>();
      (object as List<dynamic>).forEach((element) => list.add(
          INTERNALChimeraObjectDiscovery.doubleBlindInstantiate(
              listType, element as Map<String, dynamic>)));

      return list;
    }

    if (type == "KMap" ||
        type == "HashMap" ||
        type == "ConcurrentHashMap" ||
        type == "Map") {
      if (listType.startsWith("!")) {
        String rt = listType.substring(1);

        if (rt == "String") {
          Map<String, String> s = Map<String, String>();
          (object as Map<String, dynamic>)
              .forEach((k, v) => s[k] = v.toString());
          return s;
        }

        if (rt == "int") {
          Map<String, int> s = Map<String, int>();
          (object as Map<String, dynamic>)
              .forEach((k, v) => s[k] = int.tryParse(v.toString()) ?? 0);
          return s;
        }

        if (rt == "double") {
          Map<String, double> s = Map<String, double>();
          (object as Map<String, dynamic>)
              .forEach((k, v) => s[k] = double.tryParse(v.toString()) ?? 0);
          return s;
        }

        if (rt == "bool") {
          Map<String, bool> s = Map<String, bool>();
          (object as Map<String, dynamic>).forEach(
              (k, v) => s[k] = v.toString() == "true" || v.toString() == "1");
          return s;
        }

        L.w("Unknown PRIMATIVE List Type: " + rt);
        return object as Map<String, dynamic>;
      }

      Map<String, dynamic> map = Map<String, dynamic>();
      (object as Map<String, dynamic>).forEach((k, v) => map[k] =
          INTERNALChimeraObjectDiscovery.doubleBlindInstantiate(
              listType, v as Map<String, dynamic>));

      return map;
    }

    return INTERNALChimeraObjectDiscovery.doubleBlindInstantiate(type, object);
  }

  List<T> getList<T>({String listType}) {
    List<T> list = List<T>();
    (object as List<dynamic>).forEach((element) => list.add(
        INTERNALChimeraObjectDiscovery.doubleBlindInstantiate(
            listType, element as Map<String, dynamic>)) as T);

    return list;
  }

  Map<String, T> getMap<T>({String listType}) {
    Map<String, T> map = Map<String, T>();
    (object as Map<String, dynamic>).forEach((k, v) => map[k] =
        INTERNALChimeraObjectDiscovery.doubleBlindInstantiate(
            listType, v as Map<String, dynamic>)) as T;

    return map;
  }
}

class GatewayMessage {
  String id;
  String type;
  dynamic data;

  GatewayMessage() {
    id = RNG.ss(8);
  }

  static GatewayMessage from(String s) {
    Map<String, dynamic> j = jsonDecode(s);

    return GatewayMessage()
      ..id = (j["id"] ?? "?") as String
      ..type = (j["type"] ?? "?") as String
      ..data = (j["data"] ?? null);
  }

  GatewayMessage reply() {
    return new GatewayMessage()..id = id;
  }

  String toJsonString() {
    Map<String, dynamic> js = Map<String, dynamic>();
    js["id"] = id;
    js["type"] = type;

    if (data != null) {
      js["data"] = data;
    }

    return jsonEncode(js);
  }
}

class ProtocolBinary {
  zeroFillRightShift(int n, int amount) {
    return (n & 0xffffffff) >> amount;
  }

  int leadingZerosLong(int i) {
    int x = (zeroFillRightShift(i, 32));
    return x == 0 ? 32 + leadingZerosInt(i) : leadingZerosInt(x);
  }

  int leadingZerosInt(int i) {
    if (i <= 0) return i == 0 ? 32 : 0;
    int n = 31;
    if (i >= 1 << 16) {
      n -= 16;
      i = zeroFillRightShift(i, 16);
    }
    if (i >= 1 << 8) {
      n -= 8;
      i = zeroFillRightShift(i, 8);
    }
    if (i >= 1 << 4) {
      n -= 4;
      i = zeroFillRightShift(i, 4);
    }
    if (i >= 1 << 2) {
      n -= 2;
      i = zeroFillRightShift(i, 2);
    }
    return n - (zeroFillRightShift(i, 1));
  }
}
