package io.cucumber.cienvironment;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.cucumber.cienvironment.DetectCiEnvironment.removeUserInfoFromUrl;
import static io.cucumber.cienvironment.VariableExpression.evaluate;

final class CiEnvironmentImpl implements CiEnvironment {
    public String name;
    public String url;
    public String buildNumber;
    public Git git;

    CiEnvironmentImpl() {
    }

    CiEnvironmentImpl(String name, String url, String buildNumber, Git git) {
        this.name = name;
        this.url = url;
        this.buildNumber = buildNumber;
        this.git = git;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getBuildNumber() {
        return buildNumber;
    }

    @Override
    public CiEnvironment.Git getGit() {
        return git;
    }

    CiEnvironment detect(Map<String, String> env) {
        String url = evaluate(getUrl(), env);
        if (url == null) return null;

        return new CiEnvironmentImpl(
                name,
                url,
                evaluate(getBuildNumber(), env),
                detectGit(env)
        );
    }

    private Git detectGit(Map<String, String> env) {
        String revision = evaluate(git.revision, env);
        if (revision == null) return null;

        String remote = evaluate(git.remote, env);
        if (remote == null) return null;

        return new Git(
            removeUserInfoFromUrl(remote),
            revision,
            evaluate(git.branch, env),
            evaluate(git.tag, env)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CiEnvironmentImpl that = (CiEnvironmentImpl) o;
        return Objects.equals(name, that.name) && Objects.equals(url, that.url) && Objects.equals(buildNumber, that.buildNumber) && Objects.equals(git, that.git);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, buildNumber, git);
    }

    @Override
    public String toString() {
        return "CiEnvironmentImpl{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", buildNumber='" + buildNumber + '\'' +
                ", git=" + git +
                '}';
    }

    final static class Git implements CiEnvironment.Git {
        public String remote;
        public String revision;
        public String branch;
        public String tag;

        Git() {
        }

        Git(String remote, String revision, String branch, String tag) {
            this.remote = remote;
            this.revision = revision;
            this.branch = branch;
            this.tag = tag;
        }

        @Override
        public String getRemote() {
            return remote;
        }

        @Override
        public String getRevision() {
            return revision;
        }

        @Override
        public String getBranch() {
            return branch;
        }

        @Override
        public String getTag() {
            return tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Git git = (Git) o;
            return Objects.equals(remote, git.remote) && Objects.equals(revision, git.revision) && Objects.equals(branch, git.branch) && Objects.equals(tag, git.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(remote, revision, branch, tag);
        }

        @Override
        public String toString() {
            return "Git{" +
                    "remote='" + remote + '\'' +
                    ", revision='" + revision + '\'' +
                    ", branch='" + branch + '\'' +
                    ", tag='" + tag + '\'' +
                    '}';
        }
    }
}