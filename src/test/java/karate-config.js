function fn() {
    let env = karate.env; // get system property 'karate.env'

    if (!env) {
        env = 'sit';
    }
    let config = {
        env: env,
        headless: karate.properties['karate.headless'] || 'false'
    }

    // Set global headless mode
    karate.log('karate.env system property was:', env);
    const system = Java.type('java.lang.System');
    system.setProperty("karate.env", env);
    system.setProperty("database_Env", "src/test/resources/database.xml");

    let envConfig = karate.call('classpath:karate-config-env-' + env + '.js', { config: config });
    config = karate.merge(config, envConfig);

    let pathConfig = karate.call('classpath:karate-config-path.js', { config: config });
    config = karate.merge(config, pathConfig);

    let driverConfig = karate.call('classpath:karate-config-driver.js', { config: config });
    config = karate.merge(config, driverConfig);

    let basicConfig = karate.call('classpath:karate-config-base.js', { config: config });
    config = karate.merge(config, basicConfig);


    karate.configure('driver', driverConfig.driverConfig);

    return config;
}
