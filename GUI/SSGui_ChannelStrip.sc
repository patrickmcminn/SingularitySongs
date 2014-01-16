/*
Tuesday, January 14th 2013
SSGui_ChannelStrip.sc
*/

SSGui_ChannelStrip {

  var server;
  var fader, faderBox, knobArray, boxArray, <strip;
  var textArray;
  var sendLayoutArray;

  *new { | synthName, chanNum |
    ^super.new.prInit(synthName, chanNum);
  }

  prInit { | synthName, chanNum |

    this.prMakeFader(synthName, chanNum);
    this.prMakeFaderBox(synthName, chanNum);
    this.prMakeSendKnobs(synthName, chanNum);
    this.prMakeSendNumBoxes(synthName, chanNum);
    //this.prMakeSendText;
    this.prMakeSendLayout;
    this.prMakeMasterLayout;
  }

  prMakeFader { | synthName, chanNum |
    fader = Slider.new;
    fader.action = { | val |
      var amp = val.value.linlin(0, 1, -60, 6).dbamp;
      faderBox.value = amp.ampdb;
      synthName.setAmp(chanNum, amp);
    };
    fader.value = -6.linlin(-60, 6, 0, 1);
  }

  prMakeFaderBox { | synthName, chanNum |
    faderBox = NumberBox.new;
    faderBox.action = { | val |
      var value = val.value;
      var amp = value.dbamp;
      fader.value = value.linlin(-60, 6, 0, 1);
      synthName.setAmp(chanNum, amp);
    };
    faderBox.clipLo_(-60).clipHi_(6);
    faderBox.value = -6;
  }

  prMakeSendKnobs { | synthName, chanNum |
    knobArray = Array.fill(4, { | index |
      var knob;
      knob = Knob.new;
      knob.action = { |  val |
        var amp = val.value;
        boxArray[index].value = amp.ampdb;
        synthName.setSendAmp(chanNum, index, amp);
      };
      knob.maxSize = 35;
      knob.value = 0;
    });

  }

  prMakeSendNumBoxes { | synthName, chanNum |
    boxArray = Array.fill(4, { | index |
      var box = NumberBox.new;
      box.action = { | val |
        var amp = val.value.dbamp;
        knobArray[index].value = amp;
        synthName.setSendAmp(chanNum, index, amp);
      };
      box.font_(Font(size: 10));
      box.clipLo_(-60);
      box.clipHi_(0);
      box.maxDecimals_(1);
      box.value = -inf;
    });
  }

  prMakeSendText {
    textArray = Array.fill(4, { StaticText.new; });
    textArray[0].string = "Long Reverb";
    textArray[1].string = "Short Reverb";
    textArray[2].string = "BPM Delay";
    textArray[3].string = "Looper";
  }

  prMakeSendLayout {
    sendLayoutArray = Array.fill(4, { | index |
      HLayout(
        [knobArray[index], \stretch: 0],
        [boxArray[index], \stretch: 0]
      );
    });
  }


  prMakeMasterLayout {
    strip = VLayout(
      [sendLayoutArray[3]],
      [sendLayoutArray[2]],
      [sendLayoutArray[1]],
      [sendLayoutArray[0]],
      [fader, \stretch: 1],
      [faderBox, a: \center]
    );
    }
}