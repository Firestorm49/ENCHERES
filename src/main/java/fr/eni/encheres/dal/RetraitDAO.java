package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CCategorie;
import fr.eni.encheres.bo.CRetrait;

import java.util.List;

public interface RetraitDAO {
    String CreateRetrait(CRetrait Retrait);
    String ModifyRetrait(CRetrait Retrait);
    String DeleteRetrait(int id);
    CRetrait SearchRetrait(int id);
    CRetrait SearchRetraitByArticleID(int id);
}
