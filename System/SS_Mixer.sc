/*
Mixer (B&M)

Imp Mach 1.0 has a stereo mixer at the backend, with stereo channels and a stereo mixer. It is each song's responsibility to output a stereo feed. Later versions of the software could have other constructors, e.g. *newOctophonic, that handle discrete multichannel paradigms and such.
*/

SS_Mixer {
  var server;
  var nilBus;
  var <group;

  var chanBusArray;
  var masterBusArray;

  var <reverbLong, <reverbShort, <bpmDelay, <looper;

  var chanSynthArray;
  var <masterSynthArray;


  *new { |numChans = 16, numMasterChans = 1, masterOutBus = 0, reverbBufLong, reverbBufShort, relGroup = nil, addAction = \addToHead|
    ^super.new.prInit(numChans, numMasterChans, masterOutBus, reverbBufLong, reverbBufShort, relGroup, addAction);
  }


  ///////// PRIVATE FUNCTIONS
  prInit { |numChans = 16, numMasterChans = 1, masterOutBus = 0, reverbBufLong, reverbBufShort, relGroup = nil, addAction = \addToHead|
    server = Server.default;

    server.waitForBoot {
      this.prAddSynthDefs;
      server.sync;

      this.prMakeBusses(numChans, numMasterChans);
      this.prMakeGroup(relGroup, addAction);
      server.sync;

      this.prMakeSynths(numChans, numMasterChans, masterOutBus, reverbBufLong, reverbBufShort);
      server.sync;
    };
  }

  prAddSynthDefs {
    SynthDef(\IM_Mixer_ChanStrip, { |inBus = 1, outBus = 0, amp = 1, mute = 1,
      preOrPost = 1, panBal = 0, monoOrStereo = 1,
      send0Bus, send0Amp = 0, send1Bus, send1Amp = 0,
      send2Bus, send2Amp = 0, send3Bus, send3Amp = 0|

      var sig, input, monoOrStereoSig, monoInput, stereoInput, sendSig, lagTime = 0.05;

      amp = amp.lag(lagTime);

      input = In.ar(inBus, 2);

      monoInput = Pan2.ar(input[0], panBal);
      stereoInput = Balance2.ar(input[0], input[1], panBal);

      monoOrStereoSig = (monoInput * (1-monoOrStereo)) + (stereoInput * monoOrStereo);
      sig = monoOrStereoSig * amp * mute;

      // Choose pre or post fx sends
      sendSig = (monoOrStereoSig * (1 - preOrPost)) + (sig * preOrPost);

      // Fx sends
      Out.ar(send0Bus, sendSig * send0Amp.lag(lagTime) );
      Out.ar(send1Bus, sendSig * send1Amp.lag(lagTime) );
      Out.ar(send2Bus, sendSig * send2Amp.lag(lagTime) );
      Out.ar(send3Bus, sendSig * send3Amp.lag(lagTime) );

      Out.ar(outBus, sig);
    }).add;

    SynthDef(\IM_Mixer_MasterStrip, { |inBus = 1, outBus = 0, amp = 1, mute = 1,
      preOrPost = 1, balance = 0|

      var input, sig, lagTime = 0.05;

      amp = amp.lag(lagTime);
      input = In.ar(inBus, 2);
      sig = Balance2.ar(input[0], input[1], balance, amp * mute);

      Out.ar(outBus, sig);
    }).add;

    SynthDef(\IM_Mixer_Fade, { |outBus = 0, startLvl = 1, endLvl = 0, dur = 0.5|
      var sig = Line.kr(startLvl, endLvl, dur, doneAction: 2);
      Out.kr(outBus, sig);
    }).add;
  }

  prMakeBusses { |numChans = 16, numMasterChans = 1|
    nilBus = Bus.audio(server, 2);

    chanBusArray = Array.fill(numChans, { |index|
      Bus.audio(server, 2);
    });

    masterBusArray = Array.fill(numMasterChans, { |index|
      Bus.audio(server, 2);
    });
  }

  prFreeBusses {
    nilBus.free;
    chanBusArray.do({ | bus | bus.free; });
    masterBusArray.do({ | bus | bus.free; });
  }

  prMakeGroup { | relGroup = nil, addAction = \addToHead |
    group = Group.new(relGroup, addAction);
  }

  prFreeGroup { group.free; }

  prMakeSynths { | numChans = 16, numMasterChans = 1, masterOutBus = 0, reverbBufLong, reverbBufShort |
    var reverbBusCond = Condition(false);

    fork {
      masterSynthArray = Array.fill(numMasterChans, { | index |
        Synth.tail(group, \IM_Mixer_MasterStrip, [
          \inBus, masterBusArray[index], \outBus, masterOutBus, \amp, 1, \mute, 1
        ]);
      });

      // Add master send effects
      // (Use addToTail, so they always come after the channel strips, but before the master)
      reverbLong = IM_Reverb(masterBusArray[0], bufName: reverbBufLong, group: group, addAction: \addToHead);
      reverbShort = IM_Reverb(masterBusArray[0], bufName: reverbBufShort, group: group, addAction: \addToHead);
      bpmDelay = SS_BPMDelay.new(masterBusArray[0], 1, 0, 120, 4, group, \addToHead);
      looper = nil;

      server.sync;

      fork {
        loop {
          if(reverbShort.inBus.notNil, { reverbBusCond.test = true; reverbBusCond.signal; });
          0.1.wait;
        };
      };
      reverbBusCond.wait;

      10.do({ server.sync; });
      //1.0.wait;

      // Add before master sends, so the channel strips can be processed by master effects
      chanSynthArray = Array.fill(numChans, { |index|
        Synth.head(group, \IM_Mixer_ChanStrip, [
          \inBus, chanBusArray[index], \outBus, masterBusArray[0], \amp, 1, \mute, 1,
          \send0Bus, reverbLong.inBus, \send1Bus, reverbShort.inBus,
          \send2Bus, bpmDelay.inBus, \send3Bus, nilBus
        ]);
      });
    };
  }

  prFreeSynths {
    reverbLong.free;
    reverbShort.free;
    chanSynthArray.do({ | synth | synth.free; });
    masterSynthArray.do({ | synth | synth.free; });
  }


  prGetSynth { |chanNum = 0, type = \chan|
    ^switch(type,
      \chan, { chanSynthArray[chanNum]; },
      \master, { masterSynthArray[chanNum]; }
    );
  }

  //////// PUBLIC FUNCTIONS

  inBus { |chanNum = 0|
    ^chanBusArray[chanNum];
  }

  chan { |chanNum = 0|
    ^this.inBus(chanNum);
  }

  setMono { |chanNum = 0|
    this.prGetSynth(chanNum, \chan).set(\monoOrStereo, 0);
  }

  setStereo { |chanNum = 0|
    this.prGetSynth(chanNum, \chan).set(\monoOrStereo, 1);
  }

  inBusMono { |chanNum = 0|
    this.setMono(chanNum);
    ^this.inBus(chanNum).subBus(0);
  }

  chanMono { |chanNum = 0|
    ^this.inBusMono(chanNum);
  }

  inBusStereo { |chanNum = 0|
    this.setStereo(chanNum);
    ^this.inBus(chanNum);
  }

  chanStereo { |chanNum = 0|
    ^this.inBusStereo(chanNum);
  }


/*
  setMasterOutBus { |busNum = 0|
  }
*/

  tglMute { |chanNum = 0, type = \chan|
    var synth = this.prGetSynth(chanNum, type);

    synth.get(\mute, { |muteState|
      if(muteState == 1,
        { synth.set(\mute, 0); },
        { synth.set(\mute, 1); }
      );
    });
  }

  tglMuteMaster { |chanNum = 0|
    this.tglMute(chanNum, \master);
  }

  /* BUG: muteState has not been set by the time it returns, thus always returns 'nil'
  getMuteState { |chanNum = 0, type = \chan|
    var synth, muteState;

    switch(type,
      \chan, { synth = chanSynthArray[chanNum]; },
      \master, { synth = masterSynthArray[chanNum]; },
    );

    synth.get(\mute, { |val| muteState = val; });
    ^muteState;
  }
  */

  setAmp { |chanNum = 0, amp = 0, type = \chan|
    this.prGetSynth(chanNum, type).set(\amp, amp);
  }

  setVol { |chanNum = 0, db = 0, type = \chan|
    this.prGetSynth(chanNum, type).set(\amp, db.dbamp);
  }

  setMasterVol { |chanNum = 0, db = 0|
    this.setVol(chanNum, db, \master);
  }

  setPanBal { |chanNum = 0, panBal = 0, type = \chan|
    this.prGetSynth(chanNum, type).set(\panBal, panBal);
  }

  setSendAmp { |chanNum = 0, sendNum = 0, amp = 0|
    var synth, sendSymbol;

    synth = chanSynthArray[chanNum];
    sendSymbol = ("send" ++ sendNum ++ "Amp").asSymbol;

    synth.set(sendSymbol, amp);
  }

  setSendVol { |chanNum = 0, sendNum = 0, vol = 0|
    this.setSendAmp(chanNum, sendNum, vol.dbamp);
  }

  setReverbSendVol { |chanNum = 0, vol = 0|
    this.setSendVol(chanNum, 0, vol);
  }

  setSendPre { |chanNum = 0, type = \chan|
    this.prGetSynth(chanNum, type).set(\preOrPost, 0);
  }

  setSendPost { |chanNum = 0, type = \chan|
    this.prGetSynth(chanNum, type).set(\preOrPost, 1);
  }

  // set {}    // set all parameters of a channel strip (convenience that calls other functions)

  // fade {}   // create a fade using a SynthDef that this class specifies

  fadeChan { |chanNum = 0, vol = 0, dur = 0.5|
    var ctrlBus;

    fork {
      ctrlBus = Bus.control(server, 1).set(vol.dbamp);
      server.sync;

      chanSynthArray[chanNum].get(\amp, { |val|
        Synth.head(group, \IM_Mixer_Fade,
          [\outBus, ctrlBus, \startLvl, val, \endLvl, vol.dbamp, \dur, dur]
        );
      });
      server.sync;

      chanSynthArray[chanNum].map(\amp, ctrlBus);

      dur.wait;
      ctrlBus.free;
      // Necessary to explicitly set channel volume,
      // due to .map and control->audio bus idiosyncracies
      // Otherwise, the fade will always start at an amp of 1
      this.setVol(chanNum, vol);
    };
  }

  fadeOutChan { |chanNum = 0, dur = 0.5|
    this.fadeChan(chanNum, -999, dur);
  }


  free {
    this.prFreeSynths;
    this.prFreeBusses;
    this.prFreeGroup;
  }
}