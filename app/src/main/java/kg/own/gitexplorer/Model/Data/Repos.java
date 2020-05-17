package kg.own.gitexplorer.Model.Data;

public class Repos {

    private final String repos_name, full_repos_name, date_created;
    private final int openIssueNum, forkNum;

    public Repos(String reposName, String reposFullName, String dateCreated, int openIssuesNum, int forkedNum) {
        this.repos_name = reposName;
        this.full_repos_name = reposFullName;
        this.date_created = dateCreated;
        this.openIssueNum = openIssuesNum;
        this.forkNum = forkedNum;
    }

    public String getRepos_name() {
        return repos_name;
    }

    public String getFull_repos_name() {
        return full_repos_name;
    }

    public String getDate_created() {
        return date_created;
    }

    public int getOpenIssueNum() { return openIssueNum; }

    public int getForkNum() { return forkNum; }
}
