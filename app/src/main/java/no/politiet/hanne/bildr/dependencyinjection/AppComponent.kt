package no.politiet.hanne.bildr.dependencyinjection

import no.politiet.hanne.bildr.cache.BildeCache
import no.politiet.hanne.bildr.repository.BildeRepository
import no.politiet.hanne.bildr.repository.LoginRepository

fun repository() : AppComponent = AppModule
interface AppComponent {
    val bildeCache : BildeCache
    val bildeRepository : BildeRepository
    val loginRepository : LoginRepository
}