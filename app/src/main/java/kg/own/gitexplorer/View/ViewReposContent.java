package kg.own.gitexplorer.View;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kg.own.gitexplorer.Model.Data.SingleRepos;
import kg.own.gitexplorer.R;
import kg.own.gitexplorer.ViewModel.ReposViewViewModel;
import timber.log.Timber;

public class ViewReposContent extends AppCompatActivity implements ReposViewInterface {

    ReposViewViewModel reposViewViewModel;
    String path;
    String reposName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repose_view);
        retrieveDataFromIntent();
    }

    private void retrieveDataFromIntent() {
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            Timber.i("Bundle was null");
            return; }
        reposName = bundle.getString("repositoryToLoad");
        TextView textView = findViewById(R.id.tvTitle);
        textView.setText("Contents  - " + reposName);
        initializeModelView(bundle.getString("author"), bundle.getString("repositoryToLoad"));
    }

    private void initializeModelView(final String repositoryToLoad, final String author) {
        Timber.i("Initializing model view");
        reposViewViewModel = ViewModelProviders.of(this).get(ReposViewViewModel.class);
        reposViewViewModel.init(repositoryToLoad, author);

        reposViewViewModel.getSingleRepos().observe(this, new Observer<List<SingleRepos>>() {
            @Override
            public void onChanged(List<SingleRepos> singleRepos) {
                if(singleRepos!=null){
                    initializeUI(singleRepos);
                }else{
                    Timber.e("Error singleRepos was null");
                }
            }
        });
    }

    private void initializeUI(List<SingleRepos> singleRepos) {
        RecyclerView recyclerView = findViewById(R.id.RVrepos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new ReposRecyclerViewAdapter(singleRepos, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void filDirPressed(String file, String path) {
        Timber.i("File: %s; path: %s", file, path);
        this.path = path;
        reposViewViewModel.changePath(path);
    }

    @Override
    public void onBackPressed() {
        if(path == null){
            finish();
            return;
        }

        if(path.equals("")){
            finish();
        }else{
            if(path.contains("/")){
                path = path.substring(0,path.lastIndexOf("/"));
                Timber.i("new path: %s", path);
            }else{
                path = "";
            }
            reposViewViewModel.changePath(path);
        }
    }


}
