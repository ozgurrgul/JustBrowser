package com.browser.theme;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.browser.R;
import com.browser.base.BaseActivity;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JBB;
import com.browser.ui.widget.JTB;
import com.browser.ui.widget.jlist.JLBG;
import com.browser.ui.widget.jlist.JLTV;

/**
 * Created by ozgur on 02.09.2016.
 */
public class ThemesActivity extends BaseActivity implements JBB.JBottomBarClickListener, ThemeListener {

    private JTB topBar;
    private JBB bottomBar;
    private LayoutInflater mLayoutInflater;
    private RecyclerView recyclerView;
    private ThemesAdapter themesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        mLayoutInflater = LayoutInflater.from(this);
        topBar = (JTB) findViewById(R.id.TopBar);
        topBar.setTitle(R.string.themes);
        bottomBar = (JBB) findViewById(R.id.BottomBar);
        bottomBar.setjBottomBarClickListener(this);
        bottomBar.hideRemove();

        ThemeController.getInstance().register(this);
        changeTheme();

        /**/
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        themesAdapter = new ThemesAdapter();
        /**/
        GridLayoutManager llm = new GridLayoutManager(this, 3);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        //
        recyclerView.setAdapter(themesAdapter);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRemoveClick() {}

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        setBackgroundTheme();
    }


    private class ThemesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ThemeModelViewHolder(mLayoutInflater.inflate(R.layout.theme_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ThemeModelViewHolder rowHolder = (ThemeModelViewHolder) holder;
            final ThemeModel themeModel = ThemeController.getInstance().getThemeModels().get(position);

            rowHolder.name.setText(themeModel.name);

            if(themeModel.isUsing()) {
                rowHolder.selected.setVisibility(View.VISIBLE);
            } else {
                rowHolder.selected.setVisibility(View.GONE);
            }

            rowHolder.name.setBackgroundColor(themeModel.barBg);
            rowHolder.name.setTextColor(themeModel.barItemColor);

            //change theme!

            rowHolder.wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ThemeController.getInstance().changeTheme(themeModel.name);
                    themesAdapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public int getItemCount() {
            return ThemeController.getInstance().getThemeModels().size();
        }
    }

    private class ThemeModelViewHolder extends RecyclerView.ViewHolder {

        final JLBG wrapper;
        final JLTV name;
        final ImageView selected;

        public ThemeModelViewHolder(View itemView) {
            super(itemView);
            wrapper = (JLBG) itemView.findViewById(R.id.Wrapper);
            name = (JLTV) itemView.findViewById(R.id.ThemeNameTextView);
            selected = (ImageView) itemView.findViewById(R.id.SelectedImageView);
        }
    }
}
