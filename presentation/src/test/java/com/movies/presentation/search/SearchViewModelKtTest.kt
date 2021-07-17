package com.movies.presentation.search

import com.movies.core.entities.Movie
import com.movies.core.entities.MoviesSection
import com.movies.core.entities.PaginatedBatch
import com.movies.core.searching.SearchPort
import com.movies.presentation.search.seasons.MoviesSeason
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.Test

class SearchViewModelKtTest {
    @Test
    fun `bindSearchResults() with pagedItemsResult then update searchResult`() {

        val sections = listOf(
            MoviesSection(year = "1", movies = listOf(Movie("A"), Movie("B"))),
            MoviesSection(year = "2", movies = listOf(Movie("C"), Movie("D")))
        )

        val testScheduler = TestScheduler()
        val adapter = SearchSeasonsAdapter(testScheduler)

        adapter.searchResults
            .map { it.size }
            .test()
            .also { adapter.bindSearchResults() }
            .also { adapter.pagedItemsResult.onNext(PaginatedBatch(items = sections)) }
            .also { testScheduler.triggerActions() }
            .assertValues(2)
    }

    @Test
    fun `bindSearchResults() with null pagedItemsResult then update searchResult with empty list`() {

        val testScheduler = TestScheduler()
        val adapter = SearchSeasonsAdapter(testScheduler)

        adapter.searchResults
            .test()
            .also { adapter.bindSearchResults() }
            .also { adapter.pagedItemsResult.onNext(PaginatedBatch(items = null)) }
            .also { testScheduler.triggerActions() }
            .assertValues(listOf())
    }
}

class SearchSeasonsAdapter(testScheduler: TestScheduler) : SearchSeasonsPort,
    SearchPort by SearchAdapter(testScheduler) {
    override val searchResults = BehaviorSubject.create<List<MoviesSeason>>()
}

