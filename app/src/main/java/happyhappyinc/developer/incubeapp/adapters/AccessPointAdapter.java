package happyhappyinc.developer.incubeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import happyhappyinc.developer.incubeapp.R;
import happyhappyinc.developer.incubeapp.activities.MainActivity;
import happyhappyinc.developer.incubeapp.activities.WebViewActivity;
import happyhappyinc.developer.incubeapp.models.AccessPointModel;
import happyhappyinc.developer.incubeapp.preferences.PreferencesManager;

/**
 * Created by DASAROLLOHAPPYHAPPY on 29/08/2017.
 */

public class AccessPointAdapter extends RecyclerView.Adapter<AccessPointAdapter.AccessPointViewHolder>  {

    // Variables globales para inicializar nuestra grilla de puntos de acceso
    private List<AccessPointModel> mAccessPointList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    PreferencesManager mPref;

    // Inicializamos los componentes en el constructor
    public AccessPointAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mPref = new PreferencesManager(mContext);
    }

    // Método que es llamado cuando el servicio retorna 1 o más puntos de acceso
    public void setAccessPointList(List<AccessPointModel> list) {
        mAccessPointList = list;
        notifyDataSetChanged();
    }

    // Referenciamos la vista con la que vamos a crear la grilla
    @Override
    public AccessPointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.custom_item_accesspoint, parent, false);
        return new AccessPointViewHolder(view);
    }

    // Asignamos todos los valores a cada item de nuestra lista, Imagen y Nombre de punto de acceso
    @Override
    public void onBindViewHolder(AccessPointViewHolder holder, int position) {
        final AccessPointModel accesspoint = mAccessPointList.get(position);

        holder.tv_name.setText(accesspoint.getName());
        byte[] bytarray = Base64.decode(accesspoint.getIcon(), Base64.DEFAULT);
        Glide.with(mContext)
                .load(bytarray)
                .into(holder.iv_icon);

        holder.rl_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.ACCESSPOINTS_USER = accesspoint;
                WebViewActivity.USER_SESSION = MainActivity.SESSION_USER;
                mContext.startActivity(new Intent(mContext, WebViewActivity.class));
            }
        });
    }

    // Obtenemos la cantidad de items que se deben pintar en la grilla
    @Override
    public int getItemCount() {
        return mAccessPointList.size();
    }

    // Inicializamos los componentes de la vista para poder usarlos
    public class AccessPointViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rl_icon;
        private ImageView iv_icon;
        private TextView tv_name;

        public AccessPointViewHolder(View itemView) {
            super(itemView);

            rl_icon = (RelativeLayout) itemView.findViewById(R.id.rl_icon);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
