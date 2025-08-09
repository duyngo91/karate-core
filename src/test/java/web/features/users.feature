Feature: Login to OrangeHRM

  @test
  Scenario: Successful Login
    * configure driver = webConfig
    * driver demoUrl
    * driver.enableNetworkEvents()
    * call read(webPages + 'LoginPage.feature@login') {username: 'Admin', password: 'admin123'}
    * delay(5000)
    * print getResponse()
    * print getResponseAPI("action-summary")
    * match driver.url == 'https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index'
