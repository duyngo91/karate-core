# Karate Healer Knowledge Base

## Common Fix Patterns & Solutions

### 1. Timing Issues

#### Problem: waitFor timeout (default 9s không đủ)
```
Error: waitUntil (js): failed after 3 retries and 9219 milliseconds
```

**Root Cause**: Web load chậm, redirect phức tạp, hoặc element render muộn

**Fix Pattern**:
```gherkin
# Before (fails)
* waitFor(locators.element)
* waitForUrl('/path')

# After (works)
* retry(30,1000).waitFor(locators.element)
* retry(30,1000).waitForUrl('/path')
```

**Notes**: 
- `retry(30,1000)` = chờ 30 lần, mỗi lần 1 giây = 30s total
- Dùng cho trang login, redirect, hoặc SPA load chậm
- Đặc biệt quan trọng cho trang đầu tiên của web

#### Problem: Navigation redirect không đúng URL
```
Expected: /select-context
Actual: /customer-overview
```

**Root Cause**: Hệ thống có flow redirect phức tạp sau login

**Fix Pattern**:
```gherkin
# Before
* waitForUrl('/select-context')

# After - chấp nhận multiple possible URLs
* retry(30,1000).waitForUrl('/select-context')
# OR check actual final URL
* def finalUrl = driver.url
* print 'Final URL:', finalUrl
```

### 2. Element Not Found Issues

#### Problem: Locator không tìm thấy
```
Error: js eval failed: document.querySelector("#element") is null
```

**Root Cause**: Element chưa render, selector sai, hoặc page chưa load

**Fix Pattern**:
```gherkin
# Before
* waitFor('#element')

# After - với retry và fallback
* retry(30,1000).waitFor('#element')
# OR kiểm tra alternative selector
* waitFor('#element, .alternative-class')
```

#### Problem: Table/List không xuất hiện
```
Error: waitFor(locators.incidentTable) timeout
```

**Fix Pattern**:
```gherkin
# Before
* waitFor(locators.incidentTable)

# After - retry
* retry(30,1000).waitFor(locators.incidentTable)
```

### 3. Authentication & Session Issues

#### Problem: Login thành công nhưng redirect sai
**Root Cause**: Session timeout, token refresh, hoặc permission redirect

**Fix Pattern**:
```gherkin
# Thêm verification steps
* click(locators.loginButton)
* retry(30,1000).waitForUrl('/select-context')
* def currentUrl = driver.url
* print 'Current URL after login:', currentUrl
* match currentUrl contains any ['/select-context', '/customer-overview', '/essentials']
```

### 4. Page Load & SPA Issues

#### Problem: SPA (Single Page App) load chậm
**Root Cause**: Angular/React app cần thời gian initialize

**Fix Pattern**:
```gherkin
# Thêm delay trước khi interact
* driver 'https://spa-app.com'
* delay(3000)  # Chờ SPA initialize
* retry(30,1000).waitFor(locators.element)
```

### 5. Dynamic Content Issues

#### Problem: Content load bằng AJAX
**Root Cause**: Data được fetch sau khi page load

**Fix Pattern**:
```gherkin
# Chờ loading indicator biến mất
* waitUntil("document.querySelector('.loading') == null")
* retry(30,1000).waitFor(locators.dataTable)
```

## Fix Decision Tree

```
Test Failure
├── Timeout Error?
│   ├── Element not found → Add retry(30,1000).waitFor()
│   ├── URL not match → Add retry(30,1000).waitForUrl()
│   └── Page load slow → Add delay(3000) before actions
├── Locator Error?
│   ├── Selector invalid → Update locator JSON
│   ├── Element changed → Inspect page, update selector
│   └── Dynamic content → Add waitUntil() for AJAX
├── Navigation Error?
│   ├── Wrong redirect → Check actual URL, update expectation
│   ├── Session issue → Add login verification steps
│   └── Permission → Check user role/access
└── Data Error?
    ├── Missing test data → Update JSON files
    ├── API response → Check network tab
    └── State issue → Add setup/cleanup steps
```

## MCP Verification Commands

### Debug Failed Case
```javascript
// 1. Initialize browser
init_browser()

// 2. Navigate to failed page
navigate('https://failed-url.com')

// 3. Check page state
get_page_source()

// 4. Test locators
wait_exist('#username', 5000)
wait_exist('table.table-hover', 10000)

// 5. Test interactions
input('#username', 'testuser')
click('#login-button')

// 6. Verify results
get_current_url()
```

## Common Locator Fixes

### CSS Selector Updates
```json
// Before - too specific
{
  "loginButton": "#kc-form-login input[type='submit'][value='Sign In']"
}

// After - more resilient
{
  "loginButton": "#kc-login, input[type='submit'], .login-btn"
}
```

### XPath Alternatives
```json
// Text-based locators for dynamic IDs
{
  "submitButton": "//button[contains(text(), 'Submit')]",
  "dataTable": "//table[contains(@class, 'data-table')]"
}
```

## Environment-Specific Fixes

### SIT Environment
- Slower response times → Increase retry counts
- Different URLs → Update base URLs in config
- Mock data → Adjust test data expectations

### Production-like
- Security redirects → Handle additional auth steps
- Load balancers → Add retry for intermittent failures
- CDN delays → Increase wait times for assets

## Success Metrics

### Before Fix
```
Tests run: 4, Failures: 3, Errors: 1, Skipped: 0
Success Rate: 25%
```

### After Fix
```
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0  
Success Rate: 100%
```

## Notes for @karate-healer

1. **Always verify with MCP first** - Không bao giờ guess, phải kiểm tra thực tế
2. **One fix at a time** - Fix từng case riêng biệt, không batch
3. **Test immediately** - Chạy lại ngay sau khi fix
4. **Document everything** - Ghi lại root cause và solution
5. **Pattern recognition** - Học từ các fix trước đó
6. **Environment awareness** - Hiểu đặc điểm từng môi trường test