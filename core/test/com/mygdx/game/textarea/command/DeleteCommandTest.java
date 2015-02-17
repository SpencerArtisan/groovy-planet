package com.mygdx.game.textarea.command;

import com.mygdx.game.XY;
import com.mygdx.game.textarea.TextAreaModel;
import com.mygdx.game.textarea.XYAssert;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteCommandTest {
    private TextAreaModel model;
    private DeleteCommand command;

    @Before
    public void before() {
        model = new TextAreaModel("text", null);
        model.caret().moveRight(4);
        command = new DeleteCommand(model);
    }

    @Test
    public void execute() {
        command.execute();
        assertThat(model.getText()).isEqualTo("tex");
    }

    @Test
    public void executeWhenAreaSelected() {
        model.setText("hello\nthere");
        model.caret().setSelection(new XY<Integer>(3, 0), new XY<Integer>(2, 1));
        command.execute();
        assertThat(model.getText()).isEqualTo("helere");
        assertThat(model.caret().isAreaSelected()).isFalse();
        XYAssert.assertThat(model.caret()).at(3, 0);
    }

    @Test
    public void undo() {
        command.execute();
        command.undo();
        assertThat(model.getText()).isEqualTo("text");
    }

    @Test
    public void undoWhenAreaSelected() {
        model.setText("hello\nthere");
        model.caret().setSelection(new XY<Integer>(3, 0), new XY<Integer>(2, 1));
        command.execute();
        command.undo();
        assertThat(model.getText()).isEqualTo("hello\nthere");
        assertThat(model.caret().isAreaSelected()).isTrue();
        XYAssert.assertThat(model.caret().selection().getLeft()).at(3, 0);
        XYAssert.assertThat(model.caret().selection().getRight()).at(2, 1);
    }
}
