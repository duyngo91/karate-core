(function() {
  'use strict';

  // ========================================
  // CONFIGURATION
  // ========================================
  var CONFIG = {
    maxElements: 500,
    captureHidden: true,
    captureIframes: true,
    captureShadowDOM: true,
  };

  // ========================================
  // DETECTION: Framework & Page Type
  // ========================================
  function detectFramework() {
    var frameworks = {
      angular: !!window.angular || !!window.getAllAngularRootElements ||
               !!document.querySelector('[ng-version]'),
      react: !!document.querySelector('[data-reactroot], [data-reactid]') ||
             !!(window.React || window.ReactDOM),
      vue: !!window.Vue || !!document.querySelector('[data-v-]'),
      jquery: !!window.jQuery || !!window.$,
      bootstrap: !!window.bootstrap || !!document.querySelector('.modal, .navbar'),
      materialUI: !!document.querySelector('mat-form-field, mat-select, mat-checkbox'),
    };

    return Object.keys(frameworks).filter(function(k) { return frameworks[k]; });
  }

  // ========================================
  // CHECK IF ELEMENT IS INTERACTIVE
  // ========================================
  function isInteractive(el) {
    var interactiveTags = ['button', 'a', 'input', 'select', 'textarea'];
    var interactiveRoles = ['button', 'link', 'textbox', 'combobox', 'checkbox', 'tab'];
    var tag = el.tagName.toLowerCase();
    var role = el.getAttribute('role');

    return (
      interactiveTags.indexOf(tag) !== -1 ||
      (role && interactiveRoles.indexOf(role) !== -1) ||
      el.hasAttribute('onclick') ||
      el.hasAttribute('ng-click') ||
      el.hasAttribute('@click') ||
      el.hasAttribute('v-on:click') ||
      el.classList.contains('btn') ||
      el.classList.contains('clickable')
    );
  }

  // ========================================
  // HELPERS
  // ========================================
  function getDepth(el) {
    var depth = 0;
    var current = el;
    while (current.parentElement) {
      depth++;
      current = current.parentElement;
    }
    return depth;
  }

  function getXPath(el) {
    if (el.id) return '//*[@id="' + el.id + '"]';

    var path = [];
    var current = el;

    while (current && current.nodeType === 1) {
      var index = 0;
      var sibling = current.previousSibling;

      while (sibling) {
        if (sibling.nodeType === 1 && sibling.tagName === current.tagName) {
          index++;
        }
        sibling = sibling.previousSibling;
      }

      var tagName = current.tagName.toLowerCase();
      var pathIndex = index > 0 ? '[' + (index + 1) + ']' : '';
      path.unshift(tagName + pathIndex);

      current = current.parentElement;
    }

    return '/' + path.join('/');
  }

  function getCssSelector(el) {
    if (el.id) return '#' + el.id;

    if (el.className && typeof el.className === 'string') {
      var classes = el.className.trim().split(/\s+/);
      for (var i = 0; i < classes.length; i++) {
        var cls = classes[i];
        if (cls && document.querySelectorAll('.' + cls).length === 1) {
          return '.' + cls;
        }
      }
    }

    var testId = el.getAttribute('data-testid') ||
                 el.getAttribute('data-test') ||
                 el.getAttribute('automation-id');
    if (testId) {
      return '[data-testid="' + testId + '"]';
    }

    return null;
  }

  // ========================================
  // EXTRACT ELEMENT INFO
  // ========================================
  function extractElementInfo(el, context) {
    var rect = el.getBoundingClientRect();
    var isVisible = el.offsetWidth > 0 && el.offsetHeight > 0;

    var styles = window.getComputedStyle(el);
    var actuallyVisible = isVisible &&
                          styles.display !== 'none' &&
                          styles.visibility !== 'hidden' &&
                          parseFloat(styles.opacity) > 0;

    var attrs = {};
    for (var i = 0; i < el.attributes.length; i++) {
      var attr = el.attributes[i];
      attrs[attr.name] = attr.value;
    }

    var form = el.closest('form');
    var section = el.closest('section, article, main, aside, header, footer, nav');

    var label = null;
    if (['INPUT', 'SELECT', 'TEXTAREA'].indexOf(el.tagName) !== -1) {
      if (el.labels && el.labels[0]) {
        label = el.labels[0].textContent.trim();
      } else {
        var container = el.closest('mat-form-field, .form-group, .form-field');
        if (container) {
          var matLabel = container.querySelector('label, mat-label');
          if (matLabel) label = matLabel.textContent.trim();
        }
        if (!label && el.previousElementSibling && el.previousElementSibling.tagName === 'LABEL') {
          label = el.previousElementSibling.textContent.trim();
        }
      }
    }

    return {
      tag: el.tagName.toLowerCase(),
      type: el.type || null,
      text: (el.textContent || el.value || '').trim().substring(0, 100),
      label: label,

      id: el.id || null,
      name: el.name || null,
      className: el.className || null,
      attributes: attrs,

      href: el.href || null,

      position: {
        x: Math.round(rect.left),
        y: Math.round(rect.top),
        width: Math.round(rect.width),
        height: Math.round(rect.height)
      },

      visibility: {
        visible: actuallyVisible,
        inViewport: rect.top >= -100 && rect.top <= window.innerHeight + 100,
        display: styles.display,
        opacity: styles.opacity
      },

      context: {
        source: context,
        formId: form ? (form.id || form.name || 'unnamed-form') : null,
        sectionId: section ? (section.id || section.tagName.toLowerCase()) : null,
        depth: getDepth(el)
      },

      locators: {
        xpath: getXPath(el),
        css: getCssSelector(el)
      }
    };
  }

  // ========================================
  // EXTRACT FROM SHADOW DOM
  // ========================================
  function extractFromShadowDOM(root, results) {
    if (!CONFIG.captureShadowDOM) return;

    var shadowHosts = root.querySelectorAll('*');
    for (var i = 0; i < shadowHosts.length; i++) {
      var host = shadowHosts[i];
      if (host.shadowRoot) {
        var shadowElements = host.shadowRoot.querySelectorAll('*');
        for (var j = 0; j < shadowElements.length; j++) {
          var el = shadowElements[j];
          if (isInteractive(el)) {
            results.push(extractElementInfo(el, 'shadow-dom'));
          }
        }
        extractFromShadowDOM(host.shadowRoot, results);
      }
    }
  }

  // ========================================
  // EXTRACT FROM IFRAMES
  // ========================================
  function extractFromIframes(results) {
    if (!CONFIG.captureIframes) return;

    var iframes = document.querySelectorAll('iframe');
    for (var i = 0; i < iframes.length; i++) {
      try {
        var iframe = iframes[i];
        var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
        if (iframeDoc) {
          var iframeElements = iframeDoc.querySelectorAll('*');
          for (var j = 0; j < iframeElements.length; j++) {
            var el = iframeElements[j];
            if (isInteractive(el)) {
              results.push(extractElementInfo(el, 'iframe-' + i));
            }
          }
        }
      } catch (e) {
        console.warn('Cannot access iframe ' + i + ':', e.message);
      }
    }
  }

  // ========================================
  // EXTRACT HIDDEN CONTENT
  // ========================================
  function extractHiddenContent() {
    var hidden = {
      collapsed: [],
      carousels: [],
      tabs: [],
      modals: []
    };

    if (!CONFIG.captureHidden) return hidden;

    // Collapsed sections
    var collapsedElements = document.querySelectorAll('.collapse, [aria-expanded="false"]');
    for (var i = 0; i < collapsedElements.length; i++) {
      var el = collapsedElements[i];
      var links = [];
      var linkElements = el.querySelectorAll('a');
      for (var j = 0; j < linkElements.length; j++) {
        links.push({
          text: linkElements[j].textContent.trim(),
          href: linkElements[j].href
        });
      }

      if (links.length > 0) {
        hidden.collapsed.push({
          index: i,
          id: el.id,
          links: links
        });
      }
    }

    // Carousels
    var carousels = document.querySelectorAll('carousel, .carousel, [class*="slider"], [class*="swiper"]');
    for (var i = 0; i < carousels.length; i++) {
      var carousel = carousels[i];
      var slideElements = carousel.querySelectorAll('slide, .carousel-item, [class*="slide"]');
      var slides = [];

      for (var j = 0; j < slideElements.length; j++) {
        var slide = slideElements[j];
        slides.push({
          index: j,
          active: slide.classList.contains('active'),
          text: slide.textContent.trim().substring(0, 200)
        });
      }

      if (slides.length > 1) {
        hidden.carousels.push({
          index: i,
          totalSlides: slides.length,
          slides: slides
        });
      }
    }

    // Tabs
    var tabs = document.querySelectorAll('[role="tabpanel"], .tab-pane, .tab-content > div');
    for (var i = 0; i < tabs.length; i++) {
      var tab = tabs[i];
      hidden.tabs.push({
        index: i,
        id: tab.id,
        visible: !tab.hasAttribute('hidden') && tab.style.display !== 'none'
      });
    }

    // Modals
    var modals = document.querySelectorAll('.modal, [role="dialog"]');
    for (var i = 0; i < modals.length; i++) {
      var modal = modals[i];
      hidden.modals.push({
        index: i,
        id: modal.id,
        visible: modal.style.display === 'block' || modal.classList.contains('show')
      });
    }

    return hidden;
  }

  // ========================================
  // MAIN EXTRACTION - ĐỒNG BỘ
  // ========================================
  var pageData = {
//    meta: {
//      title: document.title,
//      url: window.location.href,
//      timestamp: new Date().toISOString(),
//      frameworks: detectFramework(),
//      viewport: {
//        width: window.innerWidth,
//        height: window.innerHeight
//      }
//    },

    elements: [],
    hiddenContent: null,

//    stats: {
//      totalElements: 0,
//      visibleElements: 0,
//      interactiveElements: 0
//    }
  };

  // Extract from main document
  var allElements = document.querySelectorAll('*');
  var count = 0;

  for (var i = 0; i < allElements.length && count < CONFIG.maxElements; i++) {
    var el = allElements[i];
    if (isInteractive(el)) {
      pageData.elements.push(extractElementInfo(el, 'main'));
      count++;
    }
  }

  // Extract from Shadow DOM
  extractFromShadowDOM(document, pageData.elements);

  // Extract from iframes
  extractFromIframes(pageData.elements);

  // Extract hidden content
  pageData.hiddenContent = extractHiddenContent();

  // Calculate stats
//  pageData.stats.totalElements = pageData.elements.length;
//  pageData.stats.visibleElements = pageData.elements.filter(function(e) {
//    return e.visibility.visible;
//  }).length;
//  pageData.stats.interactiveElements = pageData.elements.length;

  // TRỰC TIẾP TRẢ VỀ KẾT QUẢ - KHÔNG DÙNG PROMISE
  //return JSON.stringify(pageData, null, 2);
  return pageData;
})();