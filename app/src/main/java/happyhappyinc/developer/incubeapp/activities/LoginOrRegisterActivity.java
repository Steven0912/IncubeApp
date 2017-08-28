package happyhappyinc.developer.incubeapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import happyhappyinc.developer.incubeapp.R;

public class LoginOrRegisterActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_register);

        mContext = this;

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        initComponents();
    }

    private void initComponents() {
        TextView tv_slogan = (TextView) findViewById(R.id.tv_slogan);
        String sourceString = "Lo que<br><b>quieres<br></b>con lo que<br><b>tienes</b>";
        tv_slogan.setText(Html.fromHtml(sourceString));
    }

    public void signin(View v) {
        startActivity(new Intent(mContext, LoginActivity.class));
    }

    public void signup(View v) {
        startActivity(new Intent(mContext, RegisterActivity.class));
    }
}
