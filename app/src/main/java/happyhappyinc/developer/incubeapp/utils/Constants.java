package happyhappyinc.developer.incubeapp.utils;

/**
 * Created by DASAROLLOHAPPYHAPPY on 25/08/2017.
 */

public class Constants {
    public static final int DETAIL_CODE = 100;

    /**
     * Transición Detalle -> Actualización
     */
    public static final int UPDATE_CODE = 101;
    /**
     * Puerto que utilizas para la conexión.
     * Dejalo en blanco si no has configurado esta carácteristica.
     */
    private static final String PUERTO_HOST = ":";
    /**
     * Dirección IP del SERVIDOR
     */
    private static final String IP = "happyhappyinc.com/ws";
    /**
     * URLs del Web Service
     */

    public static final String USERS = "http://" + IP + "/IncubeWebService/users";
    public static final String USERS_LOGIN = "http://" + IP + "/IncubeWebService/checkLogin";
    public static final String ACCESS_POINTS = "http://" + IP + "/IncubeWebService/accesspoints";

    /**
     * Clave para el valor extra que representa al identificador de una meta
     */
    public static final String EXTRA_ID = "IDEXTRA";
}
