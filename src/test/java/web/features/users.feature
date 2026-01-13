Feature: Login to OrangeHRM

  Background:
    * configure driver = driverConfig


  @test-1
  Scenario: Successful Login
    * driver demoUrl
    * call read(webPages + 'LoginPage.feature@login') {username: 'Admin', password: 'admin123'}
    * match driver.url == 'https://opensource-demo.orangehrmlive.com/web/index.php/dashboard/index'


  @test-2
  Scenario: Successful Login
    * driver "https://www.csm-testcenter.org/test?do=show&subdo=common&test=file_upload"
    * retry(30,1000).waitFor("//input[@name='file_upload']")
    * uploadFile("//input[@name='file_upload']", "file:src/test/resources/pdf-test.pdf")
    * delay(10000)