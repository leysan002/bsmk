package ru.glt.ctlgFiles.builder;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.parser.ParseException;
import ru.glt.ctlgFiles.CtlgFileType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OptCtlgPdfFileBuilder extends CtlgFileBuilder {
    public OptCtlgPdfFileBuilder() {
        super();
    }

    public void write(OutputStream outputStream) throws IOException, ParseException, InvalidFormatException {
        ByteArrayOutputStream word = new ByteArrayOutputStream();
        try {
            CtlgFileBuilder builder = CtlgFileBuilder.getBuilder(CtlgFileType.OPT_WORD);
            builder.write(word);
            IConverter converter = LocalConverter.builder().build();
            converter.convert(new ByteArrayInputStream(word.toByteArray())).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
        } finally {
            word.close();
        }
    }

}
