function fn(arg) {
  let paths = {
    'dataPath' : 'file:src/test/java/resouces/data' + arg.env + '/',
    'apiFeatures' : 'classpath:api/features/',
    'webFeatures' : 'classpath:web/features/',
    'webLocators' : 'classpath:web/locators/',
    'webPages' : 'classpath:web/pages/',
    'mobileFeatures' : 'classpath:mobile/features/',
    'mobileLocators' : 'classpath:mobile/locators/',
    'mobilePages' : 'classpath:mobile/pages/',
    'dbFeatures' : 'classpath:db/features/',
  }
  return paths;
}