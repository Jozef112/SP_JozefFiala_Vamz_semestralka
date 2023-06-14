package com.example.semestralka_jozeffiala

/**
 * Tento objekt vracia aktuálnyDátum ktorý je dnes
 *
 */
object Date {
    // Táto funkcia vráti aktuálny deň vo formáte "deň/mesiac/rok".
    fun getCurrentDay(): String {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)
        val dayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        return "$dayOfMonth/${month + 1}/$year"
    }

}

/**
 * Objekt ktorý obsahuje hodiny a minúty do ktorých sa vloží čas ktorý sa vyberie pomocou hodín
 */
object Cas {
    var hodiny = 0
    var minuty = 0
}

/**
 * Objekt ktorý si uchováva zoznam poznámok v zozname úloh.
 */
object ToDolistPoznamky {
    val poznamkyList: MutableList<String> = mutableListOf()
}

/**
 * Objekt ktorý si uchováva listPoznámok pre konkrétny dátum v kalendári
 */
object DateData {
    val listPoznamok: MutableMap<String, MutableList<String>> = HashMap()
}
