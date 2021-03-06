package com.adven.concordion.extensions.exam.ui

import com.adven.concordion.extensions.exam.core.ExamPlugin
import com.adven.concordion.extensions.exam.core.commands.ExamCommand
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.WebDriverRunner
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.github.bonigarcia.wdm.DriverManagerType
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.chrome.ChromeOptions.CAPABILITY
import org.openqa.selenium.remote.DesiredCapabilities
import java.util.Collections.singletonMap

@SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
open class UiPlugin @JvmOverloads constructor(
    var timeout: Long = Configuration.timeout,
    browser: String = WebDriverRunner.CHROME,
    version: String? = null,
    baseUrl: String = Configuration.baseUrl,
    headless: Boolean = true,
    private val screenshotsOnSuccess: Boolean = true,
    screenshotsOnFail: Boolean = true,
    private var capabilities: DesiredCapabilities? = null
) : ExamPlugin {

    companion object {
        private var webDriverInited: Boolean = false
    }

    init {
        if (!webDriverInited) {
            Configuration.screenshots = screenshotsOnFail
            Configuration.timeout = timeout
            Configuration.baseUrl = baseUrl
            Configuration.browser = browser
            when (browser) {
                WebDriverRunner.FIREFOX ->
                    WebDriverManager.getInstance(DriverManagerType.FIREFOX).version(version).setup()
                WebDriverRunner.INTERNET_EXPLORER ->
                    WebDriverManager.getInstance(DriverManagerType.IEXPLORER).version(version).setup()
                else -> {
                    WebDriverManager.getInstance(DriverManagerType.CHROME).version(version).setup()
                    if (headless) {
                        setHeadlessChromeOptions()
                    }
                }
            }
            webDriverInited = true
        }
    }

    private fun setHeadlessChromeOptions() {
        val opt = ChromeOptions()
        opt.addArguments(
            "no-sandbox", "headless", "disable-gpu", "disable-extensions", "window-size=1366,768"
        )
        capabilities = DesiredCapabilities(singletonMap<String, ChromeOptions>(CAPABILITY, opt))
    }

    override fun commands(): List<ExamCommand> = listOf(
        BrowserCommand("div", screenshotsOnSuccess, capabilities)
    )
}