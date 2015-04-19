package com.bigcustard.planet.code.command;

import com.badlogic.gdx.files.FileHandle;
import com.bigcustard.planet.code.Game;
import com.bigcustard.planet.plugin.Plugin;
import com.bigcustard.scene2dplus.command.AbstractCommand;
import com.bigcustard.scene2dplus.image.ImageModel;
import com.bigcustard.scene2dplus.image.ImageUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RunCommand extends AbstractCommand {
    private final FileHandle buildFolder;
    private final Game game;
    private final BiConsumer<Game, String> runGame;
    private final Plugin languagePlugin;

    public RunCommand(Game game, BiConsumer<Game, String> runGame, Plugin languagePlugin) {
        this.game = game;
        this.runGame = runGame;
        this.languagePlugin = languagePlugin;
        buildFolder = game.folder().child("build");
    }

    @Override
    public void execute() {
        buildFolder.mkdirs();
        game.imageModel().images().forEach(this::resize);
        game.setRuntimeError(null);
        runGame.accept(game, languagePlugin.language());
    }

    @Override
    public boolean canExecute() {
        return game.isValid(languagePlugin.syntax());
    }

    protected void resize(ImageModel imageModel) {
        FileHandle target = buildFolder.child(imageModel.name());
        ImageUtils.resize(imageModel.file(), target, imageModel.width(), imageModel.height());
    }
}
