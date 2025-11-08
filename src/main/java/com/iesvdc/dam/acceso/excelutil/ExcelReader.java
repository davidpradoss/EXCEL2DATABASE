package com.iesvdc.dam.acceso.excelutil;

import java.sql.Statement;
import java.io.FileInputStream;
import java.sql.Connection;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.iesvdc.dam.acceso.modelo.FieldType;
import com.iesvdc.dam.acceso.conexion.Conexion;
import com.iesvdc.dam.acceso.modelo.FieldModel;
import com.iesvdc.dam.acceso.modelo.TableModel;
import com.iesvdc.dam.acceso.modelo.WorkbookModel;

public class ExcelReader {

    private Workbook wb;
    private WorkbookModel wbm;
    private Connection conexion;

    private final double EPSILON = 1e-10;

    public ExcelReader() {
    }

    // Devuelve el tipo de dato de la celda como FieldType
    public FieldType getTipoDato(Cell cell) {
        if (cell == null) return FieldType.UNKNOWN;

        switch (cell.getCellType()) {
            case STRING:  return FieldType.VARCHAR;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) return FieldType.DATE;
                double valor = cell.getNumericCellValue();
                return (Math.abs(valor - Math.floor(valor)) < EPSILON) ? FieldType.INTEGER : FieldType.FLOAT;
            case BOOLEAN: return FieldType.BOOLEAN;
            default: return FieldType.UNKNOWN;
        }
    }

    // Devuelve el tipo SQL completo como String (incluye longitud de VARCHAR)
    private String sqlTypeFor(FieldType type) {
        switch (type) {
            case VARCHAR: return "VARCHAR(120)";
            case INTEGER: return "INT";
            case FLOAT:   return "FLOAT";
            case BOOLEAN: return "BOOLEAN";
            case DATE:    return "DATE";
            default:      return "VARCHAR(120)";
        }
    }

    public void loadWorkbook(String filename) {
        try (FileInputStream fis = new FileInputStream(filename)) {
            wb = new XSSFWorkbook(fis);
            wbm = new WorkbookModel();
            int nHojas = wb.getNumberOfSheets();

            for (int i = 0; i < nHojas; i++) {
                Sheet hojaActual = wb.getSheetAt(i);
                TableModel tabla = new TableModel(hojaActual.getSheetName());

                Row primeraFila = hojaActual.getRow(0);
                Row segundaFila = hojaActual.getRow(1);

                int nCols = primeraFila.getLastCellNum();

                for (int j = 0; j < nCols; j++) {
                    FieldModel campo = new FieldModel(
                        primeraFila.getCell(j).getStringCellValue(),
                        getTipoDato(segundaFila.getCell(j))
                    );
                    tabla.addField(campo);
                }

                wbm.addTable(tabla);
            }

        } catch (Exception e) {
            System.out.println("Imposible cargar el archivo Excel: " + e.getLocalizedMessage());
        }
    }

    public String generateDDl() {
        StringBuilder sqlSB = new StringBuilder();

        for (TableModel tableModel : wbm.getTables()) {
            sqlSB.append("CREATE TABLE ").append(tableModel.getName()).append("(\n");

            int nCampos = tableModel.getFields().size();
            for (FieldModel fieldModel : tableModel.getFields()) {
                nCampos--;
                sqlSB.append("`").append(fieldModel.getName()).append("` ");
                sqlSB.append(sqlTypeFor(fieldModel.getType()));

                if (nCampos > 0) sqlSB.append(",");
                sqlSB.append("\n");
            }

            sqlSB.append(");\n");
        }

        return sqlSB.toString();
    }

    public boolean executeDDl() {
        boolean resultado = true;
        if (conexion == null) conexion = Conexion.getConnection();

        for (TableModel tableModel : wbm.getTables()) {
            StringBuilder sqlSB = new StringBuilder();
            sqlSB.append("CREATE TABLE ").append(tableModel.getName()).append("(\n");

            int nCampos = tableModel.getFields().size();
            for (FieldModel fieldModel : tableModel.getFields()) {
                nCampos--;
                sqlSB.append("`").append(fieldModel.getName()).append("` ");
                sqlSB.append(sqlTypeFor(fieldModel.getType()));

                if (nCampos > 0) sqlSB.append(",");
                sqlSB.append("\n");
            }

            sqlSB.append(");\n");

            try (Statement stmt = conexion.createStatement()) {
                stmt.execute(sqlSB.toString());
            } catch (Exception e) {
                resultado = false;
            }
        }

        return resultado;
    }
}
