package fr.eni.encheres.bo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CRetrait {
    private int noRetrait;
    @NotBlank(message = "La rue n'est pas correcte")
    @Size(max=250)
    private String rue;
    @Max(99999)
    @Min(1)
    private int code_postal;
    @NotBlank(message = "La ville n'est pas correcte")
    @Size(max=250)
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
