

SSGui_SplashScreen {

  var server, window, text, textLoop;

  *new {
    ^super.new.prInit;
  }

  prInit {
    server = Server.default;
    //window = .new("", Rect(left: 400, top: 400, width: 300, height: 250), true, false, server);
    window = VLayoutView.new(bounds: Rect(400, 400, 300, 250));
    this.prMakeText;
    this.prAnimateText;
    window.front;
  }

  prMakeText {
    text = StaticText.new(window, Rect(100, 100));
  }

  prAnimateText {
   textLoop = {
      loop {
        text.string = "Loading System";
        0.5.wait;
        text.string = "Loading System.";
        0.5.wait;
        text.string = "Loading System..";
        0.5.wait;
        text.string = "Loading System...";
        0.5.wait;
      };
    }.fork(AppClock);
  }

  close {
    textLoop.stop;
    window.close;
  }
}