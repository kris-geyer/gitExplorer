package kg.own.gitexplorer.Model.Repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import kg.own.gitexplorer.Model.Data.Repos;
import kg.own.gitexplorer.Model.Data.SingleRepos;

public class GithubRepos  {

    private static GithubRepos instance;
    private static ArrayList<Repos> reposData;
    private MutableLiveData<List<Repos>> repo = new MutableLiveData<>();
    private MutableLiveData<List<SingleRepos>> detailedRepos = new MutableLiveData<>();

    public static GithubRepos getInstance() {
        if (instance == null){
            instance = new GithubRepos();
            reposData = new ArrayList<>();
        }
        return instance;
    }

    public LiveData<List<Repos>> getRepos(AccessGitHubInterface accessGitHubInterface, String address) {

        AccessGitHub accessGitHub = new AccessGitHub(accessGitHubInterface, address);
        accessGitHub.execute();
        return repo;
    }


    public LiveData<List<SingleRepos>> getDetailedRepos(AccessGitHubInterface accessGitHubInterface, String repositoryToLoad, String author, String path) {
        AccessGitHub accessGitHub = new AccessGitHub(accessGitHubInterface, repositoryToLoad, author, path);
        accessGitHub.execute();

        return detailedRepos;
    }
}
