class L {
  static void v(String msg) => _l("TMI", msg);

  static void d(String msg) => _l("DBG", msg);

  static void e(String msg) => _l("ERR", msg);

  static void f(String msg) => _l("FTL", msg);

  static void i(String msg) => _l("INF", msg);

  static void w(String msg) => _l("WRN", msg);

  static void _l(String tag, String msg) => print("[$tag]: $msg");
}
