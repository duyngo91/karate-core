Feature: Simple Visual Healing Test

  Background:
    * configure driver = { type: 'ChromeCustom', showDriverLog: true }
    * eval
    """
    var HealingConfig = Java.type('core.healing.HealingConfig');
    HealingConfig.getInstance().enabled = true;
    HealingConfig.getInstance().captureGoldenState = true;
    HealingConfig.getInstance().semanticEnabled = true;

    // Manually load locators
    var LocatorRepository = Java.type('core.healing.LocatorRepository');
    var LocatorMapper = Java.type('core.healing.LocatorMapper');
    var File = Java.type('java.io.File');
    var path = new File('src/test/java/web/locators/visual_test_locators.json').getAbsolutePath();
    karate.log('Loading locators from: ' + path);
    LocatorRepository.getInstance().loadFromFile(path);
    LocatorMapper.getInstance().buildIndex();
    """

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
