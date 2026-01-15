package com.example.demo.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class User(
    @field:Column(nullable = false, updatable = false) val id: String,
)