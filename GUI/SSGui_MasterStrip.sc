/*
Tuesday, January 14th, 2014
prm
*/

SSGui_MasterStrip {

  var <strip;
  var fader, faderBox, faderText, meterLeft, meterRight;
  var responderLeft, responderRight;

  *new { | synthName |
    ^super.new.prInit(synthName);
  }

  prInit { | synthName |

    this.prMakeFader(synthName);
    this.prMakeFaderBox(synthName);
    this.prMakeFaderText;

    this.prMakeResponders;
    this.prMakeMeter;

    this.prMakeLayout;
  }

  prMakeFader { | synthName |
    fader = Slider.new;
    fader.action = { | val |
      var value = val.value;
      var amp = val.value.linlin(0, 1, -60, 6).dbamp;
       value.postln;
      faderBox.value = amp.ampdb;
      synthName.setMasterAmp(0, amp);
    };
    fader.value = -6.linlin(-60, 6, 0, 1);
  }

  prMakeFaderBox { | synthName |
    faderBox = NumberBox.new;
    faderBox.action = { | val |
      var value = val.value;
      var amp = value.dbamp;
      fader.value = value.linlin(-60, 6, 0, 1);
      synthName.setMasterAmp(0, amp);
    };
    faderBox.clipLo_(-60).clipHi_(6);
    faderBox.value = -6;
  }

  prMakeFaderText {
    faderText = StaticText().string_("Master Volume");
    faderText.font_(Font("Heather", 12, true));
  }

  prMakeResponders {

    responderLeft = OSCFunc({ | msg |
      {
        meterLeft.value = msg[3].ampdb.linlin(-40, 0, 0, 1);
        meterLeft.peakLevel = msg[4].ampdb.linlin(-40, 0, 0, 1);
      }.defer;
    }, '/masterAmpLeft');

    responderRight = OSCFunc({ | msg |
      {
        meterRight.value = msg[3].ampdb.linlin(-40, 0, 0, 1);
        meterRight.peakLevel = msg[4].ampdb.linlin(-40, 0, 0, 1);
      }.defer;
    }, '/masterAmpRight');

  }

  prMakeMeter {
    meterLeft = LevelIndicator.new;
    meterLeft.drawsPeak = true;
    meterLeft.numTicks = 11;
    meterLeft.numMajorTicks = 3;
    meterLeft.minSize_(35);

    meterRight = LevelIndicator.new;
    meterRight.drawsPeak = true;
    meterRight.numTicks = 11;
    meterRight.numMajorTicks = 3;
    meterRight.minSize_(35);
  }

  prMakeLayout {
    strip = HLayout(
      VLayout(
        [fader, \s: 0],
        [faderBox, \s: 0],
        [faderText, \s: 0, \a: \center]
      ),
      10,
      HLayout(
        [meterLeft, \s: 5],
        [meterRight, \s: 5]
      )
    );
  }

}