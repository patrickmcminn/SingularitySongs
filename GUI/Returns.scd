/*
Tuesday, January 14th 2014
SendsAndMaster.scd
prm
*/


var window = Window.new("Singularity Songs", bounds: Rect(430, 600, 410, 300), resizable: false);

var font1 = Font("Heather", 13, true);
var font2 = Font("Heather", 9, false);
var font3 = Font("Heather", 12, true);

var titleText, returnTextArray, returnText;

var longReverb, shortReverb, bpmDelay, bpmDelayControls, looper;
var master;

titleText = StaticText().string_("Returns");
titleText.font_(font1);
returnTextArray = ["Long Reverb", "Short Reverb", "BPM Delay",  "Looper"];
returnText = Array.fill(4, { | index |
  StaticText().string_(returnTextArray[index]);
});
returnText.do({ | text | text.font_(font3); });

longReverb = SSGui_ReturnStrip(~ss.mixer.reverbLong);
longReverb = longReverb.strip;
shortReverb = SSGui_ReturnStrip(~ss.mixer.reverbShort);
shortReverb = shortReverb.strip;
bpmDelay = SSGui_ReturnStrip(~ss.mixer.bpmDelay);
bpmDelay = bpmDelay.strip;
bpmDelayControls = SSGui_BPMDelayControls(~ss.mixer.bpmDelay);
bpmDelayControls = bpmDelayControls.strip;
looper = SSGui_ReturnStrip();
looper = looper.strip;

master = SSGui_MasterStrip();
master = master.strip;

window.layout = VLayout(
  [titleText, \a: \center],
  15,
  HLayout(
    VLayout(
      [longReverb],
      [returnText[0], \a: \center]
    ),
    VLayout(
      [shortReverb],
      [returnText[1], \a: \center]
    ),
    VLayout(
      [looper],
      [returnText[3], \a: \center]
    ),
    VLayout(
      [bpmDelay],
      [returnText[2], \a: \center]
    ),
    [bpmDelayControls]
  );
);

window.alwaysOnTop = true;
window.front;
