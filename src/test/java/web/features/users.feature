Feature: Login to OrangeHRM

  Background:
    * configure driver = driverConfig
    * driver demoUrl

  @test-1
  Scenario: Successful Login
    * call read(webPages + 'LoginPage.feature@login') {username: 'Admin', password: 'admin123'}
    * match driver.url == 'https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index'

