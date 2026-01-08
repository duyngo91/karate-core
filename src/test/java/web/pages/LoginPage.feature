Feature: LoginPage

  Background:
      * def locators = read(webLocators + 'Login.json')

    @login
    Scenario: login
      * retry(10,1000).waitFor(locators.loginPage.txtUserName).input(__arg.username)
      * waitFor(locators.loginPage.txtPassword).input(__arg.password)
      * click(locators.loginPage.btnSubmit)
