package fr.eni.encheres.bo;

public class CCategorie {
    private int noCategorie;
    private String libelle;
    public CCategorie() {

    }
    public CCategorie(int noCategorie, String libelle) {
        this.noCategorie = noCategorie;
        this.libelle = libelle;
    }

    public int getNoCategorie() {
        return noCategorie;
    }

    public void setNoCategorie(int noCategorie) {
        this.noCategorie = noCategorie;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public String toString() {
        return "CCategorie{" +
                "noCategorie=" + noCategorie +
                ", libelle=" + libelle +
                '}';
    }
}
