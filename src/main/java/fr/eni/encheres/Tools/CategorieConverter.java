package fr.eni.encheres.Tools;

import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bo.CCategorie;
import org.springframework.core.convert.converter.Converter;

public class CategorieConverter implements Converter<String, CCategorie> {
        private CategorieService categorieService;
        @Override
        public CCategorie convert(String source) {
            return categorieService.SearchCategorie(Integer.parseInt(source));
        }
        public CategorieConverter(CategorieService categorieService) {
            super();
            this.categorieService = categorieService;
        }
}
