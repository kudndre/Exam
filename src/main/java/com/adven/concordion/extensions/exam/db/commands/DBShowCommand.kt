package com.adven.concordion.extensions.exam.db.commands

import com.adven.concordion.extensions.exam.db.DbTester
import com.adven.concordion.extensions.exam.core.html.html
import com.adven.concordion.extensions.exam.core.html.table
import org.concordion.api.CommandCall
import org.concordion.api.Evaluator
import org.concordion.api.ResultRecorder
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.ITable
import org.dbunit.dataset.filter.DefaultColumnFilter.includedColumnsTable

class DBShowCommand(name: String, tag: String, dbTester: DbTester) : DBCommand(name, tag, dbTester) {

    override fun setUp(cmd: CommandCall?, eval: Evaluator?, resultRecorder: ResultRecorder?) {
        val el = table(cmd.html())
        val tableName = el.takeAwayAttr("table", eval)!!
        val where = el.takeAwayAttr("where", eval)
        val ds = el.takeAwayAttr("ds", DbTester.DEFAULT_DATASOURCE)
        val conn = dbTester.executors[ds]!!.connection

        renderTable(
            el,
            includedColumnsTable(
                if (where == null || where.isEmpty())
                    conn.createTable(tableName)
                else
                    getFilteredTable(conn, tableName, where),
                parseCols(el, eval!!).keys.toTypedArray()
            ))
    }

    private fun getFilteredTable(connection: IDatabaseConnection, tableName: String, rowFilter: String): ITable {
        return connection.createQueryTable(tableName, "SELECT * FROM $tableName WHERE $rowFilter")
    }
}