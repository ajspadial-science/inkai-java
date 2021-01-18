package com.spadial.inkai;

import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;

import java.util.ArrayList;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;

public class Crosswalk {
    ArrayList<Field> fields;

    public static final int MAX_ROWS = 2000;

    Crosswalk() {
        fields = new ArrayList<>();

        fields.add(new Field("icts"));
        fields.add(new Field("acronimo"));
        fields.add(new Field("descripcion"));
        fields.add(new Field("ambito"));
    }

    File exportTemplate() throws IOException {
        File output = new File(Files.createTempFile("template", ".ods").toString());

        int columns = fields.size();
        int rows = MAX_ROWS;

        Sheet sheet = new Sheet("Datos", rows, columns);
        Sheet auxSheet = new Sheet("Auxiliar", 1, 1);

        int c = 0;
        for(Field f: fields) {
            sheet.getRange(0, c).setValue(f.name);
            c++;
        }

        SpreadSheet calc = new SpreadSheet();
        calc.appendSheet(sheet);
        calc.appendSheet(auxSheet);

        calc.save(output);
        
        return output;
    }
}
