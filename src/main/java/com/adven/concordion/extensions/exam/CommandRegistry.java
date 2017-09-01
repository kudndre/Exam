package com.adven.concordion.extensions.exam;

import com.adven.concordion.extensions.exam.commands.*;
import com.adven.concordion.extensions.exam.db.commands.DBCheckCommand;
import com.adven.concordion.extensions.exam.db.commands.DBSetCommand;
import com.adven.concordion.extensions.exam.db.commands.DBShowCommand;
import com.adven.concordion.extensions.exam.files.commands.FilesCheckCommand;
import com.adven.concordion.extensions.exam.files.commands.FilesSetCommand;
import com.adven.concordion.extensions.exam.files.commands.FilesShowCommand;
import com.adven.concordion.extensions.exam.html.Html;
import com.adven.concordion.extensions.exam.rest.commands.*;
import com.adven.concordion.extensions.exam.ui.BrowserCommand;
import net.javacrumbs.jsonunit.core.Configuration;
import nu.xom.Attribute;
import nu.xom.Element;
import org.concordion.internal.command.ExampleCommand;
import org.dbunit.JdbcDatabaseTester;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Arrays.asList;

public class CommandRegistry {
    private final List<ExamCommand> commands;

    public CommandRegistry(JdbcDatabaseTester dbTester, Configuration jsonUnitCfg) {
        commands = asList(
                new GivenCommand("div"),
                new WhenCommand("div"),
                new ThenCommand("div"),

                new ExamExampleCommand("div"),
                new ExamplesSummaryCommand("summary", "div"),

                new CaseCheckCommand("check", "div"),

                new DBShowCommand("db-show", "table", dbTester),
                new DBCheckCommand("db-check", "table", dbTester),
                new DBSetCommand("db-set", "table", dbTester),

                new FilesShowCommand("fl-show", "table"),
                new FilesSetCommand("fl-set", "table"),
                new FilesCheckCommand("fl-check", "table"),

                new PostCommand("post", "div"),
                new GetCommand("get", "div"),
                new CaseCommand("tr", jsonUnitCfg),
                new ExpectedStatusCommand("rs-status", "code"),

                new BrowserCommand("div")
        );
    }

    public ExamCommand getBy(String name) {
        for (ExamCommand cmd : commands) {
            if (cmd.name().equals(name)) {
                return cmd;
            }
        }
        return null;
    }

    public List<ExamCommand> commands() {
        return new ArrayList<>(commands);
    }
}