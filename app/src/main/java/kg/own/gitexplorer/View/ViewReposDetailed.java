package kg.own.gitexplorer.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import kg.own.gitexplorer.Model.Data.SingleRepos;
import kg.own.gitexplorer.R;
import kg.own.gitexplorer.ViewModel.ReposViewViewModel;
import timber.log.Timber;

public class ViewReposDetailed extends AppCompatActivity implements View.OnClickListener {

    private String repositoryToLoad, author, readmeFileName;
    private TextView forkIssues, title;


    private ReposViewViewModel reposViewViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_specific_repos);
        initializeUI();
        if(getReposName()){
            initializeViewModel();
        }else{
            reportFailure();
        }
    }

    private void initializeUI() {
        forkIssues = findViewById(R.id.tvIssueFork);
        title = findViewById(R.id.tvName);

        findViewById(R.id.btnSeeContent).setOnClickListener(this);
        findViewById(R.id.btnSeeReadMe).setOnClickListener(this);
    }

    private void initializeViewModel() {
        Timber.i("Initializing model view");
        reposViewViewModel = ViewModelProviders.of(this).get(ReposViewViewModel.class);
        reposViewViewModel.init(author, repositoryToLoad);

        reposViewViewModel.getSingleRepos().observe(this, new Observer<List<SingleRepos>>() {
            @Override
            public void onChanged(List<SingleRepos> singleRepos) {
                if(singleRepos!=null){
                    findViewById(R.id.btnSeeContent).setEnabled(true);
                    boolean hasReadme = false;
                    for(SingleRepos repos: singleRepos){
                        Timber.i(repos.getName());
                        if(repos.getName().contains("README")){
                            readmeFileName = repos.getName();
                            hasReadme = true;
                            break;
                        }
                    }
                    if(hasReadme){
                        findViewById(R.id.btnSeeReadMe).setEnabled(true);
                    }else{
                        findViewById(R.id.btnSeeReadMe).setEnabled(false);
                    }
                }else{
                    Timber.e("Error singleRepos was null");
                    findViewById(R.id.btnSeeContent).setEnabled(false);
                }
            }
        });
    }

    private boolean getReposName() {
        Bundle bundle = getIntent().getExtras();

        if(bundle== null){
            Timber.i("Bundle is empty");
            return false;
        }

        repositoryToLoad = bundle.getString("repositoryToLoad");
        author = bundle.getString("author");
        title.setText(author + " - " + repositoryToLoad);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
            .append("Date created: ").append( bundle.getString("dateCreated")).append("\n")
            .append("Fork number: ").append(bundle.getInt("forkNum")).append("\n")
            .append("Number of issues: ").append(bundle.getInt("openIssueNum")).append("\n");

        forkIssues.setText(stringBuilder);
        return true;
    }

    private void reportFailure() {
        title.setText("Could not load repository with that name");
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btnSeeContent:
                intent = new Intent(this, ViewReposContent.class);
                intent.putExtra("repositoryToLoad", repositoryToLoad);
                intent.putExtra("author", author);
                startActivity(intent);
                break;
            case R.id.btnSeeReadMe:
                intent = new Intent(this, SeeREADME.class);
                intent.putExtra("repositoryToLoad", repositoryToLoad);
                intent.putExtra("author", author);
                startActivity(intent);
                break;
        }
    }
}
