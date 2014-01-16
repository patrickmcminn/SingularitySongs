/*
Tuesday, January 14th 2014
prm
*/

SSGui_ReturnStrip {

  var <strip;
  var fader, faderBox, faderText;

  *new { | synthName |
    ^super.new.prInit(synthName);
  }

  prInit { | synthName |

    this.prMakeFader(synthName);
    this.prMakeFaderBox(synthName);

    this.prMakeLayout;
  }

  prMakeFader { | synthName |
    fader = Slider.new;
    fader.action = { | val |
      var value = val.value;
      var amp = val.value.linlin(0, 1, -60, 6).dbamp;
       value.postln;
      faderBox.value = amp.ampdb;
      synthName.setAmp(amp);
    };
    fader.value = -6.linlin(-60, 6, 0, 1);
  }

  prMakeFaderBox { | synthName |
    faderBox = NumberBox.new;
    faderBox.action = { | val |
      var value = val.value;
      var amp = value.dbamp;
      fader.value = value.linlin(-60, 6, 0, 1);
      synthName.setAmp(amp);
    };
    faderBox.clipLo_(-60).clipHi_(6);
    faderBox.value = -6;
  }

  prMakeLayout {
    strip = VLayout(
      [fader, \s: 0],
      [faderBox, \s: 0]
    );
  }
}