package fr.eni.encheres.bo;

public class CRetrait {
    private int noRetrait;
    private String rue;
    private int code_postal;
    private String ville;
    public CRetrait() {

    }
    public CRetrait(int noRetrait,String rue, int code_postal, String ville) {
        this.noRetrait = noRetrait;
        this.rue = rue;
        this.code_postal = code_postal;
        this.ville = ville;
    }

    public int getNoRetrait() {
        return noRetrait;
    }

    public void setNoRetrait(int noRetrait) {
        this.noRetrait = noRetrait;
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public int getCode_postal() {
        return code_postal;
    }

    public void setCode_postal(int code_postal) {
        this.code_postal = code_postal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    @Override
    public String toString() {
        return "CRetrait{" +
                "rue=" + rue +
                ", code_postal=" + code_postal +
                ", ville=" + ville +
                '}';
    }
}
