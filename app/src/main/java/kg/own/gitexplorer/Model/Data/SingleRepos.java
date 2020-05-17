package kg.own.gitexplorer.Model.Data;

import java.io.Serializable;

public class SingleRepos implements Serializable {
    private String name;
    private String path;
    private String sha;
    private float size;
    private String url;
    private String html_url;
    private String git_url;
    private String download_url;
    private String type;
    _links _linksObject;


    // Getter Methods

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getSha() {
        return sha;
    }

    public float getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    public String getHtml_url() {
        return html_url;
    }

    public String getGit_url() {
        return git_url;
    }

    public String getDownload_url() {
        return download_url;
    }

    public String getType() {
        return type;
    }

    public _links get_links() {
        return _linksObject;
    }


    public static class _links {
        private String self;
        private String git;
        private String html;


        // Getter Methods

        public String getSelf() {
            return self;
        }

        public String getGit() {
            return git;
        }

        public String getHtml() {
            return html;
        }
    }


}
