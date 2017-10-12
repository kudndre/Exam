package com.adven.concordion.extensions.exam.db.commands;

import com.adven.concordion.extensions.exam.html.Html;
import org.concordion.api.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;

import static org.dbunit.operation.DatabaseOperation.CLEAN_INSERT;
import static org.dbunit.operation.DatabaseOperation.INSERT;

public class DBSetCommand extends DBCommand {
    public DBSetCommand(String name, String tag, IDatabaseTester dbTester) {
        super(name, tag, dbTester);
    }

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        super.setUp(commandCall, evaluator, resultRecorder);
        try {
            Html el = new Html(commandCall.getElement());
            renderTable(el, expectedTable);
            setUpDB(expectedTable, parseInsertMode(el));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpDB(ITable iTable, DatabaseOperation insertMode) throws Exception {
        dbTester.setSetUpOperation(insertMode);
        dbTester.setDataSet(new DefaultDataSet(iTable));
        dbTester.onSetup();
    }

    private DatabaseOperation parseInsertMode(Html el) {
        return el.attr("mode") != null && el.attr("mode").equals("add") ?
                INSERT : CLEAN_INSERT;
    }
}