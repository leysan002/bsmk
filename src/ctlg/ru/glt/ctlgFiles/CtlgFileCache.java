package ru.glt.ctlgFiles;

import java.io.*;

class CtlgFileCache {

    public CtlgFileCache() {
    }

    public boolean isFileExists(CtlgFileType fileType) {
        return initFile(fileType).exists();
    }

    private File initFile(CtlgFileType fileType) {
        return new File(CtlgFileNames.getCachePath(fileType), CtlgFileNames.getCacheName(fileType));
    }

    public FileOutputStream getFileOutputStream(CtlgFileType fileType) throws IOException {
        File ctlg = initFile(fileType);
        ctlg.getParentFile().mkdirs();
        ctlg.createNewFile();
        return new FileOutputStream(ctlg);
    }

    public FileInputStream getFileInputStream(CtlgFileType fileType) throws IOException {
        File ctlg = initFile(fileType);
        return new FileInputStream(ctlg);
    }

    public boolean reset(CtlgFileType fileType) {
        return initFile(fileType).delete();

    }
}
