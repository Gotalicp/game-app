package com.example.game_app.domain.firebase

import kotlin.random.Random

class GenerateCode(private val className: String, private val uid: String) {
    fun generateCode(): String {
        var uidRandomLetters = ""
        uid.filter { it.isLetter() }.let { uid ->
            uidRandomLetters = Random.nextInt(uid.length).let {
                var index2 = Random.nextInt(uid.length)
                while (index2 == it) {
                    index2 = Random.nextInt(uid.length)
                }
                uid[it].toString() + uid[index2]
            }
        }
        return "${className.take(2)}${Random.nextInt(0, 9)}${Random.nextInt(0, 9)}$uidRandomLetters"
    }
}