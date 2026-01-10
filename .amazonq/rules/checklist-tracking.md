# Unified Test Tracking System

## Overview
Chỉ tạo 2 files cho mỗi feature:
1. **Test Plan** - Scenarios và requirements
2. **Unified Checklist** - Track toàn bộ PLAN → DEV → TEST → HEAL

## Unified Checklist Format
```markdown
# {Feature Name} - Test Checklist

## PLAN Phase
- [ ] PLAN-001: Analyze UI structure
- [ ] PLAN-002: Identify test scenarios  
- [ ] PLAN-003: Create test plan

## DEV Phase
- [ ] DEV-001: Record Scenario 1 - [Name]
- [ ] DEV-002: Record Scenario 2 - [Name]
- [ ] DEV-003: Generate feature files
- [ ] DEV-004: Create page objects
- [ ] DEV-005: **REFACTOR** - Tách riêng LoginPage và FeaturePage
- [ ] DEV-006: **REFACTOR** - Gộp các hàm tương tự thành 1 hàm chung
- [ ] DEV-007: **REFACTOR** - Cập nhật config paths trong karate-config-path.json
- [ ] DEV-008: **REFACTOR** - Tạo locator JSON files
- [ ] DEV-009: **REFACTOR** - Sử dụng global config variables
- [ ] DEV-010: **REFACTOR** - Tái sử dụng environment variables (domain level)
- [ ] DEV-011: **FIX** - Syntax corrections

## TEST Phase
- [ ] TEST-001: Run TC001 - {Scenario Name} - Status: PENDING
- [ ] TEST-002: Run TC002 - {Scenario Name} - Status: PENDING
- [ ] TEST-003: Run TC003 - {Scenario Name} - Status: PENDING
- [ ] TEST-004: Validate results

## HEAL Phase (if needed)
- [ ] HEAL-001: Fix {Case Name} - Attempt 1/3
- [ ] HEAL-002: Fix {Case Name} - Attempt 2/3  
- [ ] HEAL-003: Fix {Case Name} - Attempt 3/3
- [ ] HEAL-004: Update final status
```

## Structure Documentation Format
```markdown
## DEV Phase
- [✓] DEV-XXX: Task name - Done: dd-MM-yyyy HH:MM
  ```
  Structure Changes:
  src/test/java/web/
  ├── features/{app}/{module}/
  │   └── {Feature}.feature (main test cases)
  ├── pages/{app}/{module}/
  │   └── {Feature}Page.feature
  └── locators/web/{app}/{module}/
      └── {Feature}Page.json
  ```
```

**Ví dụ thực tế:**
```markdown
- [✓] DEV-006: Create page objects - Done: 07-01-2025 10:50
  ```
  Structure:
  src/test/java/web/
  ├── features/tci/danh_sach_lead/
  │   └── QLLead.feature
  ├── pages/tci/quan_ly_lead/
  │   ├── QLLeadPage.feature
  │   └── TaoYCBHPage.feature
  └── locators/web/tci/danh_sach_lead/
      ├── QLLeadPage.json
      └── TaoYCBHPage.json
  ```
```

## Checklist Format Rules
- **Timestamp**: Add `Done: dd-MM-yyyy HH:MM` when completed
- **Notes**: Add error details for failed items
- **Structure**: Show file/folder changes in code blocks
- **Example**:
  ```
  - [✓] PLAN-001: Analyze UI - Done: 30-12-2025 13:45
  - [✗] TEST-001: Run TC001 - Done: 30-12-2025 14:20
    Notes: Login timeout, scriptAll syntax error
  ```

## Status Values
- `[ ]` PENDING - Not started
- `[~]` IN_PROGRESS - Currently working
- `[✓]` DONE - Test passed
- `[✗]` FAILED - Test failed
- `[⚠]` BLOCKED - Cannot proceed
- `[🚫]` SERVER_DOWN - Server unavailable (503/502/404)

## File Locations
- **Test Plan**: `src/test/plans/{feature}-plan.md`
- **Unified Checklist**: `src/test/plans/{feature}-checklist.md`

## Agent Integration

### @karate-planner
- Creates test plan + initial checklist
- Updates PLAN-* items

### @karate-generator  
- Updates DEV-* items during recording
- Generates feature files
- Documents structure changes

### @karate-executor
- Chạy TẤT CẢ tests ngay lập tức
- Updates TEST-* items với detailed failure reasons
- Parses surefire-reports + karate-json reports
- Handover failed cases to @karate-healer

### @karate-healer
- Fix từng failed case riêng biệt
- Tối đa 3 attempts mỗi case
- Sử dụng karate-json report để phân tích
- Updates HEAL-* items với attempt tracking

### @karate-reporter
- Reads unified checklist
- Generates summary from test plan + checklist
