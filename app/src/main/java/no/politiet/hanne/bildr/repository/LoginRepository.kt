package no.politiet.hanne.bildr.repository

class LoginRepository {
    private var isLoggedIn = false

    fun login(brukernavn: String, passord: String) : Boolean {
        isLoggedIn = true
        return true;
        //TODO: implementer login
    }

    fun erLoggetInn() : Boolean { return isLoggedIn }
}