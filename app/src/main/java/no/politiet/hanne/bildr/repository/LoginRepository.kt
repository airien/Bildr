package no.politiet.hanne.bildr.repository

class LoginRepository {
    private var isLoggedIn = false
    fun login(brukernavn: String?, passord: String?) : Boolean {
        if(brukernavn != null && passord != null) {
            isLoggedIn = true
            return true
        }
        return false
        //TODO: implementer login
    }

    fun erLoggetInn() : Boolean { return isLoggedIn }
}