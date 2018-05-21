package no.politiet.hanne.bildr.dependencyinjection

import no.politiet.hanne.bildr.repository.BildeRepository
import no.politiet.hanne.bildr.repository.LoginRepository

object AppModule : AppComponent {
    override val bildeRepository: BildeRepository by lazy { BildeRepository() }
    override val loginRepository: LoginRepository by lazy { LoginRepository() }
}