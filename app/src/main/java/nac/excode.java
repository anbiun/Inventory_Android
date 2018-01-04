package nac;

/**
 * Created by nac on 22/12/2560.
 */

public class excode {
    public static final Integer Cantconnect = 1;
    public static final Integer Notfound = 2;
    public static final Integer CantInsert = 3;
    public static final Integer Success = 10;
    private Integer curCode;
    public void Setcod(Integer newVal) {
        curCode = newVal;
    }
    public Integer Getcode() {
        return this.curCode;
    }
}

