import 'package:hawkeye/chimera/protocol.dart';
import 'package:hawkeye/util/identity.dart';
import 'package:hawkeye/util/l.dart';
import 'package:hive/hive.dart';
import 'package:path_provider/path_provider.dart';

class StorageUnit {
  final Box _unit;

  StorageUnit(this._unit);

  List<String> keyList({String prefix = ""}) {
    List<String> s = List<String>();
    keys(prefix: prefix).forEach((element) => s.add(element.toString()));
    return s;
  }

  Iterable<dynamic> keys({String prefix = ""}) {
    return _unit.keys.where((element) => element.toString().startsWith(prefix));
  }

  Future<bool> clear() async {
    return _unit.clear().then((value) => true);
  }

  String getString(String k, {String defaultValue = null}) {
    return _unit.get(k, defaultValue: defaultValue);
  }

  int getInt(String k, {int defaultValue = null}) {
    return int.tryParse(_unit.get(k, defaultValue: defaultValue?.toString()));
  }

  double getDouble(String k, {double defaultValue = null}) {
    return double.tryParse(
        _unit.get(k, defaultValue: defaultValue?.toString()));
  }

  bool getBool(String k, {bool defaultValue = null}) {
    String v = _unit.get(k,
        defaultValue:
            defaultValue != null ? (defaultValue ? "1" : "0") == "1" : null);
    return v == null ? null : (v == "1");
  }

  T get<T>(String k, {T defaultValue = null}) {
    if (T == dynamic) {
      L.w("Accessed Key $k in Storage Unit ${_unit.name} without a type. Use TYPE v = get() or make sure the generic type is realized. Don't use dynamics if possible!");
      return getString(k, defaultValue: defaultValue as String) as T;
    }

    if (T == String) {
      return getString(k, defaultValue: defaultValue as String) as T;
    }

    if (T == int) {
      return getInt(k, defaultValue: defaultValue as int) as T;
    }

    if (T == double) {
      return getDouble(k, defaultValue: defaultValue as double) as T;
    }

    if (T == bool) {
      return getBool(k, defaultValue: defaultValue as bool) as T;
    }

    if (INTERNALChimeraObjectDiscovery.isSupportedType(T)) {
      String v = getString(k, defaultValue: null);

      if (v == null) {
        return defaultValue;
      }

      dynamic o = INTERNALChimeraObjectDiscovery.fromIdentifiedString(v);

      if (o == null) {
        return defaultValue;
      }

      if (o is T) {
        return o;
      } else {
        L.w("Retrieved an instance of " +
            o.runtimeType.toString() +
            " in Storage Unit ${_unit.name} with key $k but a " +
            T.toString() +
            " was expected (returning default value instead)!");
        return defaultValue;
      }
    }

    return null;
  }

  void set(String k, dynamic v) {
    if (v == null) {
      L.w("Cannot set a null value for any key (on $k in unit ${_unit.name}). Remove keys with .remove().");
      return;
    }

    if (v is String) {
      _unit.put(k, v);
      return;
    }

    if (v is int || v is double) {
      _unit.put(k, v.toString());
      return;
    }

    if (v is bool) {
      _unit.put(k, v ? "1" : "0");
      return;
    }

    if (INTERNALChimeraObjectDiscovery.isSupported(v)) {
      String ids = INTERNALChimeraObjectDiscovery.toIdentifiedString(v);

      if (ids != null) {
        _unit.put(k, ids);
        return;
      } else {
        L.w("Couldnt create identified string");
      }
    } else {
      L.w("Object type not supported: " + v.runtimeType.toString());
    }

    L.w("Unable to determine type for storage: " + v.runtimeType.toString());
    _unit.put(k, v.toString());
  }
}

class Storage {
  static User getUser() => Storage.getState().get("user");

  static Map<String, StorageUnit> _units = Map<String, StorageUnit>();
  static bool _initialized = false;
  static String resourcePath;

  static StorageUnit getState() => _units["state"];

  static StorageUnit getSettings() => _units["settings"];

  static StorageUnit getCache() => _units["cache"];

  static StorageUnit getTemp() => _units["temp"];

  static void init() async {
    if (_initialized) {
      return;
    }
    _initialized = true;
    L.v("Initializing Hawkeye Storage Engine in " +
        (PlatformIdentity.hasIO() ? "SharedPrefs" : "Cookies"));

    if (PlatformIdentity.hasIO()) {
      await getApplicationDocumentsDirectory()
          .then((value) => Hive.init(value.path + "/cache/units"))
          .then((value) => true);
    }

    await _open("state");
    await _open("settings");
    await _open("cache");
    await _open("temp")
        .then((value) => value.clear().then((valuer) => value.compact()));
    L.v("Hawkeye Storage Engine Online!");
  }

  static Future<bool> purgeAllOpenedBoxes() {
    return Hive.deleteFromDisk().then((value) => true);
  }

  static Future<bool> purge(String d) {
    return Hive.deleteBoxFromDisk(d).then((value) => true);
  }

  static Future<Box> _open(String d, {tries = 4}) async {
    if (tries < 0) {
      L.e("UNABLE TO OPEN HIVE BOX: " + d + " FATAL!");
      return null;
    }

    try {
      Box b = await Hive.openBox(d);
      L.v("Opened Storage Unit: $d");
      _units[d] = StorageUnit(b);
      return b;
    } catch (e) {
      L.e("Error: " + e.toString());
      L.e("Failed to open storage unit: $d, Deleting & re-opening...");
      await Hive.deleteBoxFromDisk(d);
      L.w("Attempting to re-open box " + d);
      return _open(d, tries: tries - 1);
    }
  }
}
