function fn() {
  let env = karate.env; // get system property 'karate.env'
  karate.configure('ssl', {trustAll: true});
  karate.configure('logPrettyResponse', true);
  karate.configure('logPrettyRequest', true);

  const stringUtils = Java.type('com.tcb.utils.StringUtils')

  let cf = {
    'long_time' : 360,
    'normal_time' : 120,
    'short_time' : 60,
    'quick_time' : 10,
    'stringUtils' : stringUtils

  }
  return cf;
}