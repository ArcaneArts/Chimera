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

class UUIDS {
  static String withoutDashes(String uuid) => uuid.replaceAll("-", "");

  static String withDashes(String c) =>
      "${c.substring(0, 8)}-${c.substring(8, 12)}-${c.substring(12, 16)}-${c.substring(16, 20)}-${c.substring(20)}";
}
