/*
AudioSystem (B&M)

CONCEPT
  AudioSystem is a class that takes care of setting up the bare minimum system needed for
the Impatience Machine live rig. It handles setting up and booting the server, creating the
group for nodes, as well as creating the permanent backend mixer.

FIX THIS
*/

SS_AudioSystem {
  var server;

  var <hardwareOut, <feedbackUnit, <mixer, <postRecorder;
  var <inGroup, <procGroup; // procGroup is derived relative to the mixer/other objects
  var <irLibrary;
  var violinI, violinII, viola, cello;

  // var controlGroup, controlBank;

  *new { |numChans = 16, numMasterChans = 1, numOutputs = 8|
    ^super.new.prInit(numChans, numMasterChans, numOutputs);
  }


  ///////// PRIVATE FUNCTIONS

  prInit { |numChans = 16, numMasterChans = 1, numOutputs = 8|
     var irCond = Condition(false);
    var irDict;

    server = Server.default;
    this.prSetServerOptions(64, 131702, nil);

    server.waitForBoot {
      hardwareOut = IM_HardwareOut(numOutputs);
      1.wait;

      // feedBackUnit = IM_FeedbackUnit();
      inGroup = Group.head(server);
      procGroup = Group.after(inGroup);

      1.wait;

      irLibrary = SS_IRLibrary.new("~/Library/Application Support/SuperCollider/Extensions/Singularity Songs/System/Reverb/Impulse Responses");

      fork {
        loop{
          if( try({ irLibrary.irDict }).notNil, { irCond.test = true; irCond.signal; });
          0.1.wait;
        }
      };
      irCond.wait;

      server.sync;

      irDict = irLibrary.irDict;

      server.sync;

      mixer = SS_Mixer(numChans, numMasterChans, [hardwareOut.inBus(0), hardwareOut.inBus(1)],
        irDict['3.4Cathedral'], irDict['2.0MediumHall'] ,procGroup, \addAfter);

      server.sync;
      1.0.wait;
      violinI = IM_HardwareIn(0, mixer.chanMono(0), inGroup);
      violinII = IM_HardwareIn(1, mixer.chanMono(1), inGroup);
      viola = IM_HardwareIn(2, mixer.chanMono(2), inGroup);
      cello = IM_HardwareIn(3, mixer.chanMono(3), inGroup);
    };
  }

  // Define audio device, block size, and total memory reserved for SCLang
  // Default memory size = 2 ** 17
  prSetServerOptions { |blockSize = 64, memSize = 131072, devName|
    this.prSetBlockSize(blockSize);
    this.prSetMemSize(memSize);
    // this.prSetDevice(devName);
  }

  prSetBlockSize { |blockSize = 64|
    server.options.blockSize = blockSize;
  }

  prSetMemSize { |memSize = 131072|
    server.options.memSize = memSize;
  }

  prSetDevice { |devName = ""|
  }


  ////////// PUBLIC FUNCTIONS
  free {
    // Clean up mixer
    // mixer.free;

    // Quit server
    server.quit;
  }
}
