package com.bignerdranch.android.rabotnik;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public final static String KEY_DATA_INTENT = "KEY_DATA_INTENT";
    private static User mUser;

    public static Intent newIntent(Context context, String user){
        Log.e("MyLog", "Создал экземпляр MainActivity");
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(KEY_DATA_INTENT, user);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String jsonUser = intent.getStringExtra(KEY_DATA_INTENT);
        mUser = new Gson().fromJson(jsonUser, User.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.e("MyLog", "Сейчас отображу фрагмент с поиском резюме");
        Fragment f = FragmentMyResume.newInstance();
        setFragment(f);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_name = (TextView)hView.findViewById(R.id.nav_name);
        TextView nav_email = (TextView)hView.findViewById(R.id.nav_email);
        ImageView nav_avatar = (ImageView)hView.findViewById(R.id.nav_avatar);

        nav_name.setText(mUser.getName());
        nav_email.setText(mUser.getLogin());
        nav_avatar.setImageResource(R.drawable.avatar);

        setResult(SignActivity.code_exit);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Log.e("MyLog", "кнопка назад нажата");
            finish();
            //super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_my_res) {
            fragmentClass = FragmentMyResume.class;
        } else if (id == R.id.nav_find_res) {
            fragmentClass = FragmentFindResume.class;
        } else if (id == R.id.nav_fav_res) {
            fragmentClass = FragmentFavResume.class;
        } else if (id == R.id.nav_my_vac) {

        } else if (id == R.id.nav_find_vac) {

        }else if (id == R.id.nav_fav_vac) {

        }else if (id == R.id.nav_profile) {
            fragmentClass = FragmentUserProfile.class;
        }else if (id == R.id.nav_exit) {
            Log.e("MyLog", "кнопка выйти нажата");
            setResult(SignActivity.code_change_user);
            finish();
        }
        if(fragmentClass != null){
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        }

        // Выделяем выбранный пункт меню в шторке
        //item.setChecked(true);
        // Выводим выбранный пункт в заголовке
        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(Fragment fragment){
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public User getUser(){
        return mUser;
    }
}
