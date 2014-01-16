Tester {

  var server;

  *new { | synth |
    ^super.new.prInit(synth);
  }

  prInit { | synth |
    server = Server.default;
    server.waitForBoot {
      synth.postln;
    }
  }

}