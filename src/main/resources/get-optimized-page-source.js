(function() {
  var MAX_ELEMENTS = 500; // Giới hạn để tránh quá tải

  var summary = {
    title: document.title,
    url: window.location.href,
    viewport: {
      width: window.innerWidth,
      height: window.innerHeight
    },
    elements: []
  };

  // Helper functions
  function getDomIndex(el) {
    if (!el || !el.parentElement) return -1;
    return Array.from(el.parentElement.children).indexOf(el);
  }

  function findFormContainer(el) {
    let cur = el;
    while (cur) {
      if (
        cur.tagName === 'FORM' ||
        cur.getAttribute('role') === 'form' ||
        cur.className?.toLowerCase().includes('form')
      ) {
        return cur;
      }
      cur = cur.parentElement;
    }
    return null;
  }

  function getXPath(el) {
    if (el.id) return `//*[@id="${el.id}"]`;

    var path = [];
    while (el && el.nodeType === 1) {
      var idx = 0;
      var sibling = el.previousSibling;
      while (sibling) {
        if (sibling.nodeType === 1 && sibling.tagName === el.tagName) idx++;
        sibling = sibling.previousSibling;
      }
      var tagName = el.tagName.toLowerCase();
      path.unshift(tagName + (idx > 0 ? `[${idx + 1}]` : ''));
      el = el.parentElement;
    }
    return '/' + path.join('/');
  }

  // Interactive elements
  var selectors = [
    'button',
    'a[href]',
    'input',
    'select',
    'textarea',
    'mat-form-field',
    'mat-select',
    '[role="button"]',
    '[role="link"]',
    '[onclick]'
  ];

  var elements = document.querySelectorAll(selectors.join(','));
  var count = 0;

  for (var i = 0; i < elements.length && count < MAX_ELEMENTS; i++) {
    var el = elements[i];

    // Chỉ lấy element visible
    if (el.offsetWidth === 0 || el.offsetHeight === 0) continue;

    var rect = el.getBoundingClientRect();

    // Chỉ lấy element trong viewport hoặc gần viewport
    if (rect.bottom < -100 || rect.top > window.innerHeight + 100) continue;

    var label = null;
    if (el.tagName === 'INPUT' || el.tagName === 'SELECT' || el.tagName === 'TEXTAREA') {
      if (el.labels && el.labels[0]) {
        label = el.labels[0].textContent.trim();
      } else {
        var matLabel = el.closest('mat-form-field')?.querySelector('mat-label');
        label = matLabel ? matLabel.textContent.trim() : null;
      }
    }

    var container = findFormContainer(el);

    summary.elements.push({
      index: count,
      tag: el.tagName.toLowerCase(),
      type: el.type || null,
      text: (el.innerText || el.value || '').trim().substring(0, 100),
      label: label,
      id: el.id || null,
      name: el.name || null,
      className: el.className || null,
      placeholder: el.placeholder || null,
      href: el.href || null,

      // Position
      x: Math.round(rect.left),
      y: Math.round(rect.top),
      width: Math.round(rect.width),
      height: Math.round(rect.height),

      // Context
      formId: container ? (container.id || container.className) : null,
      depth: (function() {
        var d = 0, p = el.parentElement;
        while(p) { d++; p = p.parentElement; }
        return d;
      })(),

      // Locators
      xpath: getXPath(el),
      cssSelector: el.id ? `#${el.id}` : null
    });

    count++;
  }

  return JSON.stringify(summary, null, 2);
})()