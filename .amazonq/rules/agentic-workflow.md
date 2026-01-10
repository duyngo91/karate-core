# Karate Agentic QA Workflow

## Workflow Command
Khi user nói:
- "agentic workflow cho {feature}"
- "PLAN → DEV → TEST → HEAL for {feature}"

Hệ thống tự động kích hoạt 5 agents theo thứ tự:

---

## Bước 1: PLAN Phase
**@karate-planner**

Agent sẽ tự động:
- Đọc workflow từ `.amazonq/chatmodes/karate-planner.md`
- Thực hiện theo Workflow section trong file đó
- Tạo test plan và unified checklist
- Báo cáo khi hoàn thành

---

## Bước 2: DEV Phase
**@karate-generator**

Agent sẽ tự động:
- Đọc workflow từ `.amazonq/chatmodes/karate-generator.md`
- Thực hiện theo Workflow section (Bước 0→1→2→3→4→5)
- Generate feature files với ScriptRecorder
- Refactor theo Page Object Model
- Báo cáo khi hoàn thành

---

## Bước 3: TEST Phase
**@karate-executor**

Agent sẽ tự động:
- Đọc workflow từ `.amazonq/chatmodes/karate-executor.md`
- Chạy tất cả tests
- Parse results và cập nhật checklist
- Báo cáo kết quả

---

## Bước 4: HEAL Phase (if needed)
**@karate-healer**

Agent sẽ tự động:
- Đọc workflow từ `.amazonq/chatmodes/karate-healer.md`
- Fix từng failed case riêng biệt
- Tối đa 3 attempts mỗi case
- Báo cáo kết quả healing

---

## Bước 5: REPORT Phase
**@karate-reporter**

Agent sẽ tự động:
- Đọc workflow từ `.amazonq/chatmodes/karate-reporter.md`
- Tổng hợp test plan + checklist
- Generate final summary report
- Đưa ra recommendations

---

## Agent Responsibilities

Mỗi agent PHẢI:
1. ✅ Đọc workflow file của mình (`.amazonq/chatmodes/{agent-name}.md`)
2. ✅ Tuân thủ đúng Workflow section
3. ✅ Cập nhật unified checklist
4. ✅ Báo cáo khi hoàn thành

---

## Success Criteria

Workflow thành công khi:
- ✅ Tất cả 5 phases hoàn thành
- ✅ Unified checklist fully updated
- ✅ Test success rate ≥ target threshold
- ✅ Final report generated