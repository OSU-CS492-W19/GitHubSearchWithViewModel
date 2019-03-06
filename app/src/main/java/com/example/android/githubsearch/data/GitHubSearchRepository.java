package com.example.android.githubsearch.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.githubsearch.utils.GitHubUtils;

import java.util.List;

public class GitHubSearchRepository implements GitHubSearchAsyncTask.Callback {

    private MutableLiveData<List<GitHubRepo>> mSearchResults;
    private MutableLiveData<Status> mLoadingStatus;

    private String mCurrentQuery;

    public GitHubSearchRepository() {
        mSearchResults = new MutableLiveData<>();
        mSearchResults.setValue(null);

        mLoadingStatus = new MutableLiveData<>();
        mLoadingStatus.setValue(Status.SUCCESS);

        mCurrentQuery = null;
    }

    public LiveData<List<GitHubRepo>> getSearchResults() {
        return mSearchResults;
    }

    public MutableLiveData<Status> getLoadingStatus() {
        return mLoadingStatus;
    }

    public void loadSearchResults(String query) {
        if (shouldExecuteSearch(query)) {
            mCurrentQuery = query;
            mSearchResults.setValue(null);
            mLoadingStatus.setValue(Status.LOADING);
            String url = GitHubUtils.buildGitHubSearchURL(query);
            Log.d("GitHubSearchRepository", "executing search with url: " + url);
            new GitHubSearchAsyncTask(url, this).execute();
        } else {
            Log.d("GitHubSearchRepository", "using cached results");
        }
    }

    private boolean shouldExecuteSearch(String query) {
        return !TextUtils.equals(query, mCurrentQuery);
    }

    @Override
    public void onSearchFinished(List<GitHubRepo> searchResults) {
        mSearchResults.setValue(searchResults);
        if (searchResults != null) {
            mLoadingStatus.setValue(Status.SUCCESS);
        } else {
            mLoadingStatus.setValue(Status.ERROR);
        }
    }
}
