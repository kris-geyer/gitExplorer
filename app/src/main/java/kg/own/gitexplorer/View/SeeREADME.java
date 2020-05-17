package kg.own.gitexplorer.View;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import kg.own.gitexplorer.R;
import kg.own.gitexplorer.ViewModel.SeeReadMeViewModel;
import timber.log.Timber;

public class SeeREADME extends AppCompatActivity {

    private String readmeFileName, repositoryToLoad, author;

    private TextView title, content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_me);
        initializeUI();
        if(handleBundleObjects()){
            initializeViewModel();
        }else{
            content.setText("An error was encountered.");
        }
    }

    private void initializeUI() {
        title = findViewById(R.id.tvTitle);
        content = findViewById(R.id.tvReadMeContent);
        content.setMovementMethod(new ScrollingMovementMethod());
    }

    private boolean handleBundleObjects() {
        Bundle bundle = getIntent().getExtras();

        if(bundle== null){
            return false;
        }

        readmeFileName = bundle.getString("readMeFileName");
        repositoryToLoad = bundle.getString("repositoryToLoad");
        author = bundle.getString("author");

        title.setText(author + " - " + repositoryToLoad);

        return true;
    }

    private void initializeViewModel() {
        SeeReadMeViewModel seeReadMeViewModel = ViewModelProviders.of(this).get(SeeReadMeViewModel.class);
        seeReadMeViewModel.init(readmeFileName, repositoryToLoad, author);
        seeReadMeViewModel.getReadMe().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Timber.i("From seeReadMe activity: %s", s);
                content.setText(s);
            }
        });
    }
}
