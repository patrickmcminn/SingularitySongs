/*
Tuesday, January 14th 2014
prm
*/

SSGui_BPMDelayControls {

  var <strip;
  var fader, faderBox, faderText;
  var tempoBox, divisionBox, divisionArray;
  var tempoText, divisionText;
  var font;

  *new { | delayInstance |
    ^super.new.prInit(delayInstance);
  }

  prInit { | delayInstance |

    //this.prMakeFader(delayInstance);
    //this.prMakeFaderBox(delayInstance);
    //this.prMakeFaderText;

    this.prMakeDivision(delayInstance);
    this.prMakeTempo(delayInstance);
    this.prMakeTempoText;
    this.prMakeDivisionText;

    this.prMakeLayout;
  }

  prMakeFader { | delayInstance |
    fader = Slider.new;
    fader.action = { | val |
      var value = val.value;
      var amp = val.value.linlin(0, 1, -70, 6).dbamp;
       value.postln;
      faderBox.value = amp.ampdb;
      //delayInstance.set(\amp, amp);
    };
    fader.value = -6.linlin(-70, 6, 0, 1);
    fader.bounds_(Rect(width: 10));
  }

  prMakeFaderBox { | delayInstance |
    faderBox = NumberBox.new;
    faderBox.action = { | val |
      var value = val.value;
      var amp = value.dbamp;
      fader.value = value.linlin(-70, 6, 0, 1);
      // delayInstance.set(\amp, amp);
    };
    faderBox.clipLo_(-70).clipHi_(6);
    faderBox.bounds_(Rect(width: 40));
    faderBox.value = -6;
  }

  prMakeFaderText {
    faderText = StaticText().string_("BPM Delay");
    faderText.font_(Font("Heather", bold: true));
  }

  prMakeDivision { | delayInstance |
    divisionArray = ["16nt", "16n", "16nd", "8nt", "8n", "8nd", "4nt", "4n", "4nd", "2nt", "2n", "2nd", "1nt", "1n", "1nd"];
    divisionBox = PopUpMenu.new;
    divisionBox.items_(divisionArray);
    divisionBox.action =  { | menu |
      var noteVal = menu.item;
      case
      { noteVal == "16nt" } { delayInstance.setDivision(24); }
      { noteVal == "16n" } { delayInstance.setDivision(16); }
      { noteVal == "16nd" } { delayInstance.setDivision(32/3) }
      { noteVal == "8nt" } { delayInstance.setDivsion(12) }
      { noteVal == "8n" } { delayInstance.setDivision(8) }
      { noteVal == "8nd" } { delayInstance.setDivision(16/3) }
      { noteVal == "4nt" } { delayInstance.setDivison(6) }
      { noteVal == "4n" } { delayInstance.setDivision(4) }
      { noteVal == "4nd" } { delayInstance.setDivison(8/3) }
      { noteVal == "2nt" } { delayInstance.setDivison(3) }
      { noteVal == "2n" } { delayInstance.setDivison(2) }
      { noteVal == "2nd" } { delayInstance.setDivison(4/3) }
      { noteVal == "1nt" } { delayInstance.setDivision(1.5) }
      { noteVal == "1n" } { delayInstance.setDivision(1) }
      { noteVal == "1nd" } { delayInstance.setDivision(2/3) }
    };
    divisionBox.value = 7;
  }

  prMakeDivisionText {
    divisionText = StaticText().string_("Divison");
    divisionText.font_(Font("Heather"));
  }

  prMakeTempoText {
    tempoText = StaticText().string_("Tempo");
    tempoText.font_(Font("Heather"));
  }

  prMakeTempo { | delayInstance |
    tempoBox = NumberBox.new;
    tempoBox.font_(Font(size:12));
    tempoBox.action = { | val |
      var tempo = val.value;
      delayInstance.setTempo(tempo);
    };
    tempoBox.value = 120;
    tempoBox.clipLo_(1);
    tempoBox.clipHi_(440);
  }

  prMakeLayout {
    strip =
      VLayout(
        [tempoBox, \s: 0],
        [tempoText, \s: 0],
        [divisionBox, \s: 0],
        [divisionText, \s: 0],
        [nil, \s: 3]
      );
  }
}