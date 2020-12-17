# RedditReader

An Android app that let's you read entries from the top Reddit posts.

## Overview of features

    - Pull to Refresh
    - Pagination support
    - Indicator of unread/read post (updated status, after post it’s selected)
    - Dismiss Post Button (remove the cell from list. Animations required)
    - Dismiss All Button (remove all posts. Animations required)
    - Support split layout (left side: all posts / right side: detail post)

## Main technical features

1. MVVM
2. Dependency Injection using Hilt
3. Coroutines and Flow
4. Android Architecture Components
5. Unit tests
6. Retrofit

## Next steps

1. All the storage is handled in memory due to time constraints. We could add a persistent data source like Room. To do this, we can add a new implementation of the LocalSourceProvider interface.
2. UI could be largely improved.
3. Code coverage can be improved too. We added lots of tests as a guideline, but our EntryListViewModel has a lot more cases that can be tested. There is a lot of logic to be tested too in our LocalSourceProviderImpl and RedditRepository.
4. Improve error handling
5. Save images to gallery.
