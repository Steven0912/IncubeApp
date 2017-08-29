package happyhappyinc.developer.incubeapp.activities;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import happyhappyinc.developer.incubeapp.R;
import happyhappyinc.developer.incubeapp.models.AccessPointModel;
import happyhappyinc.developer.incubeapp.models.UserModel;

public class WebViewActivity extends AppCompatActivity {

    // Modelo de los puntos de acceso del usuario, para poder obtener la URL y cargar el webview
    public static AccessPointModel ACCESSPOINTS_USER;

    public static UserModel USER_SESSION;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Inicializamos los componentes de la vista
        mContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(ACCESSPOINTS_USER.getName());

        // Obtenemos la URL del modelo global y cargamos el webview
        WebView web = (WebView) this.findViewById(R.id.webview);
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        web.setWebViewClient(new WebViewClient());
        web.loadUrl(ACCESSPOINTS_USER.getUrl() + "?nombre=" + USER_SESSION.getNombre_completo());
    }

    @Override
    public void onBackPressed() {
        // Bloquear boton de atrás
    }
}
