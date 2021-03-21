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

class L {
  static void v(String msg) => _l("TMI", msg);

  static void d(String msg) => _l("DBG", msg);

  static void e(String msg) => _l("ERR", msg);

  static void f(String msg) => _l("FTL", msg);

  static void i(String msg) => _l("INF", msg);

  static void w(String msg) => _l("WRN", msg);

  static void _l(String tag, String msg) => print("[$tag]: $msg");
}
