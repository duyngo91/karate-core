Feature: Login to OrangeHRM

  @test
  Scenario: Successful Login
    * configure driver = driverConfig
    * driver demoUrl
    * call read(webPages + 'LoginPage.feature@login') {username: 'Admin', password: 'admin123'}
    * match driver.url == 'https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index'

  @test
  Scenario: Successful Login
    * configure driver = driverConfig
    * driver demoUrl
    * call read(webPages + 'LoginPage.feature@login') {username: 'Admin', password: 'admin123'}
    * match driver.url == 'https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index'
