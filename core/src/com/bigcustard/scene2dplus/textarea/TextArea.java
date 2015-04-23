package com.bigcustard.scene2dplus.textarea;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bigcustard.scene2dplus.XY;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.Objects;

public class TextArea extends Actor {
    private static final int TOP_MARGIN = 29;
    private static final int LEFT_MARGIN = 8;
    private static final int COLUMN_WIDTH = 14;
    private final TextureRegionDrawable white;
    private TextAreaModel model;
    private TextField.TextFieldStyle style;

    public TextArea(TextAreaModel model, Skin skin) {
        this.model = model;
        style = skin.get("code", TextField.TextFieldStyle.class);
        white = (TextureRegionDrawable) skin.getDrawable("white");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        drawBackground(batch);
        drawCurrentLineBackground(batch);
        drawOtherLineBackgrounds(batch);
        drawSelectionBackground(batch);
        drawCaret(batch);
        drawText(batch);
    }

    public XY caretLocationToPosition(XY caret) {
        float x = LEFT_MARGIN + getX() + caret.x * getColumnWidth();
        float y = -TOP_MARGIN + getHeight() + getY() - caret.y * getRowHeight();
        return new XY((int) x, (int) y);
    }

    public XY worldPositionToCaretLocation(XY worldXY) {
        float caretX = (worldXY.x  - LEFT_MARGIN) / getColumnWidth();
        float caretY = (this.getHeight()  - TOP_MARGIN + 16 - worldXY.y) / getRowHeight();
        return new XY((int) caretX, (int) caretY);
    }

    float getRowHeight() {
        return style.font.getLineHeight();
    }

    private float getColumnWidth() {
        return COLUMN_WIDTH;
    }

    private void drawBackground(Batch batch) {
        style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
    }

    private void drawCurrentLineBackground(Batch batch) {
        if (model.caret().selection() == null) {
            XY topLeftCurrent = caretLocationToPosition(new XY(0, model.caret().location().y));
            style.focusedBackground.draw(batch, 0, topLeftCurrent.y, getWidth(), getRowHeight());
        }
    }

    private void drawOtherLineBackgrounds(Batch batch) {
        Map<Integer, Color> coloredLines = model.getColoredLines();
        for (Map.Entry<Integer, Color> colorLine : coloredLines.entrySet()) {
            SpriteDrawable background = white.tint(colorLine.getValue());
            XY topLeftCurrent = caretLocationToPosition(new XY(0, colorLine.getKey()));
            background.draw(batch, 0, topLeftCurrent.y, getWidth(), getRowHeight());
        }
    }

    private void drawSelectionBackground(Batch batch) {
        Pair<XY, XY> selection = model.caret().selection();
        if (selection != null) {
            XY topLeftSelection = caretLocationToPosition(selection.getLeft());
            XY bottomRightSelection = caretLocationToPosition(selection.getRight());
            if (!Objects.equals(selection.getLeft().y, selection.getRight().y)) {
                style.selection.draw(batch, topLeftSelection.x, topLeftSelection.y, getWidth() - topLeftSelection.x, getRowHeight());
                style.selection.draw(batch, 0, bottomRightSelection.y + getRowHeight(), getWidth(), topLeftSelection.y - bottomRightSelection.y - getRowHeight());
                style.selection.draw(batch, 0, bottomRightSelection.y, bottomRightSelection.x, getRowHeight());
            } else {
                style.selection.draw(batch, topLeftSelection.x, topLeftSelection.y, bottomRightSelection.x - topLeftSelection.x, getRowHeight());
            }
        }
    }

    private void drawText(Batch batch) {
        try {
            style.font.setMarkupEnabled(true);
            XY textStart = caretLocationToPosition(new XY(0, 0));
            BitmapFont.TextBounds textBounds = style.font.drawMultiLine(batch, model.coloredText(), textStart.x, textStart.y + TOP_MARGIN - 8);
            setHeight(Math.max(TOP_MARGIN + textBounds.height, getParent().getHeight()));
            ((Layout) getParent()).invalidate();
        } catch (Exception e) {
            System.out.println("Failed to draw text: " + model.coloredText());
            throw e;
        }
    }

    private void drawCaret(Batch batch) {
        Drawable caretImage = style.cursor;
        XY caretPosition = caretLocationToPosition(model.caret().location());
        caretImage.draw(batch, caretPosition.x, caretPosition.y, caretImage.getMinWidth(), getRowHeight());
    }
}