package fr.eni.encheres.Tools;

public class ErrorCode {
    public static final String NO_ERROR = "Code 0 : Success";
    public static final String DUPLICATE_KEY = "Error Code 1 : Détection de clé dupliquer";
    public static final String INCORRECT_COLUMN_TYPE = "Error Code 2 : La valeur renseigner ne correspond pas au type de colonne";
    public static final String SQL_ERROR = "Error Code 3 : Erreur SQL non gérée";
    public static final String CONSTRAINT_VIOLATION = "Error Code 4 : Violation de contrainte dans la base de données";
    public static final String DEADLOCK_DETECTED = "Error Code 5 : Détection d'un deadlock dans la base de données";

}
