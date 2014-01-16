/*
Tuesday, January 14th 2014
Instrument Mixer.scd
prm
*/


var window = Window.new("Singularity Songs", bounds: Rect(25, 400, 400, 450), resizable: false);
var spacer = 10;
var textSpacer = 22;
var titleText;
var violinI, violinII, viola, cello;
var instrumentTextArray, instrumentText;
var reverbLongText, reverbShortText, delayText, looperText;

var font1 = Font("Heather", 13, true);
var font2 = Font("Heather", 9, false);
var font3 = Font("Heather", 11, true);

instrumentTextArray = ["Violin I", "Violin II", "Viola", "Cello"];

violinI = SSGui_ChannelStrip.new(~ss.mixer, 0);
violinI = violinI.strip;
violinII = SSGui_ChannelStrip.new(~ss.mixer, 1);
violinII = violinII.strip;
viola = SSGui_ChannelStrip.new(~ss.mixer, 2);
viola = viola.strip;
cello = SSGui_ChannelStrip.new(~ss.mixer, 3);
cello = cello.strip;

titleText = StaticText().string_("Instrument Mixer");
titleText.font = font1;

reverbLongText = StaticText().string_("Reverb Long").font_(font2);
reverbShortText = StaticText().string_("Reverb Short").font_(font2);
delayText = StaticText().string_("BPM Delay").font_(font2);
looperText = StaticText().string_("Looper").font_(font2);

instrumentText = Array.fill(4, { | index |
  StaticText().string_(instrumentTextArray[index]);
});
instrumentText.do({ | text | text.font_(font3); });


window.layout = VLayout(
  [ titleText, a: \center ],
  HLayout(
    VLayout(
      12,
      [looperText, a: \right],
      textSpacer,
      [delayText, a: \right],
      textSpacer,
      [reverbShortText, a: \right],
      textSpacer,
      [reverbLongText, a: \right],
      365
    ),

    VLayout(
      [violinI],
      [instrumentText[0], \a: \center]
    ),
    spacer,
    VLayout(
      [violinII],
      [instrumentText[1], \a: \center]
    ),
    spacer,
    VLayout(
      [viola],
      [instrumentText[2], \a: \center]
    ),
    spacer,
    VLayout(
      [cello],
      [instrumentText[3], \a: \center]
    )
  )
);

window.alwaysOnTop = true;
window.front;
