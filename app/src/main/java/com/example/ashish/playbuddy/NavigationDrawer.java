package com.example.ashish.playbuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

public class NavigationDrawer extends AppCompatActivity implements AdminNewsFrag.OnFragmentInteractionListener ,NewsAdminRecyclerViewFrag.OnFragmentInteractionListener,fragment_news_user_recyclerview.OnFragmentInteractionListener,AdminEventRecyclerViewFrag.OnFragmentInteractionListener,AdminEventFrag.OnFragmentInteractionListener
{

    public static final String LOGTAG = "indus";


    String accountName;
    //email of user
    public static String accountEmail;
    Uri accountPicture;
    int accountLevel;
    ImageView imageView;
    Fragment fr;
    //save  header or drawer result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private IProfile profile;
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    //onCreate method of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {

                    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        //Setting toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.toolbar_name);


        //taking value from intent of login
        Intent intent = getIntent();
        accountName = intent.getStringExtra("name");
        accountEmail = intent.getStringExtra("email");
        accountLevel = intent.getIntExtra("level", 2);
        accountPicture = intent.getParcelableExtra("dp");

        //loading picture using Picasso
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }

        });


        //adding profile
        profile = new ProfileDrawerItem().withName(accountName).withEmail(accountEmail).withIcon(accountPicture);

        //create Account holder
        buildHeader(false, savedInstanceState);

        //create drawer
        if (accountLevel == 1) {

            admin(toolbar, savedInstanceState);
            fr=new AdminSportRecyclerViewFrag();
            fragmentCall(fr);


        } else {
            user(toolbar, savedInstanceState);
            fr=new fragment_news_user_recyclerview();
            fragmentCall(fr);
        }

       //for image display
       imageView=findViewById(R.id.navigation_image_view);

    }
    //built header part
    private void buildHeader(boolean compact, Bundle savedInstanceState) {
        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder().withActivity(this).withSelectionListEnabledForSingleProfile(true).withCompactStyle(compact)
                .addProfiles(profile).withSavedInstance(savedInstanceState).build();
    }

    /**
     * code for admin part
     *
     * @param toolbar
     * @param savedInstanceState
     */
    void admin(Toolbar toolbar, Bundle savedInstanceState) {

        final Intent intt = new Intent(this, Login_Main.class);
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withSliderBackgroundColor(Color.parseColor("#202a36"))//#3f42a4
                .addDrawerItems(new PrimaryDrawerItem().withName("Sports"),
                        new PrimaryDrawerItem().withName("Venue"),
                        new PrimaryDrawerItem().withName("News"),
                        new PrimaryDrawerItem().withName("Events"),
                        new SecondaryDrawerItem().withName("Logout").withIdentifier(5),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Toggle Image").withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener)

                ).withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View clickedView) {

                        //   NavigationDrawer.this.finish();

                        return true;
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                indusLog("admin sports");
                                fr=new AdminSportRecyclerViewFrag();
                                break;
                            case 2:
                                indusLog("admin add venue");
                                fr=new AdminVenueRecyclerViewfrag();
                                break;

                            case 3:
                                indusLog("admin news");
                                fr=new NewsAdminRecyclerViewFrag();
                                break;

                            case 4:
                                indusLog("admin events");
                                fr=new AdminEventRecyclerViewFrag();

                                break;

                            case 5:
                                indusToast(NavigationDrawer.this,"Admin Logout");
                                intt.putExtra("logouttoken", 1);
                                startActivity(intt);
                                finish();
                                break;
                            default:
                                //fr=new AdminSportFrag();
                               // indusToast(NavigationDrawer.this,"Admin Wrong Request");
                                break;
                        }

                        if(fr!=null) {
                                fragmentCall(fr);
                        }

                        return false;
                    }
                }).withSavedInstance(savedInstanceState).build();
    }

    /**
     * code for user part
     * @param toolbar
     * @param savedInstanceState
     */
    void user(Toolbar toolbar, Bundle savedInstanceState) {

        final Intent intt = new Intent(this, Login_Main.class);
        result = new DrawerBuilder().withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withSliderBackgroundColor(Color.parseColor("#202a36"))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("News"),
                        new PrimaryDrawerItem().withName("Interest"),

                        new PrimaryDrawerItem().withName("Events"),
                        new PrimaryDrawerItem().withName("Find Buddy"),
                        new SecondaryDrawerItem().withName("About us"),
                        new SecondaryDrawerItem().withName("Contact us"),
                        new SecondaryDrawerItem().withName("Logout"),
                        new DividerDrawerItem(),
                        new SwitchDrawerItem().withName("Toggle Image").withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener)

                ).withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View clickedView) {

                         // NavigationDrawer.this.finish();

                        return true;
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                fr = new fragment_news_user_recyclerview();
                                indusLog("news");

                                break;
                            case 2:
                                indusLog("interests");
                                fr=new UserInterestFrag();
                                break;
                            case 3:
                                indusLog("Events");
                                fr = new UserEventFrag();
                                break;
                            case 4:
                                fr = new UserFindBuddyFrag();
                                indusLog("find buddy");
                                break;
                            case 5:
                                fr=new farg_about_us();

                                indusLog("about us");
                                break;
                            case 6:
                                fr=new frag_contact_us();
                                indusLog("contact us");
                                break;
                            case 7:
                                indusToast(NavigationDrawer.this,"User Logout");
                                intt.putExtra("logouttoken", 1);
                                startActivity(intt);
                                finish();
                                break;
                            default:
                                indusToast(NavigationDrawer.this,"User Wrong Request");
                                break;
                        }
                        if(fr!=null) {
                            UserTImeLineRVFrag.classActive = false;
                            fragmentCall(fr);
                        }
                        return false;
                    }
                }).withSavedInstance(savedInstanceState).build();
    }

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                if(isChecked) {
                    imageView.setVisibility(View.VISIBLE);
                    Log.i("indus", "onChecked toggle: "+isChecked);
                }
                else
                {
                    imageView.setVisibility(View.INVISIBLE);
                    Log.i("indus", "onUnChecked toggle : "+isChecked);
                }
            } else {

                Log.i("indus", "Unknown in toggle: "+isChecked);
            }
        }
    };


    @Override
    protected void onSaveInstanceState(Bundle state) {

        state = result.saveInstanceState(state);

        state = headerResult.saveInstanceState(state);
        super.onSaveInstanceState(state);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }


    /////////////////////////
/*
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    //////////////////////*/




    public void indusLog(String message)
    {
        Log.i(LOGTAG,message);
    }

   public  void indusToast(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        indusLog(message);
    }

    public void fragmentCall(Fragment fr)
    {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fr);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
