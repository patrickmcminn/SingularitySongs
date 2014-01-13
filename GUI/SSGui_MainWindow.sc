

SSGui_MainWindow {

  var server, window;

  *new { | width = 400, height = 400 |
    ^super.new.prInit;
  }

  prInit { | width = 400, height = 400 |
    server = Server.default;
    window = Window.new("Singularity Songs", Rect(width: width, height: height), true, true, server);
    window.front;
  }
}