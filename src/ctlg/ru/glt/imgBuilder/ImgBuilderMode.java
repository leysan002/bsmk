package ru.glt.imgBuilder;

public enum ImgBuilderMode {
    CTLG(300, 400),
    BIG_LINK(350, 150);

    private final int width;
    private final int height;

    ImgBuilderMode(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}