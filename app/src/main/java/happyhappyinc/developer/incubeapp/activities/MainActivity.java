package happyhappyinc.developer.incubeapp.activities;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import happyhappyinc.developer.incubeapp.R;
import happyhappyinc.developer.incubeapp.adapters.AccessPointAdapter;
import happyhappyinc.developer.incubeapp.models.AccessPointModel;
import happyhappyinc.developer.incubeapp.models.UserModel;
import happyhappyinc.developer.incubeapp.network.VolleySingleton;
import happyhappyinc.developer.incubeapp.preferences.PreferencesManager;
import happyhappyinc.developer.incubeapp.utils.Constants;
import happyhappyinc.developer.incubeapp.utils.Utils;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /*
        Variables globales que controlan el contexto, componentes de la vista, parsear datos del servicio, manejar las
        preferencias, manejar el swipeRefresh para hacer de nuevo la petición y los datos del usuario actual logueado
    */
    public static UserModel SESSION_USER;
    private Context mContext;
    private AccessPointAdapter mAdapter;
    private RecyclerView mListRecyclerView;
    private Gson gson = new Gson();
    private PreferencesManager mPref;
    private ImageView mImageNetwork;
    private TextView mLblNetwork;
    private SwipeRefreshLayout mSwipe;
    public static boolean WELCOME = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos los componentes
        mContext = this;
        mPref = new PreferencesManager(mContext);

        if (WELCOME) {
            Utils.alertSuccess(mContext, "Bienvenido " + SESSION_USER.getNombre_completo(), "Te ves muy bien hoy!").show();
        }
        WELCOME = false;

        initComponents();

        setUpToolbar();
        mSwipe.setColorSchemeResources(R.color.colorPrimary);
        mSwipe.setOnRefreshListener(this);
    }

    // Método que al ejecutarse hace el llamado al consumo del Servicio
    @Override
    protected void onResume() {
        super.onResume();
        loadAccessPoints();
    }

    // Inicializando los componentes de la vista
    private void initComponents() {
        mListRecyclerView = (RecyclerView) findViewById(R.id.rv_accesspoint_list);
        mListRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mImageNetwork = (ImageView) findViewById(R.id.iv_networkoff);
        mLblNetwork = (TextView) findViewById(R.id.tv_networkoff);
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.sr_webservice);
    }

    // Configurar la toolbar para poner titulo
    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Puntos de acceso");
    }

    // Método que hace el consumo del servicio y trae los puntos de acceso del usuario actual logueado
    private void loadAccessPoints() {

        String ruta = Constants.ACCESS_POINTS + "/" + mPref.checkId();

        VolleySingleton.
                getInstance(mContext).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                ruta,
                                null,
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
                                        mLblNetwork.setText("No hay conexión a Internet");
                                        if (mSwipe.isRefreshing()) {
                                            mSwipe.setRefreshing(false);
                                        }
                                    }
                                }
                        )
                        {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("Authorizationh", Utils.md5("H*2017*" + Utils.getDay()));
                                return params;
                            }
                        }
                );
    }

    // Método donde manipulamos los datos que retorna el servicio
    private void proccesingResponse(JSONObject response) {

        if (mSwipe.isRefreshing()) {
            mSwipe.setRefreshing(false);
        }

        try {
            String state = response.getString("state");

            switch (state) {
                case "1":

                    JSONArray datas = response.getJSONArray("accesspoints");

                    // Parsear con Gson
                    AccessPointModel[] accesspoints = gson.fromJson(datas.toString(), AccessPointModel[].class);

                    // verificar si el usuario solo tiene un punto de acceso para enviarlo directamente al webview
                    if (Arrays.asList(accesspoints).size() == 1) {
                        AccessPointModel current = new AccessPointModel();
                        current.setId(Arrays.asList(accesspoints).get(0).getId());
                        current.setName(Arrays.asList(accesspoints).get(0).getName());
                        current.setUrl(Arrays.asList(accesspoints).get(0).getUrl());
                        current.setIcon(Arrays.asList(accesspoints).get(0).getIcon());

                        WebViewActivity.ACCESSPOINTS_USER = current;
                        WebViewActivity.USER_SESSION = SESSION_USER;
                        mPref.changeActivity(this, WebViewActivity.class);
                        return;
                    }

                    mAdapter = new AccessPointAdapter(this);
                    mAdapter.setAccessPointList(Arrays.asList(accesspoints));
                    mListRecyclerView.setAdapter(mAdapter);

                    mImageNetwork.setVisibility(View.GONE);
                    mLblNetwork.setVisibility(View.GONE);
                    mListRecyclerView.setVisibility(View.VISIBLE);

                    break;
                case "2":

                    mImageNetwork.setImageResource(R.drawable.empty);
                    mImageNetwork.setVisibility(View.VISIBLE);
                    mLblNetwork.setVisibility(View.VISIBLE);
                    mListRecyclerView.setVisibility(View.GONE);
                    mLblNetwork.setText("No tienes puntos de acceso asignados");

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        loadAccessPoints();
    }
}
