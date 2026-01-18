@healing
Feature: LoginPage

  Background:
      * def locators = read(webLocators + 'Login.json')

    @login
    Scenario: login
      * retry(10,1000).waitFor(locators.loginPage.username).input(__arg.username)
      * waitFor(locators.loginPage.password).input(__arg.password)
      * click(locators.loginPage.unknown_btn)
