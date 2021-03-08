import 'package:flutter/foundation.dart';

class PlatformIdentity {
  static bool isWeb() {
    return kIsWeb;
  }

  static bool hasIO() {
    return !isWeb();
  }
}
