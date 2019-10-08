package uk.aber.dcs.ac.uk.cs13620.weatherexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private WebView m_webView;
    private DatePicker m_datePicker;
    private Spinner m_weatherSpinner;
    private ProgressBar m_webViewProgressBar;
    private AppCompatActivity m_thisActivity;
    private boolean m_showWeather = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ensurePermissionsForString(Manifest.permission.INTERNET);

        m_webView = findViewById(R.id.webView);
        m_datePicker = findViewById(R.id.datePicker);
        m_weatherSpinner = findViewById(R.id.spinner);
        m_webViewProgressBar = findViewById(R.id.progressBar);
        m_webViewProgressBar.setIndeterminate(false);
        m_thisActivity = this;

        setupWebViewClient();
        setupWeatherSpinner();
        setupDatePicker();

        onDateChangeCalled();
    }

    private void setupWebViewClient() {
        m_webView.getSettings().setJavaScriptEnabled(true);

        m_webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(m_thisActivity, "Error Loading!" + description, Toast.LENGTH_SHORT).show();
            }
        });
        m_webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress > 0 && m_webViewProgressBar.getVisibility() == View.GONE) {
                    m_webViewProgressBar.setVisibility(View.VISIBLE);
                }
                m_webViewProgressBar.setProgress(newProgress);
                if (newProgress == 100){
                    m_webViewProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupWeatherSpinner() {
        m_weatherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                m_showWeather = (position == 0);
                onDateChangeCalled();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do Nothing
            }
        });
    }

    private void setupDatePicker() {
        if (m_datePicker == null){
            Toast.makeText(this, "No Date Picker!", Toast.LENGTH_SHORT).show();
        } else {
            Calendar cal = Calendar.getInstance();
            m_datePicker.init(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH),
                    new DatePicker.OnDateChangedListener() {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            onDateChangeCalled();
                        }
                    }
            );
        }
    }

    private void ensurePermissionsForString(String permission) {
        while (!checkForPermissions(permission)) {
            Toast.makeText(this, "No permissions given for camera!", Toast.LENGTH_LONG).show();
            askForPermissions(permission);
        }
    }

    private boolean checkForPermissions(String permission) {
        return ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void askForPermissions(String permission) {
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
    }

    private void onDateChangeCalled() {
        String url = "";
        if (m_showWeather) {
            url = "https://www.timeanddate.com/weather/uk/aberystwyth/historic?month="+
                    (m_datePicker.getMonth() + 1) + "&year="+ m_datePicker.getYear();
        } else {
            url = "http://www.webexhibits.org/calendars/moon.html?day="+
                    m_datePicker.getDayOfMonth() + "&month=" + (m_datePicker.getMonth() + 1)+
                    "&year=" + m_datePicker.getYear() + "#moonphase";
        }
        setUrl(url);
    }

    private void setUrl(String url) {
        if (m_webView == null) {
            Toast.makeText(this, "WebView is not Present!", Toast.LENGTH_SHORT).show();
        } else {
            m_webView.loadUrl(url);
        }
    }
}
