/**
 * @namespace xml
 * @dependsOn lang.js
 * @dependsOn fs.js
 * @dependsOn MSXML2.DOMDocument
 * @dependsOn MSXML2.MXXMLWriter
 * @dependsOn MSXML2.SAXXMLReader
 */
var xml = {};

!function() {
  
  xml.save = function(filename, xmldoc) {
    if (typeof xmldoc !== 'string') {
      xmldoc = xmldoc.xml;
    }
    
    var writer = WScript.createObject('MSXML2.MXXMLWriter');
    writer.omitXMLDeclaration = true;
    // writer.version = '1.0';
    // writer.encoding = 'utf-8';
    // writer.standalone = true;
    writer.indent = true;
    
    var reader = WScript.createObject('MSXML2.SAXXMLReader');
    reader.contentHandler = writer;
    reader.parse(xmldoc);
    
    fs.save(filename, '<?xml version="1.0" encoding="Shift_JIS" standalone="true"?>\n' + writer.output);
  };
  
  
  var conv = function(dom, json) {
    switch (typeof json) {
      case 'string':
      case 'number':
      case 'boolean':
        return conv.scala(dom, json);
      case 'function':
        throw new Error('unsupported type: function');
      case 'object':
        if (json instanceof Array) {
          return conv.array(dom, json);
        }
        else if (json != null) {
          return conv.object(dom, json);
        }
        // if null, fall through
      case 'undefined':
        return conv.scala(dom, '');
    }
  }
  conv.array = function(dom, list) {
    var node = dom.createElement('list');
    list.foreach(function(value, index) {
      var item = dom.createElement('item');
      item.setAttribute('name', index);
      item.appendChild( conv(dom, value) );
      node.appendChild(item);
    });
    return node;
  };
  conv.object = function(dom, obj) {
    var node = dom.createElement('object');
    for (var key in obj) if (obj.hasOwnProperty(key)) {
      var item = dom.createElement('item');
      item.setAttribute('name', key);
      item.appendChild( conv(dom, obj[key]) );
      node.appendChild(item);
    }
    return node;
  };
  conv.scala = function(dom, value) {
    return dom.createTextNode(value + '');
  };
  
  
  xml.convert = function(json) {
    var dom = WScript.createObject('MSXML.DOMDocument');
    
    var root = dom.createElement('root');
    
    root.appendChild(conv(dom, json));
    
    dom.appendChild(root);
    
    return dom;
  };
  
}();
