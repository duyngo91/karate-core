@healing
Feature: 50 Healing Test Cases

  Background:
    * configure driver = { type: 'ChromeCustom', showDriverLog: true }
    # Enable Healing
    * configure afterScenario = function(){ karate.call('classpath:helpers/after_scenario.feature') }
    * eval
    """
    // Force enable healing and golden state capture
    var HealingConfig = Java.type('core.healing.HealingConfig');
    HealingConfig.getInstance().enabled = true;
    HealingConfig.getInstance().captureGoldenState = true;
    HealingConfig.getInstance().semanticEnabled = true;

    // Manually load locators to ensure they are managed
    var LocatorRepository = Java.type('core.healing.LocatorRepository');
    var LocatorMapper = Java.type('core.healing.LocatorMapper');
    // Ensure absolute path or correct relative path
    var File = Java.type('java.io.File');
    var path = new File('src/test/java/web/locators/healing_locators.json').getAbsolutePath();
    karate.log('Loading locators from: ' + path);
    LocatorRepository.getInstance().loadFromFile(path);
    LocatorMapper.getInstance().buildIndex();
    """

  @test-50
  Scenario: Run 50 Healing Scenarios
    # 1. Open V1 and Capture Golden State
    * driver 'file:///e:/Project/auto/karate-fw/karate-core/src/test/resources/healing_v1.html'
    * maximize()
    * delay(1000)
    
    # Interact with all 50 elements to save them to Global State
    # We use explicit locators here that match V1
    
    # Group 1: ID Changes
    * click('#test_01')
    * click('#test_02')
    * click('#test_03')
    * click('#test_04')
    * click('#test_05')
    * click('#test_06')
    * click('#test_07')
    * click('#test_08')
    * click('#test_09')
    * click('#test_10')

    # Group 2: Name/Class Changes
    # Using single quotes for attributes to avoid JS escaping issues
    * value("input[name='test_name_11']", 'test')
    * value("input[name='test_name_12']", 'test')
    * click('#id_13')
    * click('#id_14')
    * value("input[name='test_name_15']", 'test')
    * click('#id_16')
    * value("input[name='test_name_17']", 'test')
    * click('#id_18')
    * value("input[name='test_name_19']", 'test')
    * click('#id_20')

    # Group 3: Text Changes
    * click('#id_21')
    * click('#id_22')
    * click('#id_23')
    * click('#id_24')
    * click('#id_25')
    * click('#id_26')
    * click('#id_27')
    * click('#id_28')
    * click('#id_29')
    * click('#id_30')

    # Group 4: Structural Changes
    * click('#id_31')
    * click('#id_32')
    * click('#id_33')
    * click('#id_34')
    * click('#id_35')
    * click('#id_36')
    * click('#id_37')
    * click('#id_38')
    * click('#id_39')
    * click('#id_40')

    # Group 5: Attribute Changes
    * click('#id_41')
    * value('#id_42', 'test')
    * click('#id_43')
    * click('#id_44')
    * click('#id_45')
    * click('#id_46')
    * click('#id_47')
    * click('#id_48')
    * click('#id_49')
    * click('#id_50')
    
    # Wait for capture to finish
    * delay(3000)

    # 2. Open V2 (Broken Page)
    * driver 'file:///e:/Project/auto/karate-fw/karate-core/src/test/resources/healing_v2.html'
    * delay(1000)
    
    # 3. Try to click elements using OLD locators
    # The healing engine should kick in and find them on the new page
    
    # Group 1: ID Changes
    * print '>>> HEALING TEST 01: Suffix Change'
    * click('#test_01')
    * print '>>> HEALING TEST 02: Separator Change'
    * click('#test_02')
    * print '>>> HEALING TEST 03: Word Swap'
    * click('#test_03')
    * print '>>> HEALING TEST 04: Underscore Removal'
    * click('#test_04')
    * print '>>> HEALING TEST 05: Case Change'
    * click('#test_05')
    * print '>>> HEALING TEST 06: Reference Change (btn)'
    * click('#test_06')
    * print '>>> HEALING TEST 07: Abbreviation'
    * click('#test_07')
    * print '>>> HEALING TEST 08: Extension'
    * click('#test_08')
    * print '>>> HEALING TEST 09: Typo'
    * click('#test_09')
    * print '>>> HEALING TEST 10: New ID'
    * click('#test_10')

    # Group 2: Name/Class Changes
    * print '>>> HEALING TEST 11: Name Change'
    * value("input[name='test_name_11']", 'healed')
    * print '>>> HEALING TEST 12: Name Abbrev'
    * value("input[name='test_name_12']", 'healed')
    * print '>>> HEALING TEST 13: Class Change'
    * click('#id_13')
    * print '>>> HEALING TEST 14: Class Semantic'
    * click('#id_14')
    * print '>>> HEALING TEST 15: Name Semantic'
    * value("input[name='test_name_15']", 'healed')
    * print '>>> HEALING TEST 16: Class Error'
    * click('#id_16')
    * print '>>> HEALING TEST 17: Name Typo'
    * value("input[name='test_name_17']", 'healed')
    * print '>>> HEALING TEST 18: Class Inversion'
    * click('#id_18')
    * print '>>> HEALING TEST 19: Name Synonym'
    * value("input[name='test_name_19']", 'healed')
    * print '>>> HEALING TEST 20: Class Icon'
    * click('#id_20')

    # Group 3: Text Changes
    * print '>>> HEALING TEST 21: Login -> Sign In'
    * click('#id_21')
    * print '>>> HEALING TEST 22: Submit -> Send'
    * click('#id_22')
    * print '>>> HEALING TEST 23: Contact -> Support'
    * click('#id_23')
    * print '>>> HEALING TEST 24: Add to Cart -> Buy Now'
    * click('#id_24')
    * print '>>> HEALING TEST 25: View Details -> More Info'
    * click('#id_25')
    * print '>>> HEALING TEST 26: Delete -> Remove'
    * click('#id_26')
    * print '>>> HEALING TEST 27: Edit -> Update'
    * click('#id_27')
    * print '>>> HEALING TEST 28: Next -> Continue'
    * click('#id_28')
    * print '>>> HEALING TEST 29: Previous -> Back'
    * click('#id_29')
    * print '>>> HEALING TEST 30: Forgot -> Reset'
    * click('#id_30')

    # Group 4: Structural Changes
    * print '>>> HEALING TEST 31: Depth Span'
    * click('#id_31')
    * print '>>> HEALING TEST 32: Tag Change'
    * click('#id_32')
    * print '>>> HEALING TEST 33: Parent P to Div'
    * click('#id_33')
    * print '>>> HEALING TEST 34: Moved Next'
    * click('#id_34')
    * print '>>> HEALING TEST 35: Moved Prev'
    * click('#id_35')
    * print '>>> HEALING TEST 36: Parent ID Change'
    * click('#id_36')
    * print '>>> HEALING TEST 37: Parent Class Change'
    * click('#id_37')
    * print '>>> HEALING TEST 38: Sibling Removed'
    * click('#id_38')
    * print '>>> HEALING TEST 39: Sibling Added'
    * click('#id_39')
    * print '>>> HEALING TEST 40: Deep Nesting'
    * click('#id_40')

    # Group 5: Attribute Changes
    * print '>>> HEALING TEST 41: testid change'
    * click('#id_41')
    * print '>>> HEALING TEST 42: placeholder change'
    * value('#id_42', 'healed')
    * print '>>> HEALING TEST 43: type change'
    * click('#id_43')
    * print '>>> HEALING TEST 44: role change'
    * click('#id_44')
    * print '>>> HEALING TEST 45: aria change'
    * click('#id_45')
    * print '>>> HEALING TEST 46: href change'
    * click('#id_46')
    * print '>>> HEALING TEST 47: value change'
    * click('#id_47')
    * print '>>> HEALING TEST 48: title change'
    * click('#id_48')
    * print '>>> HEALING TEST 49: src change'
    * click('#id_49')
    * print '>>> HEALING TEST 50: alt change'
    * click('#id_50')
    
    * print '>>> ALL 50 HEALING TESTS COMPLETED SUCCESSFULLY'
