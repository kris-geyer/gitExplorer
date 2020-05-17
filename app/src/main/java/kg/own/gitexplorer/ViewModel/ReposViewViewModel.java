package kg.own.gitexplorer.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import kg.own.gitexplorer.Model.Data.Repos;
import kg.own.gitexplorer.Model.Data.SingleRepos;
import kg.own.gitexplorer.Model.Repository.AccessGitHubInterface;
import kg.own.gitexplorer.Model.Repository.GithubRepos;
import timber.log.Timber;

public class ReposViewViewModel extends ViewModel implements AccessGitHubInterface {

    MutableLiveData<List<SingleRepos>> singleRepos;
    private String author, repositoryToLoad, path;

    public void init(final String author, final String repositoryToLoad) {
        if( singleRepos == null){
            singleRepos = new MutableLiveData<>();
        }
        this.author = author;
        this.repositoryToLoad = repositoryToLoad;
        this.path = "";
    }

    public void changePath (String path){
        this.path = path;
        getSingleRepos();
    }

    public LiveData<List<SingleRepos>> getSingleRepos (){
        singleRepos.setValue((List<SingleRepos>) GithubRepos.getInstance().getDetailedRepos(this,repositoryToLoad,author, path).getValue());

        return singleRepos;
    }

    @Override
    public void retrievedRepository(ArrayList<Repos> repos) {
        //is not used
    }

    @Override
    public void failureToRetrieve(int error) {
        Timber.e("Failure to connect with github: %s", error);

    }

    @Override
    public void retrievedRepository(List<SingleRepos> singleRepos) {
        Timber.i("MainViewModel size: %s", singleRepos.size());
        this.singleRepos.postValue(singleRepos);
    }
}
