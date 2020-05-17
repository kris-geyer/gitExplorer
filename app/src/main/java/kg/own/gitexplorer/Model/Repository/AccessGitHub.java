package kg.own.gitexplorer.Model.Repository;

import android.os.AsyncTask;
import android.util.JsonReader;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import kg.own.gitexplorer.Model.Data.Repos;
import kg.own.gitexplorer.Model.Data.SingleRepos;
import timber.log.Timber;

public class AccessGitHub extends AsyncTask<Void, Void, Void> {
    private final AccessGitHubInterface accessGitHubInterface;
    private final String address, author, path;

    AccessGitHub(AccessGitHubInterface accessGitHubInterface, String address, String author, String path) {
        this.accessGitHubInterface = accessGitHubInterface;
        this.address = address;
        this.author = author;
        this.path = path;
    }

    AccessGitHub(AccessGitHubInterface accessGitHubInterface, String repositoryToLoad) {
        this.accessGitHubInterface = accessGitHubInterface;
        this.address = repositoryToLoad;
        this.author = "none";
        this.path = "";
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Timber.i("AccessGitHub called");
        if(author.equals("none")){
            loadAllRepos();
        }else{
            detailedViewRepos();
        }
        return null;
    }

    private void detailedViewRepos() {
        Timber.i("Retrieve detailed view");
        HttpsURLConnection httpsURLConnection;
        try{
            StringBuilder destination = new StringBuilder();
            destination.append("https://api.github.com/repos/").append(author).append("/").append(address).append("/contents");
            if (!path.equals("")){
                destination.append("/").append(path);
            }
            Timber.i("destination: %s" ,destination);
            httpsURLConnection = initializeConnection(destination.toString());
            httpsURLConnection.getErrorStream();

            if(httpsURLConnection.getResponseCode() == 200){
                String json = returnRawJson(httpsURLConnection.getInputStream());
                Timber.i("Json : %s", json);
                Moshi moshi = new Moshi.Builder().build();
                Type listSingleRepos = Types.newParameterizedType(List.class, SingleRepos.class);
                JsonAdapter<List<SingleRepos>> jsonAdapter = moshi.adapter(listSingleRepos);

                List<SingleRepos> singleRepos = jsonAdapter.fromJson(json);

                if (singleRepos != null) {
                    accessGitHubInterface.retrievedRepository(singleRepos);
                }

                httpsURLConnection.disconnect();
            }else{
                Timber.e("response code: %s", httpsURLConnection.getResponseCode());
                InputStream errorStream = httpsURLConnection.getErrorStream();



                accessGitHubInterface.failureToRetrieve(httpsURLConnection.getResponseCode() );
            }
        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());
        }
    }

    private void loadAllRepos() {
        Timber.i("Load all repos");
        HttpsURLConnection httpsURLConnection;
        ArrayList<Repos> repos = new ArrayList<>();
        try{
            httpsURLConnection = initializeConnection("https://api.github.com/users/" + address + "/repos");
            if(httpsURLConnection.getResponseCode() == 200){
                JsonReader jsonReader = initializeJsonReader(httpsURLConnection.getInputStream());
                repos = reportJson(jsonReader);
                jsonReader.close();
                httpsURLConnection.disconnect();
            }else{
                Timber.e("response code: %s", httpsURLConnection.getResponseCode());
                accessGitHubInterface.failureToRetrieve(httpsURLConnection.getResponseCode() );
            }
        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());
        } catch (Exception e) {
            Timber.e("Exception: %s", e.getLocalizedMessage());
        }

        for (Repos rep: repos){
            Timber.i("repos: %s, full repos: %s", rep.getRepos_name(), rep.getFull_repos_name());
        }

        accessGitHubInterface.retrievedRepository(repos);
    }

    private HttpsURLConnection initializeConnection(String destination) throws IOException {
        URL github_endpoint = new URL(destination);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) github_endpoint.openConnection();
        httpsURLConnection.setReadTimeout(10000);
        httpsURLConnection.setConnectTimeout(150000);
        return httpsURLConnection;
    }

    private String returnRawJson(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line+"\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    private JsonReader initializeJsonReader(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new JsonReader(inputStreamReader);
    }

    private ArrayList<Repos> reportJson(JsonReader jsonReader) throws Exception {
        ArrayList<Repos> repos = new ArrayList<>();
        jsonReader.setLenient(true);
        jsonReader.beginArray();

        while (jsonReader.hasNext()) {
            repos.add(reviewRepos(jsonReader));
        }
        return repos;
    }

    private Repos reviewRepos(JsonReader jsonReader) throws Exception {
        String reposName ="";
        String reposFullName = "";
        String dateCreated = "";
        boolean forkAssessed = false;
        boolean issuesAssesed = false;
        int forkedNum = 0;
        int openIssuesNum = 0;
        jsonReader.beginObject();
        while (jsonReader.hasNext()){
            final String name = jsonReader.nextName();
            Timber.i("Name: %s", name);
            switch (name) {
                case "name":
                    reposName = jsonReader.nextString();
                    break;
                case "full_name":
                    reposFullName = jsonReader.nextString();
                    break;
                case "created_at":
                    dateCreated = jsonReader.nextString();
                    break;
                case "open_issues_count":
                    issuesAssesed = true;
                    openIssuesNum = jsonReader.nextInt();
                    break;
                case "forks_count":
                    forkAssessed = true;
                    forkedNum = jsonReader.nextInt();
                    break;
                case "default_branch":
                    jsonReader.skipValue();
                    jsonReader.endObject();
                    if(reposName.equals("") | reposFullName.equals("") | dateCreated.equals("") | !issuesAssesed | !forkAssessed){
                        throw new Exception("Could not traverse json accurately");
                    }
                    return new Repos(reposName, reposFullName, dateCreated, openIssuesNum, forkedNum);
                default:
                    jsonReader.skipValue();
                    break;
            }
        }
        throw new Exception("Could not find repos name");
    }
}
