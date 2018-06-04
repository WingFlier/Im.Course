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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{

    private TextView mTextMessage;
    private static MainActivity instance;

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String SESSION_COOKIE = "ASP.NET_SessionId";

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mTextMessage = (TextView) findViewById(R.id.message);

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
        //TODO split login and other requests part
        new Api().init(this).getAvailableClasses(new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                ArrayList<HashMap<String, String>> classesList = new ArrayList<>();
                mTextMessage.setText("Response is: " + response);
                Document parse = Jsoup.parse(response);
                Elements tables = parse.select("table");
                Element table = tables.get(0);
                if (table.id().equals("notAvClasses"))
                    table = tables.get(1);

                Elements classes = table.select("tr");
                for (int i = 1; i < classes.size(); i++)
                {
                    HashMap<String, String> map = new HashMap<>();
                    Element course = classes.get(i);
                    String text = course.select("td").select("div").select("div").text();
                    String id = table.select("tr").get(3).select("div").select("div").select("a").attr("onClick");
                    map.put("id", id.substring(id.indexOf("(") + 1, id.indexOf(",")));
                    map.put("title", text);
                    classesList.add(map);
                }
                System.out.println("");

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
