package wingfly.com.imcourse;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Map;

public class MainActivity extends AppCompatActivity
{

    private TextView mTextMessage;
    private static MainActivity instance;

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "ASP.NET_SessionId";

    private SharedPreferences preferences;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final Response.Listener<String> suc = new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                mTextMessage.setText("Response is: " + response);
            }
        };
        final Response.ErrorListener err = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                mTextMessage.setText("That didn't work!");
            }


        };
        Api init = new Api().init(this);
        init.login(suc, err);
        init.getAvailableClasses(new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                mTextMessage.setText("Response is: " + response);
            }
        }, err);
    }

    public static MainActivity getInstance()
    {
        return instance;
    }

    public final void addSessionCookie(Map<String, String> headers)
    {
        String sessionId = preferences.getString(SESSION_COOKIE, "");
        if (sessionId.length() > 0)
        {
            StringBuilder builder = new StringBuilder();
            builder.append(SESSION_COOKIE);
            builder.append("=");
            builder.append(sessionId);
            if (headers.containsKey(COOKIE_KEY))
            {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
        }
    }

    public void saveCookie(String cookie)
    {
        SharedPreferences.Editor prefEditor = preferences.edit();
        prefEditor.putString(SESSION_COOKIE, cookie);
        prefEditor.apply();
    }
}
