/**
 * @namespace fs
 * @dependsOn Scripting.FileSystemObject
 */
var fs = {};

!function() {
  
  fs.OVERWRITE = true;
  
  var fso = WScript.createObject('Scripting.FileSystemObject');
  
  fs.getDir = function(path) {
    return fso.getFolder(path);
  };
  
  fs.save = function(filename, contents) {
    var stream = fso.createTextFile(filename, fs.OVERWRITE);
    try {
      stream.write(contents);
    }
    finally {
      stream.close();
    }
  };
  
}();
