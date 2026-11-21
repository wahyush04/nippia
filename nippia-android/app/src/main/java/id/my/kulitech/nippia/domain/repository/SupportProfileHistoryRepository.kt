package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.BookmarkItem
import id.my.kulitech.nippia.domain.model.BookmarkOverview
import id.my.kulitech.nippia.domain.model.FaqCatalog
import id.my.kulitech.nippia.domain.model.TryoutHistoryOverview
import id.my.kulitech.nippia.domain.model.UpdateProfileInput

interface SupportProfileHistoryRepository {
    suspend fun updateProfile(input: UpdateProfileInput): AppResult<Unit>
    suspend fun deleteAccount(confirmationText: String): AppResult<Unit>
    suspend fun getTryoutHistory(): AppResult<TryoutHistoryOverview>
    suspend fun listBookmarks(itemType: String? = null, categoryCode: String? = null): AppResult<BookmarkOverview>
    suspend fun upsertBookmark(
        itemType: String,
        targetId: String,
        categoryCode: String,
        title: String,
        snippet: String,
        personalNote: String? = null
    ): AppResult<BookmarkItem>
    suspend fun deleteBookmark(bookmarkId: String): AppResult<Unit>
    suspend fun getFaqCatalog(): AppResult<FaqCatalog>
    suspend fun submitQuestionReport(questionId: String?, category: String, description: String): AppResult<String>
}
