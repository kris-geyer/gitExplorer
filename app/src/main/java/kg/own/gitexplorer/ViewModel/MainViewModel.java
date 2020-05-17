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

public class MainViewModel extends ViewModel implements AccessGitHubInterface {

    private MutableLiveData<List<Repos>> repos;
    private String address;
    private MutableLiveData<String> error;

    public void init(){
        if (repos == null){
            repos = new MutableLiveData<>();
            error = new MutableLiveData<>();
            address = "";
        }
    }

    public void setAddress (String address){
        this.address = address;
        getRepos();
    }

    public LiveData<List<Repos>> getRepos(){
        if(!address.equals("")){
            repos.setValue(GithubRepos.getInstance().getRepos(this,address).getValue());
        }
        return repos;
    }

    public LiveData<String> getError(){
        return error;
    }

    @Override
    public void retrievedRepository(ArrayList<Repos> repos) {
        Timber.i("MainViewModel size: %s", repos.size());
        this.repos.postValue(repos);
    }

    @Override
    public void failureToRetrieve(int error) {
        Timber.i("Failure to connect with github");
        switch (error){
            case 404:
                this.error.postValue("User not found");
                break;
            default:
                this.error.postValue("Error code: " + error);
                break;
        }

    }

    @Override
    public void retrievedRepository(List<SingleRepos> singleRepos) {

    }


}
