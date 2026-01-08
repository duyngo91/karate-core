function fn(arg) {
    let cf = {
        'driverConfig' : {
            type : 'ChromeCustom', class: 'com.com.platform.web.ChromeCustom',
            headless: arg.config.headless === 'true',
            addOptions:
            [
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--disable-infobars",
            "--disable-notifications",
            "--disable-default-apps",
            "--disable-extensions",
            "--disable-plugins",
            "--disable-popup-blocking",
            "--disable-translate",
            "--disable-background-timer-throttling",
            "--disable-renderer-backgrounding",
            "--disable-backgrounding-occluded-windows",
            "--disable-permissions-api",
            "--disable-background-networking",
            "--deny-permission-prompts",
            "--disable-features=VizDisplayCompositor,TranslateUI",
            "--disable-ipc-flooding-protection",
            "--disable-hang-monitor",
            "--disable-prompt-on-repost",
            "--disable-sync",
            "--metrics-recording-only",
            "--no-first-run",
            "--safebrowsing-disable-auto-update",
            "--ignore-certificate-errors",
            "--ignore-ssl-errors",
            "--ignore-certificate-errors-spki-list",
            "--allow-insecure-localhost",
            "--disable-web-security",
            "--disable-save-password-bubble",
            "--force-device-scale-factor=1",
            "--high-dpi-support=1"
            ]
        }
      };

    if (arg.config.headless === 'true') { cf.driverConfig.addOptions.push("--headless=new") }

    return cf;
}