(function() {
  var summary = {
    title: document.title,
    url: window.location.href,
    forms: [],
    tables: [],
    buttons: [],
    inputs: [],
    selects: [],
    links: []
  };
  
  document.querySelectorAll('form').forEach(function(form, idx) {
    summary.forms.push({
      index: idx,
      id: form.id || 'N/A',
      action: form.action || 'N/A',
      method: form.method || 'GET',
      inputCount: form.querySelectorAll('input, select, textarea').length
    });
  });
  
  document.querySelectorAll('table').forEach(function(table, idx) {
    var headers = Array.from(table.querySelectorAll('th')).map(function(th) { return th.textContent.trim(); });
    summary.tables.push({
      index: idx,
      id: table.id || 'N/A',
      class: table.className || 'N/A',
      headers: headers,
      rowCount: table.querySelectorAll('tbody tr').length
    });
  });
  
  document.querySelectorAll('button').forEach(function(btn, idx) {
    if (idx < 20) {
      summary.buttons.push({
        index: idx,
        text: btn.textContent.trim().substring(0, 50),
        type: btn.type || 'button',
        id: btn.id || 'N/A',
        class: btn.className.substring(0, 50) || 'N/A'
      });
    }
  });
  
  document.querySelectorAll('mat-form-field').forEach(function(field, idx) {
    if (idx < 20) {
      var label = field.querySelector('mat-label, label');
      var input = field.querySelector('input, textarea, mat-select');
      if (input) {
        summary.inputs.push({
          index: idx,
          label: label ? label.textContent.trim() : 'N/A',
          id: input.id || 'N/A',
          type: input.type || input.tagName,
          name: input.name || 'N/A',
          placeholder: input.placeholder || 'N/A',
          class: input.className.substring(0, 50) || 'N/A'
        });
      }
    }
  });
  
  document.querySelectorAll('mat-slide-toggle, mat-checkbox').forEach(function(toggle, idx) {
    if (idx < 20) {
      var label = toggle.textContent.trim();
      var input = toggle.querySelector('input');
      if (input) {
        summary.inputs.push({
          index: summary.inputs.length,
          label: label || 'N/A',
          id: input.id || 'N/A',
          type: 'checkbox',
          name: input.name || 'N/A',
          checked: input.checked,
          class: input.className.substring(0, 50) || 'N/A'
        });
      }
    }
  });
  
  document.querySelectorAll('select, ng-select, [role="combobox"]').forEach(function(sel, idx) {
    if (idx < 20) {
      var options = sel.tagName === 'SELECT' ? Array.from(sel.options).map(function(o) { return o.text; }) : [];
      summary.selects.push({
        index: idx,
        id: sel.id || 'N/A',
        name: sel.name || 'N/A',
        class: sel.className.substring(0, 50) || 'N/A',
        optionCount: options.length,
        options: options.slice(0, 10)
      });
    }
  });
  
  document.querySelectorAll('a[href]').forEach(function(link, idx) {
    if (idx < 20) {
      summary.links.push({
        index: idx,
        text: link.textContent.trim().substring(0, 50),
        href: link.href,
        id: link.id || 'N/A'
      });
    }
  });
  
  return JSON.stringify(summary, null, 2);
})()
