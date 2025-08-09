function fn(arg) {


  let cf = {
    'webConfig' : {type : 'CustomChrome', class: 'com.tcb.platform.web.CustomChrome'},
    headless: arg.config.headless === 'true',
    addOptions:
    [
        "--ignore-certificate-errors",
        "--ignore-certificate-errors-spki-list",
        "--ignore-ssl-errors",
        "--no-sandbox"
    ]
  }
  return cf;
}