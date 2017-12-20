package com.bignerdranch.android.rabotnik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class FragmentFindVacancy extends Fragment {
    RecyclerView recFav, recAll;
    ArrayList <Poster> mPosters;
    ImageButton btnAdd;
    public static Handler handler = new Handler(Looper.getMainLooper());
    RecyclerView.Adapter adapter;
    ArrayList<String> mCategories = new ArrayList<>();
    ArrayList<String> mSallaries = new ArrayList<>();
    Button btnCateg, btnSallary, btnCity;
    public static final int REQUEST_CODE_CATEGORY = 1;
    public static final int REQUEST_CODE_CITY = 2;
    public static final int REQUEST_CODE_SALLARY = 3;
    FindPost mFindPost = new FindPost();
    User mUser;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public static FragmentFindVacancy newInstance(){
        Log.e("MyLog", "Создал обьект фрагмента с поиском резюме");
        FragmentFindVacancy fragement = new FragmentFindVacancy();
        return fragement;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MyLog", "onCreate");
        mPosters = new ArrayList<>();
        handler = new MyHandler();
        setHasOptionsMenu(true);
        mUser = ((MainActivity) getActivity()).getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("MyLog", "отобразил фрагмент поиска резюме");
        View v = inflater.inflate(R.layout.resume_layout, container, false);

        recAll = (RecyclerView) v.findViewById(R.id.rec_view_all);
        recAll.setLayoutManager(new LinearLayoutManager(getActivity()));
        btnCateg = (Button) v.findViewById(R.id.btn_filter_category);
        btnSallary = (Button) v.findViewById(R.id.btn_filter_sallary);
        btnCity = (Button) v.findViewById(R.id.btn_filter_city);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findPosters();
            }
        });

        btnCateg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogCategories();
            }
        });
        btnCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogCity();
            }
        });
        btnSallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogSallary();
            }
        });
        getActivity().setTitle("Искать вакансии");
        getCategories();
        return v;
    }

    public void openDialogCategories(){
        ListDialog fragment = ListDialog.newInstance(mCategories);
        fragment.setTargetFragment(this, REQUEST_CODE_CATEGORY);
        fragment.show(getFragmentManager(), fragment.getClass().getName());
    }
    public void openDialogCity(){
        CityDialog fragment = new CityDialog();
        fragment.setTargetFragment(this, REQUEST_CODE_CITY);
        fragment.show(getFragmentManager(), fragment.getClass().getName());
    }
    public void openDialogSallary(){
        SallaryDialog fragment = new SallaryDialog();
        fragment.setTargetFragment(this, REQUEST_CODE_SALLARY);
        fragment.show(getFragmentManager(), fragment.getClass().getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if(requestCode == REQUEST_CODE_CATEGORY) {
                int result = data.getIntExtra(ListDialog.KEY_RESULT, 0);
                btnCateg.setText(mCategories.get(result));
                if(result == 0){
                    mFindPost.setCategory(null);
                }else{
                    mFindPost.setCategory(mCategories.get(result));
                }
                findPosters();
            }else if(requestCode == REQUEST_CODE_CITY) {
                String result = data.getStringExtra(CityDialog.KEY_RESULT);
                if(result.equals("")){
                    mFindPost.setCity(null);
                    btnCity.setText("Все города");
                }else{
                    btnCity.setText(result);
                    mFindPost.setCity(result);
                }
                findPosters();
            }else if(requestCode == REQUEST_CODE_SALLARY) {
                String result = data.getStringExtra(CityDialog.KEY_RESULT);
                if(result.equals("")){
                    mFindPost.setSallary(null);
                    btnSallary.setText("Без ограничений");
                }else{
                    mFindPost.setSallary(result);
                    btnSallary.setText("От " + result + " грн");
                }
                findPosters();
            }
        }
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e("MyLog", "Словил сообщение в фрагменте поиска вакансий");
            super.handleMessage(msg);
            int what = msg.what;
            if(what == MyService.KEY_UPDATE){
                Bundle bundle = msg.getData();
                String jsonString = bundle.getString(MyService.KEY_JSON_RESULT);
                Log.e("MyLog", "Строка с найденными вакансиями:" + jsonString);
                Type listType = new TypeToken<ArrayList<Poster>>(){}.getType();
                mPosters = new Gson().fromJson(jsonString, listType);
                updateUI();
                mSwipeRefreshLayout.setRefreshing(false);
            }else if(what == MyService.KEY_RETURN_CATEGORIES){
                Bundle bundle = msg.getData();
                String jsonString = bundle.getString(MyService.KEY_JSON_RESULT);
                Type listType = new TypeToken<ArrayList<String>>(){}.getType();
                mCategories = new Gson().fromJson(jsonString, listType);
                mCategories.add(0, "Все категории");
            }
        }
    }

    public void updateUI(){
        adapter = new PostAdapter(mPosters);
        recAll.setAdapter(adapter);
    }

    public void getCategories(){
        MesToServer mts = new MesToServer(MyService.KEY_COMMAND_GET_CATEGORIES, null);     //Теперь запрашиваем список категорий
        String jsonMes = new Gson().toJson(mts);
        Intent i = new Intent(getActivity(), MyService.class);
        i.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes);
        i.putExtra(MyService.SENDER, MyService.SENDER_FIV);
        getActivity().startService(i);
    }

    public void findPosters(){
        String data = new Gson().toJson(mFindPost);
        MesToServer mts = new MesToServer(MyService.KEY_COMMAND_FIND_VACANCIES, data);
        String jsonMes = new Gson().toJson(mts);
        Intent i = new Intent(getActivity(), MyService.class);
        i.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes);
        i.putExtra(MyService.SENDER, MyService.SENDER_FIV);
        getActivity().startService(i);
    }

    public void toggleFavorite(Poster mPoster){
        String data = new Gson().toJson(mPoster);
        MesToServer mts = new MesToServer(MyService.KEY_COMMAND_TOGGLE_FAV_VACANCY, data);
        String jsonMes = new Gson().toJson(mts);
        Intent i = new Intent(getActivity(), MyService.class);
        i.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes);
        i.putExtra(MyService.SENDER, MyService.SENDER_FIV);
        getActivity().startService(i);
    }

    public class PostHolder extends RecyclerView.ViewHolder {
        ImageButton btnFav;
        TextView tvTitle, tvCity, tvSallary;
        Poster mPoster;

        public PostHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvCity = (TextView) itemView.findViewById(R.id.tv_city);
            tvSallary = (TextView) itemView.findViewById(R.id.tv_sallary);
            btnFav = (ImageButton) itemView.findViewById(R.id.btn_fav);

            btnFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFavorite(mPoster);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String json = new Gson().toJson(mPoster);
                    Log.e("MyLog", "jsonString = " + json);
                    Intent i = PostShowActivity.newIntent(getActivity(), json);
                    startActivity(i);
                }
            });
        }

        public void bindViewHolder(Poster poster) {
            this.mPoster = poster;
            tvTitle.setText(mPoster.getTitle());
            tvSallary.setText(mPoster.getSallary());
            tvCity.setText(mPoster.getCity());

            setActiv(btnFav, mPoster);

            btnFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Poster poster = new Poster();
                    poster.setIdCreator(mUser.getId());
                    poster.setId(mPoster.getId());
                    String data = new Gson().toJson(poster);

                    MesToServer mts = new MesToServer(MyService.KEY_COMMAND_TOGGLE_FAV, data);
                    String jsonMes = new Gson().toJson(mts);
                    Intent i = new Intent(getActivity(), MyService.class);
                    i.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes);
                    i.putExtra(MyService.SENDER, MyService.SENDER_WF);
                    getActivity().startService(i);

                    mPoster.setFavorite(!mPoster.isFavorite());
                    setActiv(btnFav, mPoster);
                }
            });
        }
    }

    public void setActiv(ImageButton btn, Poster p){
        if(p.isFavorite()){
            btn.setImageResource(R.drawable.ic_action_delete_from_favorite);
        }else{
            btn.setImageResource(R.drawable.ic_action_add_to_favorite);
        }
    }

    public class PostAdapter extends RecyclerView.Adapter<PostHolder>{
        private ArrayList<Poster> mPosters;

        public PostAdapter(ArrayList<Poster> posters) {
            mPosters = posters;
        }

        @Override
        public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.list_item, parent, false);
            return new PostHolder(v);
        }

        @Override
        public void onBindViewHolder(PostHolder holder, int position) {
            Poster poster = mPosters.get(position);
            holder.bindViewHolder(poster);
        }

        @Override
        public int getItemCount() {
            return mPosters.size();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_find, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s.equals("")){
                    mFindPost.setWord(null);
                }else{
                    mFindPost.setWord(s);
                }
                findPosters();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        findPosters();
    }
}
