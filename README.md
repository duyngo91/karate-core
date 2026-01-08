
# Project Beta

## 1. Mục tiêu:
* Mở rộng cho web, mobile trong Karate bằng cách tích hợp driver của karate thông qua com.intuit.karate.Runner.customDrivers.

## 2. Lợi ích:
* Thêm log hoặc các thuộc tính của driver khi khỏi tạo.
* Mở rông thêm các hàm phục vụ cho web hoặc mobile mà driver default không có.
* *Không sửa hay làm ảnh hướng gì đến các hàm đã có sẵn của karate driver*
---
## 3. Các đối tượng chính:
* *ChromeCustom* : dùng để mở rộng các hàm liên quan đến web
* *MobileCustom* : dùng để mở rộng các hàm liên quan đến mobile

## 4. Cách sử dụng: [chi tiết](https://confluence.techcombank.com.vn/pages/viewpage.action?pageId=553950208)

## 5. Self-Healing Locators:
* Tự động tìm và sửa locators khi UI thay đổi
* Giảm maintenance test scripts
* Sử dụng AI để học và cải thiện locators theo thời gian

### 📚 Tài liệu:
* [Hướng dẫn cấu hình](HEALING_SETUP.md) - Setup self-healing cho dự án mới
* [Ví dụ chi tiết](HEALING_EXAMPLES.md) - Các ví dụ cụ thể và use cases

### ⚡ Quick Start:
```javascript
// 1. Thêm vào karate-config.js
karate.callSingle('classpath:healing-loader.js');

// 2. Tạo locators JSON
// src/test/resources/locators/login-page.json
{
  "loginPage": {
    "inpUserID": "//input[@id='username']",
    "btnLogin": "//button[@type='submit']"
  }
}

// 3. Sử dụng trong test - healing tự động!
```

---

© 2025 Core Platform. All rights reserved.  
📧 Email hỗ trợ: ngovanduy1991@gmail.com - duynv3@techcombank.com.vn
