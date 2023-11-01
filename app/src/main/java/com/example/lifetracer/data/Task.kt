package com.example.lifetracer.data

data class Task(
    private var _id: Long, // Make _id private
    val name: String,
    val taskQuality: String,
    val dateOfCreation: String,
    val regularity: Int,
    val fixed: Boolean
) {
    var id: Long
        get() = _id // Getter to access the id
        private set(value) {
            _id = value // Setter to update the id (private to restrict external updates)
        }

    // Other methods and properties for your Task class

    // Custom method to set the id
    fun updateId(newId: Long) {
        // Add any validation or constraints here if needed
        id = newId
    }
}