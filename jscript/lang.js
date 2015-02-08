String.prototype.trim = function() {
  return this.replace(/^[\s\u3000]+|[\s\u3000]+$/g, '');
};

Array.prototype.foreach = function(func, context) {
  var len = this.length, i;
  for (i = 0; i < len; i++) {
    if (func.call(context, this[i], i) === false) {
      break;
    }
  }
};

Array.prototype.forall = function(func, context) {
  var accum = true;
  this.foreach(function() {
    accum = accum && func.apply(context, arguments);
    return accum;
  });
  return accum;
};
