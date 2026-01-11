@healing-stress-test
Feature: Stress Testing All Healing Strategies in Local Environment

  Background:
    * configure driver = { type: 'ChromeCustom', showDriverLog: true }
    # Load locators
    * def locators = read(webLocators + 'LocalPage.json')
    * def localPage = locators.localPage
    # Construct absolute path to local HTML
    * def projectDir = karate.properties['user.dir']
    * def testFile = 'file:///' + projectDir.replace('\\', '/') + '/src/test/java/web/local_test/index.html'
    * driver testFile
    * delay(1000)

  @strategy-exact
  Scenario Outline: Verify ExactAttributeStrategy - <caseId>
    * click(localPage.<caseId>)
    Examples:
      | caseId |
      | exact1 |
      | exact2 |
      | exact3 |
      | exact4 |
      | exact5 |
      | exact6 |
      | exact7 |
      | exact8 |
      | exact9 |
      | exact10|

  @strategy-keybased
  Scenario Outline: Verify KeyBasedStrategy - <caseId>
    * input(localPage.<caseId>, "Test Data")
    Examples:
      | caseId |
      | kb1    |
      | kb2    |
      | kb3    |
      | kb4    |
      | kb5    |
      | kb6    |
      | kb7    |
      | kb8    |
      | kb9    |
      | kb10   |

  @strategy-cross
  Scenario Outline: Verify CrossAttributeStrategy - <caseId>
    * waitFor(localPage.<caseId>)
    Examples:
      | caseId |
      | cross1 |
      | cross2 |
      | cross3 |
      | cross4 |
      | cross5 |
      | cross6 |
      | cross7 |
      | cross8 |
      | cross9 |
      | cross10|

  @strategy-neighbor
  Scenario Outline: Verify NeighborStrategy - <caseId>
    * waitFor(localPage.<caseId>)
    * scroll(localPage.<caseId>)
    Examples:
      | caseId |
      | nb1    |
      | nb2    |
      | nb3    |
      | nb4    |
      | nb5    |
      | nb6    |
      | nb7    |
      | nb8    |
      | nb9    |
      | nb10   |

  @strategy-semantic
  Scenario Outline: Verify SemanticValueStrategy - <caseId>
    * waitFor(localPage.<caseId>)
    * click(localPage.<caseId>)
    Examples:
      | caseId |
      | sem1   |
      | sem2   |
      | sem3   |
      | sem4   |
      | sem5   |
      | sem6   |
      | sem7   |
      | sem8   |
      | sem9   |
      | sem10  |

  @strategy-structural
  Scenario Outline: Verify StructuralStrategy - <caseId>
    * waitFor(localPage.<caseId>)
    * highlight(localPage.<caseId>)
    Examples:
      | caseId |
      | struct1|
      | struct2|
      | struct3|
      | struct4|
      | struct5|
      | struct6|
      | struct7|
      | struct8|
      | struct9|
      | struct10|

  @strategy-text
  Scenario Outline: Verify TextBasedStrategy - <caseId>
    * waitFor(localPage.<caseId>)
    * def textValue = text(localPage.<caseId>)
    * print 'Healed Text:', textValue
    Examples:
      | caseId |
      | text1  |
      | text2  |
      | text3  |
      | text4  |
      | text5  |
      | text6  |
      | text7  |
      | text8  |
      | text9  |
      | text10 |
