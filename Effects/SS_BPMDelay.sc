/*
Wednesday, January 8th 2013
SS_BPMDelay.sc
prm
*/

SS_BPMDelay {

  var server;
  var <inBus, delayBus, faderBus;
  var inputSynth, delaySynth, faderSynth;

  *new { | outBus = 0, amp = 1, balance = 0, tempo = 120, division = 4, group = nil, addAction = \addToTail |
    ^super.new.prInit(outBus, amp, balance, tempo, division, group, addAction);
  }

  prInit { | outBus = 0, amp = 1, balance = 0, tempo = 120, division = 4, group = nil, addAction = \addToTail |
    server = Server.default;
    server.waitForBoot {
      this.prAddSynthDefs;
      server.sync;
      this.prMakeBusses;
      server.sync;
      this.prMakeSynths(outBus, amp, balance, tempo, division, group, addAction);

    };
  }

  prAddSynthDefs {

    SynthDef(\prm_BPMDelay, {
      | inBus, outBus, amp = 1, maxDelay = 5, delayTime = 1, decayTime = 0 |
      var input, delay, sig;
      input = In.ar(inBus, 2);
      delay = CombC.ar(input, maxDelay, delayTime, decayTime);
      sig = delay * amp;
      Out.ar(outBus, sig);
    }).add;

    SynthDef(\prm_BPMDelay_StereoInput, {
      | inBus, outBus, amp = 1, mute = 1 |
      var input, sig;
      input = In.ar(inBus, 2);
      sig = input * amp;
      sig = sig * mute;
      Out.ar(outBus, sig);
    }).add;

    SynthDef(\prm_BPMDelay_StereoFader, {
      | inBus, outBus, amp = 0.6, mix = 1, balance = 0, mute = 1 |
      var input, dry, bal, sig;
      input = In.ar(inBus, 2);
      dry = input * (1-mix);
      bal = Balance2.ar(input[0], input[1], balance);
      sig = bal * mix;
      sig = sig + dry;
      sig = bal * amp;
      sig = sig * mute;
      Out.ar(outBus, sig);
    }).add;
  }

  prMakeBusses {
    inBus = Bus.audio(server, 2);
    delayBus = Bus.audio(server, 2);
    faderBus = Bus.audio(server, 2);
  }

  prFreeBusses {
    inBus.free;
    delayBus.free;
    faderBus.free;
  }

  prMakeSynths { | outBus = 0, amp = 1, balance = 0, tempo = 120, division = 4, group = nil, addAction = \addToTail |
    var delayTime = this.calculateDelay(tempo, division);
    inputSynth = Synth(\prm_BPMDelay_StereoInput, [\inBus, inBus, \outBus, delayBus], group, addAction);
    faderSynth = Synth(\prm_BPMDelay_StereoFader, [\inBus, faderBus, \outBus, outBus, \amp, amp, \balance, balance],
      inputSynth, \addAfter);
    delaySynth = Synth(\prm_BPMDelay, [\inBus, delayBus, \outBus, faderBus, \amp, 1, \delayTime, delayTime, \decayTime, 0],
      inputSynth, \addAfter);
  }

  prFreeSynths {
    inputSynth.free;
    delaySynth.free;
    faderSynth.free;
  }

  calculateDelay { | tempo = 120, division = 4 |
    var tempoCalc = 60/tempo;
    var divisionAdjust = tempoCalc * 4;
    var delayTime = divisionAdjust / division;
    ^delayTime;
  }

  //////// Public Functions:

  setAmp { | amp = 0.5 |
    faderSynth.set(\amp, amp);
    ^amp;
  }

  setvol { | vol = -6 |
    this.setAmp(vol.dbamp);
    ^vol;
  }

  setBalance { | balance = 0 |
    faderSynth.set(\balance, balance);
    ^balance;
  }

  setDelayTime { | tempo = 120, division = 4 |
    var delayTime = this.calculateDelay(tempo, division);
    delaySynth.set(\delayTime, delayTime);
  }

  setTempo { | tempo = 120 |
    var delayTime = this.calculateDelay;
  }

}