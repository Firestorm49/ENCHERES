package fr.eni.encheres.Tools;

import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bo.CCategorie;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeConverter implements Converter<String, LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    public LocalDateTime convert(String source) {
        return LocalDateTime.parse(source, FORMATTER);
    }

    public DateTimeConverter() {
        super();
    }
}