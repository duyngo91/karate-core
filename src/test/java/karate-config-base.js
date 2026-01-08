function fn(arg) {

    karate.configure('ssl', { trustAll: true });
    karate.configure('logPrettyResponse', true);
    karate.configure('logPrettyRequest', true);
    karate.configure('connectTimeout', 180000);
    karate.configure('readTimeout', 180000);

    let System = Java.type('java.lang.System');
    let userDir = System.getProperty('user.dir');
    let downloadPath = userDir + '/target/download';


    let cf = {
        'otpDebug': 123456,
        'long_time' : 360,
        'short_time' : 60,
        'normal_time' : 120,
        'stableTime' : 5000,
        'downloadPath' : downloadPath
    };
    return cf;
}