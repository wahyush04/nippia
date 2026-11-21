package id.my.kulitech.nippia.domain.usecase

import id.my.kulitech.nippia.core.common.AppResult
import id.my.kulitech.nippia.domain.model.TryoutCatalog
import id.my.kulitech.nippia.domain.repository.TryoutRepository
import javax.inject.Inject

class GetTryoutCatalogUseCase @Inject constructor(
    private val tryoutRepository: TryoutRepository
) {
    suspend operator fun invoke(): AppResult<TryoutCatalog> =
        tryoutRepository.getTryoutCatalog()
}
