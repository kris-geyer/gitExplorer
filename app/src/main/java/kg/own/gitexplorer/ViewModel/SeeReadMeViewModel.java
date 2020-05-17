package kg.own.gitexplorer.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import kg.own.gitexplorer.Model.Data.SingleRepos;
import kg.own.gitexplorer.Model.Repository.AccessGitHubContentInterface;
import kg.own.gitexplorer.Model.Repository.GithubRepos;
import timber.log.Timber;

public class SeeReadMeViewModel extends ViewModel implements AccessGitHubContentInterface {

    MutableLiveData<String> readMe;

    String author, repositoryToLoad, readMeFileName;

    public void init(String readmeFileName, String repositoryToLoad, String author){
        readMe = new MutableLiveData<>();
        this.readMeFileName = readmeFileName;
        this.repositoryToLoad = repositoryToLoad;
        this.author = author;
    }

    public LiveData<String> getReadMe(){
        Timber.i("called getReadMe");
        GithubRepos.getInstance().getContent(author, repositoryToLoad, readMeFileName, this);
        return readMe;
    }

    @Override
    public void encounteredError(String error) {

    }

    @Override
    public void resultGenerated(String text) {
        readMe.postValue(text);
    }
}
