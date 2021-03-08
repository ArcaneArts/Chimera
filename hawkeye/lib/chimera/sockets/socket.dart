class AbstractSocket {
  void connect(String connect) {
    print('ERROR NO SUPPORT FOR CURRENT PLATFORM: CANNOT CREATE SOCKETS!');
  }

  Stream getStream() => null;

  bool isConnected() {
    return false;
  }

  void send(String data) {}

  void disconnect() {}
}
