package pe.edu.upeu.syslibrary.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String convertToDatabaseColumn(LocalDate locDate) {
        return (locDate == null ? null : locDate.format(FORMATTER));
    }

    @Override
    public LocalDate convertToEntityAttribute(String sqlDate) {
        if (sqlDate == null) return null;

        try {
            // ✅ Caso 1: formato normal "yyyy-MM-dd"
            return LocalDate.parse(sqlDate, FORMATTER);
        } catch (Exception e) {
            try {
                // ✅ Caso 2: timestamp tipo "1762232400000"
                long millis = Long.parseLong(sqlDate);
                return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
            } catch (Exception ex) {
                System.err.println("⚠️ Fecha no válida en BD: " + sqlDate);
                return null;
            }
        }
    }
}
