package com.adven.concordion.extensions.exam.mq

import com.adven.concordion.extensions.exam.commands.ExamVerifyCommand
import com.adven.concordion.extensions.exam.html.html
import com.adven.concordion.extensions.exam.rest.RestResultRenderer
import org.concordion.api.CommandCall
import org.concordion.api.Evaluator
import org.concordion.api.ResultRecorder

interface MqTester {
    fun start()
    fun stop()
    fun send(message: String)
    fun recieve(): String
    fun purge()
}

class MqCheckCommand(name: String, tag: String, private val mqTesters: Map<String, MqTester>) : ExamVerifyCommand(name, tag, RestResultRenderer()) {
    override fun verify(cmd: CommandCall, evaluator: Evaluator, resultRecorder: ResultRecorder) {
        val root = cmd.html()
        val mqName = root.takeAwayAttr("name")
        val mqTester = mqTesters[mqName]
                ?: throw IllegalArgumentException("MQ with name $mqName not registered in Exam")
        val actual = mqTester.recieve()
    }
}