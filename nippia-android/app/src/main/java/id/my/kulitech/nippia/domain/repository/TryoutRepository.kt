package id.my.kulitech.nippia.domain.repository

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.TryoutCatalog

interface TryoutRepository {
    suspend fun getTryoutCatalog(): AppResult<TryoutCatalog>
}
