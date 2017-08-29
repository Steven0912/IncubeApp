package happyhappyinc.developer.incubeapp.activities;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import happyhappyinc.developer.incubeapp.R;
import happyhappyinc.developer.incubeapp.models.UserModel;
import happyhappyinc.developer.incubeapp.network.VolleySingleton;
import happyhappyinc.developer.incubeapp.preferences.PreferencesManager;
import happyhappyinc.developer.incubeapp.utils.Constants;
import happyhappyinc.developer.incubeapp.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    /*
        Variables globales que controlan el contexto, componentes de la vista, parsear datos del servicio y manejar las
        preferencias
    */
    private Context mContext;
    private EditText[] mInputs = new EditText[2];
    private View.OnClickListener mBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!Utils.validateTextFormLogin(mInputs)) {
                return;
            }
            checkLogin();
        }
    };
    private Gson gson = new Gson();
    private PreferencesManager mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_login);
        mContext = this;
        mPref = new PreferencesManager(mContext);
        initComponents();
    }

    // Método donde inicializamos todos los componentes de la vista
    private void initComponents() {

        // Old design
        ImageView logo = (ImageView) findViewById(R.id.iv_logo);
        Glide.with(mContext)
                .load(R.drawable.logo_happy)
                .into(logo);

        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(mBtnClickListener);

        mInputs[0] = (EditText) findViewById(R.id.et_mail);
        mInputs[1] = (EditText) findViewById(R.id.et_password);
    }

    // Método que se ejecuta cuando se presiona el boton Ingresar, validamos las credenciales del usuario
    private void checkLogin() {

        final String mail = mInputs[0].getText().toString();
        final String password = mInputs[1].getText().toString();

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        // Guardamos en un HashMap los 3 parámetros necesarios para hacer el consumo del login
        map.put("mail", mail);
        map.put("password", password);
        map.put("token", mPref.getToken());

        // Armamos el JSONObject con los datos que serán enviados al servidor
        JSONObject jobject = new JSONObject(map);
        // Hacemos el consumo respectivo del login checkLogin

        VolleySingleton.
                getInstance(mContext).
                addToRequestQueue(

                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constants.USERS_LOGIN,
                                jobject,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        proccesingResponse(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Utils.alertError(mContext, "Verifica tu conexión a internet").show();
                                    }
                                }
                        ) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("Authorizationh", Utils.md5("H*2017*" + Utils.getDay()));
                                return params;
                            }
                        }
                );
    }

    // Método donde manipulamos los datos que nos retorna el WebService
    private void proccesingResponse(JSONObject response) {

        try {
            String state = response.getString("state");

            switch (state) {
                // En este caso la petición fue exitosa y retorna los datos correspondientes
                case "1":

                    JSONObject object = response.getJSONObject("user");
                    UserModel user = gson.fromJson(object.toString(), UserModel.class);

                    MainActivity.SESSION_USER = user;
                    MainActivity.WELCOME = true;

                    mPref.createLoginSession(String.valueOf(user.getId()), mPref.getToken());
                    mPref.changeActivity(LoginActivity.this, MainActivity.class);

                    break;
                // En este caso la petición se realizó, pero no fue exitosa
                case "2":

                    // Como no hubo éxito, mostramos al usuario cual fue el error que hubo!!!
                    Utils.alertError(mContext, response.getString("message")).show();

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
