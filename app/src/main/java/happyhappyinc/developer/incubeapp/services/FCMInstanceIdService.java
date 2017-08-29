package happyhappyinc.developer.incubeapp.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import happyhappyinc.developer.incubeapp.preferences.PreferencesManager;

/**
 * Created by DASAROLLOHAPPYHAPPY on 29/08/2017.
 */

public class FCMInstanceIdService extends FirebaseInstanceIdService {

    public static String mRefreshedToken = "";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        mRefreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("TEST", "Refreshed token: " + mRefreshedToken);

        // Llamamos el método que almacena el token
        sendRegistrationToPreferences(mRefreshedToken);
    }

    // recibimos el token que obtiene la aplicación la primera vez que es instalada
    public void sendRegistrationToPreferences(String token) {

        // Obtenemos una instancia del preferences para almacenar el token
        PreferencesManager mPref = new PreferencesManager(this);

        // validamos de que ya tengamos un token
        if (!token.equals("")) {
            // guardamos el token en el perferences
            mPref.setToken(token);
        }
    }
}