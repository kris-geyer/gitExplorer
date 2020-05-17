package kg.own.gitexplorer.Model.Repository;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

import timber.log.Timber;

public class AccessGitHubContent extends AsyncTask<Void, Void, Void> {

    private final String author, repositoryToLoad, readMeFileName;
    private final AccessGitHubContentInterface accessGitHubContentInterface;

    AccessGitHubContent(String author, String repositoryToLoad, String readMeFileName, AccessGitHubContentInterface accessGitHubContentInterface) {
        this.author = author;
        this.repositoryToLoad = repositoryToLoad;
        this.readMeFileName = readMeFileName;
        this.accessGitHubContentInterface = accessGitHubContentInterface;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        retrieveGitHubContent();
        return null;
    }

    private void retrieveGitHubContent() {
        Timber.i("Retrieve detailed view");
        HttpsURLConnection httpsURLConnection;
        try{
            StringBuilder destination = new StringBuilder();
            destination.append("https://raw.githubusercontent.com/").append(author).append("/").append(repositoryToLoad).append("/master/").append(readMeFileName);

            Timber.i("destination: %s" ,destination);
            httpsURLConnection = initializeConnection(destination.toString());

            if(httpsURLConnection.getResponseCode() == 200){
                String json = returnRawJson(httpsURLConnection.getInputStream());

                accessGitHubContentInterface.resultGenerated(json);

                httpsURLConnection.disconnect();
            }else{
                Timber.e("response code: %s", httpsURLConnection.getResponseCode());
                accessGitHubContentInterface.encounteredError("Error: " + httpsURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Timber.e("Error: %s", e.getLocalizedMessage());
        }
    }

    private HttpsURLConnection initializeConnection(String destination) throws IOException {
        URL github_endpoint = new URL(destination);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) github_endpoint.openConnection();
        httpsURLConnection.setRequestProperty("User-Agent", "github-explorer-app-v0.1");
        httpsURLConnection.setReadTimeout(10000);
        httpsURLConnection.setConnectTimeout(150000);
        return httpsURLConnection;
    }

    private String returnRawJson(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

}
