/*
Monday, January 13th 2013
SSGui_SplashScreen.sc
prm
*/

SSGui_SplashScreen {

  var server, window, text, textLoop;

  *new {
    ^super.new.prInit;
  }

  prInit {
    GUI.qt;
    server = Server.default;
    window = Window.new("", Rect(525, 400, 150, 125), true, false, server);
    this.prMakeText;
    this.prAnimateText;
    window.layout = VLayout([text, align: \center]);
    window.front;
    window.alwaysOnTop = true;
  }

  prMakeText {
    text = StaticText.new();
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