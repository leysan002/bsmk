package ru.glt.ctlgFiles;

import ru.glt.ctlgFiles.builder.CtlgFileBuilder;

import java.io.*;

public class CtlgFileController {
    private final CtlgFileCache cache = new CtlgFileCache();


    public InputStream getFile(CtlgFileType fileType) throws Exception {
        return getFile(fileType, false);
    }

    public InputStream getFile(CtlgFileType fileType, boolean forceRefresh) throws Exception {
        if (cache.isFileExists(fileType) && forceRefresh) {
            cache.reset(fileType);
        }

        if (!cache.isFileExists(fileType)) {
            FileOutputStream outputStream = cache.getFileOutputStream(fileType);
            try {
                CtlgFileBuilder fileBuilder = CtlgFileBuilder.getBuilder(fileType);
                fileBuilder.write(outputStream);
            } finally {
                outputStream.flush();
                outputStream.close();
            }
        }
        return cache.getFileInputStream(fileType);
    }

}
