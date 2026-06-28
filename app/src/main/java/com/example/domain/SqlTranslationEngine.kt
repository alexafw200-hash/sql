package com.example.domain

object SqlTranslationEngine {
    
    fun translate(query: String, fromEngine: String, toEngine: String): String {
        var translated = query
        
        if (fromEngine == "SQLite" && toEngine == "MySQL") {
            translated = translated.replace("AUTOINCREMENT", "AUTO_INCREMENT", ignoreCase = true)
            // Add more SQLite -> MySQL replacements
        } else if (fromEngine == "MySQL" && toEngine == "SQLite") {
            translated = translated.replace("AUTO_INCREMENT", "AUTOINCREMENT", ignoreCase = true)
        } else if (toEngine == "PostgreSQL") {
            translated = translated.replace("AUTOINCREMENT", "SERIAL", ignoreCase = true)
            translated = translated.replace("AUTO_INCREMENT", "SERIAL", ignoreCase = true)
        }
        
        return translated
    }
}
