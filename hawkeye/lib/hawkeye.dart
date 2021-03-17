/// Hawkeye supports chimera in flutter apps.
library hawkeye;

export 'chimera/chimera_tools.dart';
export 'chimera/protocol.dart';
export 'chimera/sockets/socket.dart'
    if (dart.library.html) 'chimera/sockets/web_socket.dart'
    if (dart.library.io) 'chimera/sockets/io_socket.dart';
export 'core/storage.dart';
