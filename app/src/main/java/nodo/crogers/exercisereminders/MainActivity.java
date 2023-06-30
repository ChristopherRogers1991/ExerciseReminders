package nodo.crogers.exercisereminders;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
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
    private ActivityMainBinding binding;

    @Override
    public void onBackPressed() {
        View tags = binding.getRoot().findViewById(R.id.tagRecyclerView);
        View exercises = binding.getRoot().findViewById(R.id.exerciseRecyclerView);
        if (tags.getX() < 0) {
            ObjectAnimator tagAnimator = ObjectAnimator.ofFloat(tags, "translationX", 0);
            tagAnimator.setDuration(500);
            exercises.animate()
                    .x(tags.getWidth())
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            exercises.setVisibility(View.GONE);
                        }
                    });
            tagAnimator.start();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager preferenceManager = PreferenceManager.getInstance(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
        }

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        if (preferenceManager.termsAccepted()) {
            ExerciseAlarm.scheduleIfUnscheduled(this);
        } else {
            AlertDialog createExerciseDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dont_get_injured))
                    .setView(R.layout.terms_dialog)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.i_agree), ((dialog, which) -> {
                        preferenceManager.setTermsAccepted(true);
                        ExerciseAlarm.scheduleIfUnscheduled(this);
                        dialog.dismiss();
                    }))
                    .setNegativeButton(getString(R.string.close_app), ((dialog, which) -> this.finishAndRemoveTask()))
                    .create();

            createExerciseDialog.show();

            Spanned licenseLinkHtml = Html.fromHtml(getString(R.string.licenseLink), Html.FROM_HTML_MODE_LEGACY);
            TextView licenceLink = Objects.requireNonNull(createExerciseDialog.findViewById(R.id.licenseLinkTermsDialog));
            licenceLink.setText(licenseLinkHtml);
            licenceLink.setMovementMethod(LinkMovementMethod.getInstance());

        }
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