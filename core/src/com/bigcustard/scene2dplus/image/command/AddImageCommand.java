package com.bigcustard.scene2dplus.image.command;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.bigcustard.planet.code.InaccessibleUrlException;
import com.bigcustard.scene2dplus.XY;
import com.bigcustard.scene2dplus.command.AbstractCommand;
import com.bigcustard.scene2dplus.image.ImageAreaModel;
import com.bigcustard.scene2dplus.image.ImageModel;
import com.bigcustard.scene2dplus.image.ImageUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class AddImageCommand extends AbstractCommand {
    private ImageAreaModel model;
    private String url;
    private ImageModel image;

    public AddImageCommand(ImageAreaModel model, String url) {
        this.model = model;
        this.url = url;
    }

    @Override
    public void execute() {
        image = addImage(url);
    }

    public ImageModel addImage(String url) {
        InputStream imageStream = inputStream(url);
        try {
            FileHandle mainImageFile = generateImageFileHandle(url);
            mainImageFile.write(imageStream, false);
            imageStream.close();
            XY imageSize = imageSize(mainImageFile);
            image = new ImageModel(mainImageFile, imageSize.x, imageSize.y);
            return model.addImage(image);
        } catch (IOException e) {
            System.err.println("Error importing image: " + e);
            throw new InaccessibleUrlException(url, e);
        }
    }

    @Override
    public void undo() {
        model.removeImage(image);
    }

    private FileHandle generateImageFileHandle(String url) {
        String filename = url.substring(url.lastIndexOf("/") + 1);
        filename = filename.contains("?") ? filename.substring(0, filename.indexOf("?")) : filename;
        return findUniqueImageName(filename);
    }

    private FileHandle findUniqueImageName(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        String filenameExcludingExtension = filename.substring(0, dotIndex);
        String extension = filename.substring(dotIndex);

        FileHandle candidate = model.folder().child(filenameExcludingExtension + extension);
        int suffix = 2;
        while (candidate.exists()) {
            candidate = model.folder().child(filenameExcludingExtension + suffix++ + extension);
        }
        return candidate;
    }

    protected XY imageSize(FileHandle mainImageFile) {
        return ImageUtils.imageSize(mainImageFile);
    }

    protected InputStream inputStream(String url) {
        try {
            return new URL(url).openStream();
        } catch (IOException e) {
            throw new InaccessibleUrlException(url, e);
        }
    }
}
