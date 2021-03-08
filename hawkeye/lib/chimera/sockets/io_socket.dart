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
