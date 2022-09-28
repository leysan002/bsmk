package ru.glt.imgBuilder;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static ru.glt.Constants.CATALOG_PATH;


public class ImgBuilder {
    private static final String FILE_TYPE = "png";

    private String sourceFilePath;

    public ImgBuilder() {
        sourceFilePath = CATALOG_PATH;
    }

    public InputStream getImage(String file, ImgBuilderMode mode) throws IOException {
        //create img with transparent bg
        BufferedImage result = new BufferedImage(mode.getWidth(), mode.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        graphics.setBackground(new Color( 230, 230, 230, 150));
        graphics.clearRect(0, 0, mode.getWidth(), mode.getHeight());
        graphics.dispose();

        BufferedImage ctlgImg =  ImageIO.read(new File(sourceFilePath, file + ".jpeg"));
        double zoom = Math.min(ctlgImg.getHeight() > mode.getHeight() ? (double)mode.getHeight() / (double)ctlgImg.getHeight(): 1.0, ctlgImg.getWidth() > mode.getWidth() ? (double)mode.getWidth() / (double)ctlgImg.getWidth() : 1.0);
        int imgWidth = (int)Math.round(ctlgImg.getWidth() * zoom);
        int imgHeight = (int)Math.round(ctlgImg.getHeight() * zoom);
        result.getGraphics().drawImage(ctlgImg, (mode.getWidth() - imgWidth) / 2, (mode.getHeight() - imgHeight) / 2, imgWidth, imgHeight, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(result, FILE_TYPE, outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public InputStream getFullImage(String file, int width, int height) throws IOException {
                BufferedImage ctlgImg =  ImageIO.read(new File(sourceFilePath, file + ".jpeg"));
        double zoom = Math.min(ctlgImg.getHeight() > height ? (double)height / (double)ctlgImg.getHeight(): 1.0, ctlgImg.getWidth() > width ? (double)width / (double)ctlgImg.getWidth() : 1.0);
        int imgWidth = (int)Math.round(ctlgImg.getWidth() * zoom);
        int imgHeight = (int)Math.round(ctlgImg.getHeight() * zoom);

        //create img with transparent bg
        BufferedImage result = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);

        result.getGraphics().drawImage(ctlgImg, 0, 0, imgWidth, imgHeight, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(result, FILE_TYPE, outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
