package nodo.crogers.exercisereminders;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import nodo.crogers.exercisereminders.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String NOTIFICATION_CHANNEL_ID = "Exercise_Reminders";
    public static final String NOTIFICATION_CHANNEL_NAME = "Exercise Reminders";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Reminders to perform exercises";


    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nodo.crogers.exercisereminders.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_exercises, R.id.nav_alarms, R.id.nav_about)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onResume() {
        super.onResume();
        onResumeFlow();
    }

    private void onResumeFlow() {
        // onResume is called automatically after the notifications permissions popup,
        // and after the app regains focus after the alarm permissions settings, which
        // causes this to be called repeatedly, and flow through the necessary checks
        // before scheduling an alarm.
        PreferenceManager preferenceManager = PreferenceManager.getInstance(this);
        if (!preferenceManager.termsAccepted()) {
            showTerms();
        } else if (!hasNotificationPermissions()) {
            requestNotificationPermissions();
        } else if (!ExerciseAlarm.hasPermission(this)) {
            requestAlarmPermissions();
        } else {
            ExerciseAlarm.scheduleIfUnscheduled(this);
        }
    }

    private void showTerms() {
        PreferenceManager preferenceManager = PreferenceManager.getInstance(this);
        AlertDialog createExerciseDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dont_get_injured))
                .setView(R.layout.terms_dialog)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.i_agree), ((dialog, which) -> {
                    preferenceManager.setTermsAccepted(true);
                    dialog.dismiss();
                    onResumeFlow();
                }))
                .setNegativeButton(getString(R.string.close_app), ((dialog, which) -> this.finishAndRemoveTask()))
                .create();

        createExerciseDialog.show();

        Spanned licenseLinkHtml = Html.fromHtml(getString(R.string.licenseLink), Html.FROM_HTML_MODE_LEGACY);
        TextView licenceLink = Objects.requireNonNull(createExerciseDialog.findViewById(R.id.licenseLinkTermsDialog));
        licenceLink.setText(licenseLinkHtml);
        licenceLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private boolean hasNotificationPermissions() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestNotificationPermissions() {
        if (!hasNotificationPermissions()) {
            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
        }
    }

    private void requestAlarmPermissions() {
        AlertDialog allowAlarmsDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.allow_exact_alarms))
                .setView(R.layout.alarm_permissions_dialog)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.go_to_permissions), ((dialog, which) -> {
                    dialog.dismiss();
                    startActivity(new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
                }))
                .setNegativeButton(getString(R.string.close_app), ((dialog, which) -> this.finishAndRemoveTask()))
                .create();
        allowAlarmsDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void randomExercise(MenuItem menuItem) {
        ExerciseAlarm.showNotification(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}