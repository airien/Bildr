package no.politiet.hanne.bildr.dependencyinjection

import no.politiet.hanne.bildr.cache.BildeCache
import no.politiet.hanne.bildr.repository.BildeRepository
import no.politiet.hanne.bildr.repository.LoginRepository

object AppModule : AppComponent {
    override val bildeCache: BildeCache  by lazy { BildeCache() }
    override val bildeRepository: BildeRepository by lazy { BildeRepository(bildeCache) }
    override val loginRepository: LoginRepository by lazy { LoginRepository() }
}