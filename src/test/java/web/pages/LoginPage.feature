Feature: LoginPage

  Background:
      * def loginLts = read(webLocators + 'Login.json')

    @login
    Scenario: login
      * retry(10,3000).waitFor(loginLts.txtUserName).input(__arg.username)
      * retry(10).waitFor(loginLts.txtPassword).input(__arg.password)
      * click(loginLts.btnSubmit)
