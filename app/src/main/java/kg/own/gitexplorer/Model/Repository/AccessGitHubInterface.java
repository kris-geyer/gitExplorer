package kg.own.gitexplorer.Model.Repository;

import java.util.ArrayList;
import java.util.List;

import kg.own.gitexplorer.Model.Data.Repos;
import kg.own.gitexplorer.Model.Data.SingleRepos;

public interface AccessGitHubInterface {

    void retrievedRepository(ArrayList<Repos> repos);
    void failureToRetrieve(int error);
    void retrievedRepository(List<SingleRepos> singleRepos);
}
