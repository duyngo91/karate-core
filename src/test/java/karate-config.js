function fn() {
  let env = karate.env; // get system property 'karate.env'
  karate.log('karate.env system property was:', env);
  if (!env) {
    env = 'dev';
  }
  var config = {
    env: env,
    headless: karate.properties['karate.headless'] || 'false'
  }

  let envConfig = karate.call('classpath:karate-config-env-' + config.env + '.js');
  config = karate.merge(config, envConfig);

  let pathConfig = karate.call('classpath:karate-config-path.js', {config: config});
  config = karate.merge(config, pathConfig);

  let driverConfig = karate.call('classpath:karate-config-driver.js', {config: config});
  config = karate.merge(config, driverConfig);

  let baseConfig = karate.call('classpath:karate-config-base.js');
  config = karate.merge(config, baseConfig);
  return config;
}