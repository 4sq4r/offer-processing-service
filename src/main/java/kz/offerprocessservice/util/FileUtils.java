package kz.offerprocessservice.util;

import kz.offerprocessservice.model.enums.FileFormat;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

@Slf4j
@NoArgsConstructor
public class FileUtils {

    public static final String COMA = ",";
    public static final String OFFER_CODE = "OfferCode";
    public static final String OFFER_NAME = "OfferName";

    public static boolean isRowEmpty(Row r) {
        if (r == null) {
            return true;
        }

        for (int i = 0; i < r.getLastCellNum(); i++) {
            Cell cell = r.getCell(i);
            if (cell != null) {
                switch (cell.getCellType()) {
                    case STRING:
                        if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                            return false;
                        }
                        break;
                    case NUMERIC:
                    case BOOLEAN:
                    case FORMULA:
                        return false;
                    default:
                        break;
                }
            }
        }

        return true;
    }

    public static String getStringCellValue(Cell c) {
        if (c == null) {
            return null;
        }

        return switch (c.getCellType()) {
            case STRING -> c.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(Math.round(c.getNumericCellValue()));
            default -> null;
        };
    }

    public static void createCellAndValue(int index, Row row, String value) {
        Cell c = row.createCell(index);
        c.setCellValue(value);
    }

    public static String getContentDisposition(FileFormat format) {
        return "attachment; filename=template" + format.getExtension();
    }
}
