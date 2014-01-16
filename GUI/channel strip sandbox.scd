x = SSGui_SplashScreen.new

x.closew




(
var name = '/yep';

w = Window.new;
b = LevelIndicator.new(w);
b.drawsPeak = true;
b.numTicks = 11;
b.numMajorTicks = 3;
w.front;

a = Bus.audio;
SynthDef(\rmsResponder, {
  var input;
  input = In.ar(a);
  SendPeakRMS.kr(input, 10, 0.1, '/doofus');
}).play;

{ Out.ar(a, SoundIn.ar(0)) }.play;

OSCdef(\test, { | msg |
  {
    b.value = msg[3].ampdb.linlin(-40, 0, 0, 1);
    b.peakLevel = msg[4].ampdb.linlin(-40, 0, 0, 1);
  }.defer;
  msg[3].postln; }, name);

)

(

var name = 'looooda';
SynthDef(("rmsResponder" ++ name).asSymbol, {
  var input;
  input = In.ar(0);
  SendPeakRMS.kr(input, 10, 0.1, '/doofus');
}).play;
