/*
Thursday, January 16th 2014
prm
*/

SSGui_InputMeter {

  var server;
  var nilBus;
  var input, rms, osc, meter;

  *new { | inBus |
    ^super.new.prInit(inBus);
  }

  prInit {
    server.waitForBoot { | inBus |
      this.prMakeBus;
      server.sync;
      this.prMakeRMS;
      server.sync;
      this.prMakeInput(inBus);
      server.sync;
      this.prMakeOSC;
      this.prMakeMeter;
    };
  }

  prMakeBus {
    nilBus = Bus.audio;
  }

  prMakeInput { | inBus |
    input = IM_HardwareIn.new(inBus, nilBus);
  }

  prMakeRMS { | inBus |
    SynthDef(("peakMeter" ++ inBus).asSymbol, {
      | inBus = 0 |
      var input;
      input = In.ar(inBus);
      SendPeakRMS.kr(input, 20, 0.1, '/reply');
    }).add;

  }




}

