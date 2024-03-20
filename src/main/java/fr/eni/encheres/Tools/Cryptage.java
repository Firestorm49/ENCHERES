package fr.eni.encheres.Tools;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class Cryptage {
    public String cryptageBCrypt(String mdp) {
        return BCrypt.hashpw(mdp, BCrypt.gensalt());
    }
    public boolean checkPassword(String mdpTest, String mdp) {
        return BCrypt.checkpw(mdpTest, mdp);
    }
}
