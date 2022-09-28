package ru.glt.ctlgFiles;

import com.google.common.base.Charsets;
import org.apache.http.client.utils.DateUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import static ru.glt.ctlgFiles.CtlgFileConstants.*;

public class CtlgFileNames {
    private static String getFileExtension(CtlgFileType fileType) {
        switch (fileType) {
            case OPT_PDF:
            case PDF:
                return "pdf";
            default:
                return "docx";
        }
    }

    private static String getCacheShortName(CtlgFileType fileType) {
        switch (fileType) {
            case OPT_PDF:
            case OPT_WORD:
            case OPT_WORD2:
                return getOutputName(fileType);
            default:
                return CACHE_FILE_NAME;
        }
    }

    static String getCachePath(CtlgFileType fileType) {
        switch (fileType) {
            case OPT_PDF:
            case OPT_WORD:
                return OPT_FILE_PATH;
            case OPT_WORD2:
                return OPT_FILE_PATH2;
            default:
                return CACHE_FILE_PATH;
        }
    }

    static String getCacheName(CtlgFileType fileType) {
        return String.format(getCacheShortName(fileType), getFileExtension(fileType));
    }

    static String getOutputName(CtlgFileType fileType) {
        return String.format(OUTPUT_FILE_NAME, DateUtils.formatDate(new Date(), "dd-MM-yyyy"), getFileExtension(fileType));
    }

    public static String getEncodedOutputName(CtlgFileType fileType) throws UnsupportedEncodingException {
        return URLEncoder.encode(getOutputName(fileType), Charsets.UTF_8.toString()).replace("+", "%20");
    }
}
