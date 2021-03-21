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

import 'package:web_socket_channel/io.dart';

class AbstractSocket {
  bool _connected = false;
  IOWebSocketChannel _channel;

  void connect(String connect) {
    _channel = IOWebSocketChannel.connect(connect);
    _connected = true;
  }

  Stream getStream() => _channel.stream;

  bool isConnected() {
    return _connected;
  }

  void send(String data) {
    _channel.sink.add(data);
  }

  void disconnect() {
    if (_connected) {
      _channel.sink.close();
      _channel = null;
      _connected = false;
    }
  }
}
