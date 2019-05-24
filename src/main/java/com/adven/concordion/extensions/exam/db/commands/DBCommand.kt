package com.adven.concordion.extensions.exam.db.commands

import com.adven.concordion.extensions.exam.commands.ExamCommand
import com.adven.concordion.extensions.exam.db.TableData
import com.adven.concordion.extensions.exam.html.*
import com.adven.concordion.extensions.exam.resolveToObj
import com.github.database.rider.core.api.dataset.DataSetProvider
import com.github.database.rider.core.dataset.DataSetExecutorImpl
import org.concordion.api.CommandCall
import org.concordion.api.Evaluator
import org.concordion.api.ResultRecorder
import org.dbunit.dataset.Column
import org.dbunit.dataset.DefaultDataSet
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.ITable

open class DBCommand(name: String, tag: String, protected val dbTester: DataSetExecutorImpl) : ExamCommand(name, tag) {
    private val remarks = HashMap<String, Int>()
    private val colParser = ColParser()
    protected lateinit var expectedTable: ITable

    protected var where: String? = null
    protected var ds: String? = null

    override fun setUp(cmd: CommandCall?, eval: Evaluator?, resultRecorder: ResultRecorder?) {
        val root = tableSlim(cmd.html())(
            div(""),
            span("")
        )
        remarks.clear()
        where = root.takeAwayAttr("where", eval)
        ds = root.takeAwayAttr("ds", DataSetExecutorImpl.DEFAULT_EXECUTOR_ID)
        val rows = RowParser(root, "row", eval!!).parse()
        val cols = parseCols(root, eval)
        expectedTable = TableData.filled(
            root.takeAwayAttr("table", eval)!!,
            rows,
            cols
        )
    }

    protected fun parseCols(el: Html, eval: Evaluator): TableData.Cols {
        val attr = el.takeAwayAttr("cols")
        return if (attr == null)
            TableData.Cols()
        else {
            val remarkAndVal = colParser.parse(attr)
            remarks += remarkAndVal.map { it.key to it.value.first }.filter { it.second > 0 }
            TableData.Cols(
                remarkAndVal
                    .filterValues { !it.second.isBlank() }
                    .mapValues { resolveToObj(it.value.second, eval) },
                remarkAndVal.keys.toList()
            )
        }
    }

    protected fun renderTable(root: Html, t: ITable) {
        val cols = t.columnsSortedBy(remarks)
        val rows = t.mapRows { row ->
            cols.map { col -> t[row, col] }
        }
        val classFor = { c: Column -> if (remarks.containsKey(c.columnName)) "table-info" else "" }

        root(
            tableCaption(root.takeAwayAttr("caption"), t.tableName()),
            thead()(
                tr()(
                    cols.map {
                        th(it.columnName, CLASS to classFor(it))
                    })
            ),
            tbody()(
                rows.map {
                    tr()(
                        it.withIndex().map { (i, text) ->
                            td(text, CLASS to classFor(cols[i]))
                        })
                })
        )
    }

    protected fun tableCaption(title: String?, def: String?): Html {
        return caption(if (title != null && !title.isBlank()) title else def)(
            italic("", CLASS to "fa fa-database fa-pull-left fa-border")
        )
    }
}

class ColParser {
    fun parse(attr: String): Map<String, Pair<Int, String>> {
        return attr.split(",")
            .map {
                val (r, n, v) = ("""(\**)([^=]+)=?(.*)""".toRegex()).matchEntire(it.trim())!!.destructured
                mapOf(n to (r.length to v))
            }
            .reduce { acc, next -> acc + next }
    }
}

fun ITable.tableName(): String = this.tableMetaData.tableName

fun ITable.columnsSortedBy(remarks: Map<String, Int>): List<Column> = tableMetaData.columns.copyOf().sortedWith(
    Comparator { o1, o2 ->
        -compareValues(remarks[o1.columnName], remarks[o2.columnName])
    })

operator fun ITable.get(row: Int, col: Column): String = this.getValue(row, col.columnName)?.toString() ?: "(null)"

fun <R> ITable.mapRows(transform: (Int) -> R): List<R> = (0 until this.rowCount).map(transform)

class ExamDataSetProvider : DataSetProvider {
    companion object {
        lateinit var table: ITable
    }

    override fun provide(): IDataSet {
        return DefaultDataSet(table)
    }
}