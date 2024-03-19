package fr.eni.encheres.dal;

import fr.eni.encheres.bo.CCategorie;
import fr.eni.encheres.bo.CRetrait;

import java.util.List;

public interface RetraitDAO {
    void CreateRetrait(CRetrait Retrait);
    void ModifyRetrait(CRetrait Retrait);
    void DeleteRetrait(int id);
    CRetrait SearchRetrait(int id);
    CRetrait SearchRetraitByArticleID(int id);
}
