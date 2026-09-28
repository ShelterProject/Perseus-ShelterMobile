package id.my.shelter.app.domain.model

enum class Gender { MALE, FEMALE }

data class User(
    val uid: String,
    val email: String,
    val fullName: String,
    val gender: Gender? = null,
    val address: String? = null,
)
