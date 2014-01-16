/*
Wednesday, January 15th
2014
*/


var window = Window.new("Singularity Songs", bounds: Rect(845, 600, 150, 300), resizable: false);

var font1 = Font("Heather", 13, true);
var font2 = Font("Heather", 9, false);
var font3 = Font("Heather", 12, true);

var titleText;
var master;

titleText = StaticText().string_("Master Output");
titleText.font_(font3);

master = SSGui_MasterStrip(~ss.mixer);
master = master.strip;

window.layout = VLayout(
  [titleText, \a: \center],
  15,
  [master, \a: \center]
);

window.alwaysOnTop = true;
window.front;
