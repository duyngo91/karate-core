Feature: Simple Visual Healing Test

  Background:
    * configure driver = { type: 'ChromeCustom', showDriverLog: true }

  @test-visual-healing
  Scenario: Visual Healing - Simple Button ID Change
    # Step 1: Capture Golden State on V1
    #Given driver 'file:///' + karate.toAbsolutePath('src/test/resources/visual_test_v1.html')
    Given driver 'file:///E:/Project/auto/karate-fw/karate-core/src/test/resources/visual_test_v1.html'
    And delay(1000)
    * print '>>> STEP 1: Capturing Golden State'
    When click('#submit-btn')
    And delay(1000)
    
    # Step 2: Navigate to V2 (broken page) and test healing
    #Given driver 'file:///' + karate.toAbsolutePath('src/test/resources/visual_test_v2.html')
    Given driver 'file:///E:/Project/auto/karate-fw/karate-core/src/test/resources/visual_test_v2.html'
    And delay(1000)
    * print '>>> STEP 2: Testing Visual Healing (ID changed)'
    When click('#submit-btn')
    * print '>>> Healing should have found btn-submit-v2'
    And delay(1000)
    
    * print '>>> TEST COMPLETE - Check target/golden-screenshots/ for captured images'
