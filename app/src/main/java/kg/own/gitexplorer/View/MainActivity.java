package kg.own.gitexplorer.View;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import kg.own.gitexplorer.CrashHandler;
import kg.own.gitexplorer.Model.Data.Repos;
import kg.own.gitexplorer.R;
import kg.own.gitexplorer.ViewModel.MainViewModel;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MainViewModel mainViewModel;
    EditText etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeErrorHandling();
        checkForCrash();
        initializeViewModel();
        initializeUI();
    }

    private void checkForCrash() {
        Bundle extras = getIntent().getExtras();
        if(extras!= null){
            Timber.i("Crash results at - %s; cause - %s; Crash report - %s", DateFormat.getDateTimeInstance().format(new Date()), extras.getString("crashReport"), extras.getString("cause"));
        }
    }

    private void initializeErrorHandling() {
        Timber.plant(new Timber.DebugTree(){
            @NotNull
            @Override
            protected String createStackElementTag(@NotNull StackTraceElement element) {
                return String.format("C:%s:%s",super.createStackElementTag(element), element.getLineNumber());
            }
        });

        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));
    }

    private void initializeViewModel() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.init();
        mainViewModel.getRepos().observe(this, new Observer<List<Repos>>() {
            @Override
            public void onChanged(List<Repos> repos) {
                if(repos== null){
                    return;
                }
                if(repos.size() == 0){
                    Toast.makeText(MainActivity.this, "User has no repositories", Toast.LENGTH_SHORT).show();
                }else{
                    Switch aSwitch = findViewById(R.id.swKnownName);

                    if(aSwitch.isChecked()){
                        presentRepos(repos);
                    }else{
                        presentAllRepos(repos);
                    }

                }
                Timber.i("Size: %s", repos.size());
            }
        });
        mainViewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                Timber.e("error detected: %s" , error);
            }
        });

    }

    private void presentAllRepos(final List<Repos> repos) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] options = new String[repos.size()];
        final boolean[] results = new boolean[repos.size()];
        for (int i = 0; i < repos.size(); i++){
            options[i] = repos.get(i).getRepos_name();
            results[i] = false;
        }

        builder
            .setTitle("Select repository")
            .setMultiChoiceItems(options, results, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                    for (int i = 0; i < options.length; i++) {
                        if (i == which) {
                            results[i]=true;
                            ((AlertDialog) dialog).getListView().setItemChecked(i, true);
                        }
                        else {
                            results[i]=false;
                            ((AlertDialog) dialog).getListView().setItemChecked(i, false);
                        }
                    }

                }
            }).setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String repositoryToLoad = "";

                for (int i = 0; i < options.length; i++){
                    if(results[i]){
                        repositoryToLoad = options[i];
                    }
                }

                for (Repos repo: repos){
                    if(repo.getRepos_name().equals(repositoryToLoad)){
                        reviewRepository(
                            repo.getRepos_name(),
                            etAddress.getText().toString(),
                            repo.getDate_created(),
                            repo.getForkNum(),
                            repo.getOpenIssueNum()
                        );
                    }
                }

            }
        })
            .create().show();

    }


    private void presentRepos(final List<Repos> repos) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(this);
        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
        autoCompleteTextView.setLayoutParams(layoutParams);
        autoCompleteTextView.setCompletionHint("enter");

        final String[] autoFill = new String[repos.size()];

        for (int i = 0; i < repos.size(); i++){
            autoFill[i] = repos.get(i).getRepos_name();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, autoFill);
        autoCompleteTextView.setAdapter(adapter);

        builder
            .setView(autoCompleteTextView)
            .setTitle("Enter repository name")
            .setPositiveButton("Enter", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Repos chosenRepos = reposExists(autoCompleteTextView.getText().toString(), repos);

                if(chosenRepos!=null){
                    reviewRepository(
                        autoCompleteTextView.getText().toString(),
                        etAddress.getText().toString(),
                        chosenRepos.getDate_created(),
                        chosenRepos.getForkNum(),
                        chosenRepos.getOpenIssueNum()
                    );
                }else{
                    Toast.makeText(MainActivity.this, "Could not find that repository", Toast.LENGTH_SHORT).show();
                }
            }
        })
            .create().show();

    }

    private Repos reposExists(String reposName, List<Repos> reposList) {
        for (Repos repos : reposList) {
            if (reposName.equals(repos.getRepos_name())) {
                return repos;
            }
        }
        return null;
    }

    private void reviewRepository(String repositoryToLoad, String author, String dateCreated, int forkNum, int openIssueNum) {
        Intent intent = new Intent(this, ViewReposDetailed.class);
        intent.putExtra("repositoryToLoad", repositoryToLoad)
        .putExtra("author", author)
        .putExtra("dateCreated", dateCreated)
        .putExtra("forkNum", forkNum)
        .putExtra("openIssueNum", openIssueNum);
        startActivity(intent);
    }

    private void initializeUI() {
        findViewById(R.id.btnSubmit).setOnClickListener(this);
        etAddress = findViewById(R.id.etRepos);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSubmit:
                mainViewModel.setAddress(etAddress.getText().toString());
                break;
        }
    }
}
