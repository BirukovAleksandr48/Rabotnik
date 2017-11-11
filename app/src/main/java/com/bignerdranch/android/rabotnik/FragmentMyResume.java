package com.bignerdranch.android.rabotnik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
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

public class FragmentMyResume extends Fragment {
    RecyclerView recAll;
    ArrayList <Poster> mPosters;
    public static Handler handler = new Handler();
    RecyclerView.Adapter adapter;
    FindPost mFindPost = new FindPost();
    User mUser;

    public static FragmentMyResume newInstance(){
        Log.e("MyLog", "Создал обьект мои резюме");
        FragmentMyResume fragement = new FragmentMyResume();
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
        mFindPost.setIdCreator(mUser.getId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("MyLog", "отобразил фрагмент моих резюме");
        View v = inflater.inflate(R.layout.my_resume_layout, container, false);

        recAll = (RecyclerView) v.findViewById(R.id.rec_view_all);
        recAll.setLayoutManager(new LinearLayoutManager(getActivity()));

        getActivity().setTitle("Мои резюме");
        return v;
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.e("MyLog", "Словил сообщение в фрагменте моих резюме");
            super.handleMessage(msg);
            int what = msg.what;

            if(what == MyService.KEY_UPDATE){
                Bundle bundle = msg.getData();
                String jsonString = bundle.getString(MyService.KEY_JSON_RESULT);
                Log.e("MyLog", jsonString);
                Type listType = new TypeToken<ArrayList<Poster>>(){}.getType();
                mPosters = new Gson().fromJson(jsonString, listType);
                updateUI();
            }
        }
    }

    public void updateUI(){
        adapter = new PostAdapter(mPosters);
        recAll.setAdapter(adapter);
    }

    public void findPosters(){
        String data = new Gson().toJson(mFindPost);
        MesToServer mts = new MesToServer(MyService.KEY_COMMAND_FIND_RESUMES, data);
        String jsonMes = new Gson().toJson(mts);
        Intent i = new Intent(getActivity(), MyService.class);
        i.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes);
        i.putExtra(MyService.SENDER, MyService.SENDER_MR);
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String json = new Gson().toJson(mPoster);
                    Log.e("MyLog", "jsonString = " + json);
                    Intent i = PostEditActivity.newIntent(getActivity(), json);
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
                    poster.setId(mPoster.getId());
                    String data = new Gson().toJson(poster);

                    MesToServer mts = new MesToServer(MyService.KEY_COMMAND_TOGGLE_FAV, data);
                    String jsonMes = new Gson().toJson(mts);
                    Intent i = new Intent(getActivity(), MyService.class);
                    i.putExtra(MyService.KEY_MESSAGE_TO_SERVER, jsonMes);
                    i.putExtra(MyService.SENDER, MyService.SENDER_MR);
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
        menuInflater.inflate(R.menu.menu_my, menu);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_post:
                Poster poster = new Poster();
                poster.setIdCreator(mUser.getId());
                String json = new Gson().toJson(poster);
                Intent i = PostEditActivity.newIntent(getActivity(), json);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        findPosters();
    }
}
