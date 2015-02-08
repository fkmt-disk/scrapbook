/**
 * @namespace excel
 * @dependsOn lang.js
 * @dependsOn Excel.Application
 */
var excel = {};

!function() {
  
  excel.UPDATE_LINKS = false;
  
  excel.READ_ONLY = true;
  
  excel.SAVE_CHANGES = false;
  
  excel.MAX_ROW = 65536;
  
  excel.MAX_COL = 256;
  
  excel.default_format = function(value) {
    return value == null ? value : String(value).trim();
  };
  
  excel.default_isEmpty = function(value) {
    return value == null || value.length === 0;
  };
  
  excel.open = function(filename, func, context) {
    var excelApp = WScript.createObject('Excel.Application');
    try {
      var saveChanges;
      var workbook = excelApp.workbooks.open(filename, excel.UPDATE_LINKS, excel.READ_ONLY);
      try {
        var result = func.call(context, workbook);
        saveChanges = typeof result === 'boolean' ? result : excel.SAVE_CHANGES;
      }
      finally {
        workbook.close(saveChanges);
      }
    }
    finally {
      excelApp = null;
      CollectGarbage();
    }
  };
  
  excel.parseTable = function(params) {
    var sheet     = params.sheet,
        start_row = params.start_row,
        end_row   = params.end_row,
        start_col = params.start_col,
        end_col   = params.end_col,
        format    = params.format,
        isEmpty   = params.isEmpty;
    
    var row_limit = typeof end_row === 'number';
    end_row = row_limit ? end_row : excel.MAX_ROW;
    
    var col_limit = typeof end_col === 'number';
    end_col = col_limit ? end_col : excel.MAX_COL;
    
    format = typeof format === 'function' ? format : excel.default_format;
    isEmpty = typeof isEmpty === 'function' ? isEmpty : excel.default_isEmpty;
    
    var isAllEmpty = function(list) {
      return list.forall(isAllEmpty.empty);
    };
    isAllEmpty.empty = function(value) {
      return isEmpty(value);
    };
    
    var labels = excel.parseRow({
      sheet     : sheet,
      row       : start_row,
      start_col : start_col,
      end_col   : (col_limit ? end_col : null),
      format    : format,
      isEmpty   : isEmpty
    });
    
    var list = [];
    
    for (var row = start_row + 1; row <= end_row; row++) {
      var values = excel.parseRow({
        sheet     : sheet,
        row       : row,
        start_col : start_col,
        end_col   : (col_limit ? end_col : labels.length),
        format    : format,
        isEmpty   : isEmpty
      });
      
      if (isAllEmpty(values)) {
        break;
      }
      
      list.push(values);
    }
    
    list.unshift(labels);
    
    return list;
  };
  
  excel.parseRow = function(params) {
    var sheet     = params.sheet,
        row       = params.row,
        start_col = params.start_col,
        end_col   = params.end_col,
        format    = params.format,
        isEmpty   = params.isEmpty;
    
    var limit = typeof end_col === 'number';
    end_col = limit ? end_col : excel.MAX_COL;
    
    format = typeof format === 'function' ? format : excel.default_format;
    isEmpty = typeof isEmpty === 'function' ? isEmpty : excel.default_isEmpty;
    
    var list = [];
    
    for (var col = start_col; col <= end_col; col++) {
      var value = format(sheet.cells(row, col).value);
      if (limit === false && isEmpty(value)) {
        break;
      }
      list.push(value);
    }
    
    return list;
  };
  
}();
