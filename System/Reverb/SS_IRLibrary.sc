/*
Sunday, January 12th 2014

IM_IRLibrary.sc

*/

SS_IRLibrary {

  var server;
  var <irDict;
  var fftBaseSize, fftWindowMul;

  *new {
    | path = "~/Library/Application Support/SuperCollider/Extensions/Impatience Machine/System/Reverb/Impulse Responses", fftMul = 2 |
    ^super.new.prInit(path, fftMul);
  }

  prInit { | path, fftMul |
    var pathName = PathName(path);
    server = Server.default;

   server.waitForBoot {
      fftBaseSize = 1024;
      fftWindowMul = fftMul;

      irDict = IdentityDictionary.new;

      pathName.filesDo { | path |
        var key = path.fileNameWithoutExtension.asSymbol;
        this.prPrepareIRBuf(key, path.fullPath);
      };
    };
  }

  prPrepareIRBuf { | key, pathString |
    var buffer, bufSize, irBuf;
    var server = Server.default;

    fork {
      buffer = Buffer.read(
        server, pathString,
        action: { | buf | bufSize = PartConv.calcBufSize(fftBaseSize * fftWindowMul, buf) }
      );

      server.sync;

      irBuf = Buffer.alloc(server, bufSize, 1);
      irBuf.preparePartConv(buffer, fftBaseSize * fftWindowMul);

      server.sync;

      buffer.free;
      irDict.put(key, irBuf);
      server.sync;
    };
  }

  prFreeIRDict {
    irDict.do({ | item | item.free; });
  }

  irKeys {
    ^irDict.keys;
  }


  returnIRDictKeyFromIndex { }


}

