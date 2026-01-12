@abc
Feature: Self-Healing Strategy Verification

  Background:
    * configure driver = driverConfig
    # Lưu ý: Đây là file mẫu để verify từng Strategy. 
    # Bạn sẽ chạy test này trên một trang web thật (ví dụ: https://opensource-demo.orangehrmlive.com/) 
    # để giả lập việc Locator bị hỏng.

  @strategy-lexical
  Scenario: Verify Lexical Strategies (ExactAttribute, KeyBased, CrossAttribute)
    Given driver 'https://opensource-demo.orangehrmlive.com/web/index.php/auth/login'
    And delay(5000)
    
    # 1. GIẢ LẬP HỎNG NAME (ExactAttribute/KeyBased)
    * waitFor("//input[@name='usertest']")
    * input("//input[@name='usertest']", "Admin")
    
    # 2. GIẢ LẬP ĐỔI THUỘC TÍNH CHÉO (CrossAttribute)
    * waitFor("//input[@id='pass_old']")
    * input("//input[@id='pass_old']", "admin123")
    
    # Đăng xuất để scenario sau bắt đầu sạch
    * click("button[type='submit']")
    * delay(5000)
    * driver.quit()

  @strategy-semantic
  Scenario: Verify Semantic and AI Strategies (RAG, SemanticValue)
    Given driver 'https://opensource-demo.orangehrmlive.com/web/index.php/auth/login'
    And delay(5000)
    
    # 3. GIẢ LẬP ĐỔI TEXT ĐỒNG NGHĨA (SemanticValue/RAG)
    * waitFor("//button[text()='Sign']")
    * click("//button[text()='Sign']")

  @strategy-structural
  Scenario: Verify Structural and Neighbor Strategies
    Given driver 'https://opensource-demo.orangehrmlive.com/web/index.php/auth/login'
    And delay(5000)
    
    # 4. GIẢ LẬP MẤT SẠCH ID, DÙNG HÀNG XÓM (Neighbor)
    * waitFor("//label[text()='User']/following::input")
    * input("//label[text()='User']/following::input", "Admin")

  @strategy-visual
  Scenario: Verify Visual Healing (Visual Style Change)
    Given driver 'https://opensource-demo.orangehrmlive.com/web/index.php/auth/login'
    And delay(5000)
    
    # 5. GIẢ LẬP ĐỐI CSS/TAG (Visual Strategy)
    * waitFor("//div[@class='unknown']//button")
    * click("//div[@class='unknown']//button")
