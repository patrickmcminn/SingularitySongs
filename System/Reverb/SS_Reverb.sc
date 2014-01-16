/*

11/7/2013
IM_Reverb.sc


TO DO:
  Do we ever want to use more than one convolution reverb at a time?
If we do, and we try to use this class, then there could be a lot of duplication
of Buffers and computation time, as each instance will be loading and storing
its own set of impulse response buffers.
  One way around this is to make a class that wraps an impulse response dict
and provides an interface for loading IR wavs, converting their kernels, etc.
Then, each IM_Reverb instance just references the impulse response dict object.

NB: Pass the class an fx group that's held in IM_Mixer (should exist in front
of master channel).
*/

SS_Reverb {

  var server;
  var fftBaseSize;
  var fftWindowMul;
  var irDict;

  var <synth;
  var <inBus;

  *new { | outBus = #[0, 1], amp = 1, bufName = '3.4Cathedral', fftMul = 2, group = nil, addAction = 'addToTail' |
    ^super.new.prInit(outBus, amp, bufName, fftMul, group, addAction);
  }

  /////// PRIVATE FUNCTIONS

  prInit { | outBus = #[0, 1], amp = 1, bufName = '3.4Cathedral', fftMul = 2, group = nil, addAction = 'addToTail' |

    server = Server.default;

    server.waitForBoot {

      fftWindowMul = fftMul;
      this.prMakeSynthDefs;
      server.sync;

      this.prMakeBus;
      server.sync;
      this.prMakeSynth(outBus, amp, bufName, group, addAction);
    };

  }

  prMakeSynthDefs {
    SynthDef(\IM_reverbConv, {
      | inBus = 0, outBus = 0, preAmp = 1, amp = 1, lowPassFreq = 15000, highPassFreq = 80, buffer, fftMul = 2 |

      var fftSize, input, leftConvolution, rightConvolution, sum, lowPass, highPass, sig, lagTime;

      lagTime = 0.1;
      fftSize = 1024 * fftMul;

      input = In.ar(inBus, 2);
      input = input * preAmp;
      leftConvolution = PartConv.ar(input[0], fftSize, buffer);
      rightConvolution = PartConv.ar(input[1], fftSize, buffer);
      sum = [leftConvolution, rightConvolution];

      lowPass = LPF.ar(sum, lowPassFreq.lag(lagTime));
      highPass = HPF.ar(lowPass, highPassFreq.lag(lagTime));

      sig = highPass * amp.lag(lagTime);
      Out.ar(outBus, sig);
    }).add;
  }

  prMakeBus {
    inBus = Bus.audio(server, 2);
  }

  prFreeBus {
    inBus.free;
  }


  prMakeSynth { | outBus = 0, amp = 1, bufName = '3.4Cathedral', group = nil, addAction = 'addToTail'  |

    // Spawn a reverb synth if the buffer index was valid, if not, post an error message
    synth = Synth(\IM_reverbConv, [\inBus, inBus, \outBus, outBus, \amp, amp,
      \buffer, bufName, \fftMul, fftWindowMul], group, addAction);
  }

  prFreeSynth {
    synth.free;
  }

  //////// PUBLIC FUNCTIONS

    free {
      this.prFreeSynth;
      this.prFreeBus;
  }

  set { | name = 'amp', val = 0 |
    synth.set(name, val);
  }

  setOutBus { | outBus = 0 |
    synth.set(\outBus, outBus);
  }

  setAmp { | amp = 1 |
    synth.set(\amp, amp);
  }

  setVol { | vol = 0 |
    this.setAmp(vol.dbamp);
  }

  setLowpassCutoff { | cutoff = 15000 |
    synth.set(\lowPassFreq, cutoff);
  }

  setHighpassCutoff { | cutoff = 80 |
    synth.set(\highPassFreq, cutoff);
  }

  changeIR {
    // crossfades between existing reverb synth while fading in another

  }


}