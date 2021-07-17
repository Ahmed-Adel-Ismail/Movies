package com.movies.presentation.search

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import com.movies.presentation.R

internal fun SearchFragment.inflateSearchView(inflater: MenuInflater, menu: Menu) {
    inflater.inflate(R.menu.options_menu, menu)
    val searchViewMenuItem: MenuItem = menu.findItem(R.id.search)
    val searchView = searchViewMenuItem.actionView as SearchView
    searchView.queryHint = resources.getString(R.string.search_hint)
    searchView.setQuery(viewModel.searchTerm.value, false)
    searchView.clearFocus()
    searchView.setOnQueryTextListener(onQueryTextListener())
}

internal fun SearchFragment.onQueryTextListener() = object : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?) = false
    override fun onQueryTextChange(newText: String): Boolean {
        viewModel.searchTerm.onNext(newText)
        return false
    }
}
