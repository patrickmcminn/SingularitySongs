  // loads audio system
{
  var splash;
  var cond = Condition(false);

  splash = SSGui_SplashScreen.new;
  ~ss= SS_AudioSystem.new(16, 1, 2);

  {
    loop {
      if( try({ ~ss.mixer.reverbShort.synth }).notNil, { cond.test = true; cond.signal; });
      0.1.wait;
    };
  }.fork;
  cond.wait;

  "works".postln;
  splash.close;

  "/Users/patrickmcminn/Library/Application Support/SuperCollider/Extensions/Singularity Songs/GUI/Master.scd".load;
  "/Users/patrickmcminn/Library/Application Support/SuperCollider/Extensions/Singularity Songs/GUI/Returns.scd".load;
  "/Users/patrickmcminn/Library/Application Support/SuperCollider/Extensions/Singularity Songs/GUI/Instrument Mixer.scd".load;

}.fork(AppClock);




