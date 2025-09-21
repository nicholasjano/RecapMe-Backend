package com.recapme.backend.service

import com.recapme.backend.exception.InvalidInputException
import org.springframework.stereotype.Service

@Service
class InputSanitizationService {

    companion object {
        private const val MAX_INPUT_LENGTH = 4000000 // 4 million characters (conservative limit)
        private const val MAX_LINES = 50000 // Proportionally set

        // Suspicious patterns that might indicate prompt injection attempts
        private val SUSPICIOUS_PATTERNS = listOf(
            // System instruction overrides
            Regex("(?i)ignore\\s+(previous|all)\\s+(instructions?|prompts?|commands?)", RegexOption.IGNORE_CASE),
            Regex("(?i)forget\\s+(everything|all|previous)", RegexOption.IGNORE_CASE),
            Regex("(?i)new\\s+(instructions?|prompts?|commands?)", RegexOption.IGNORE_CASE),
            Regex("(?i)system\\s*:?\\s*(override|reset|clear)", RegexOption.IGNORE_CASE),

            // Role playing attempts
            Regex("(?i)(pretend|act\\s+as|you\\s+are\\s+now)\\s+(a|an|the)\\s+", RegexOption.IGNORE_CASE),
            Regex("(?i)role\\s*:?\\s*(admin|root|system|developer)", RegexOption.IGNORE_CASE),

            // Output manipulation
            Regex("(?i)(print|output|return|respond\\s+with)\\s+\"", RegexOption.IGNORE_CASE),
            Regex("(?i)format\\s+(your\\s+)?response\\s+(as|in)", RegexOption.IGNORE_CASE),

            // Encoding/Escaping attempts
            Regex("\\\\[nr]|\\\\x[0-9a-fA-F]{2}|\\\\u[0-9a-fA-F]{4}", RegexOption.IGNORE_CASE),

            // Multiple system-like separators
            Regex("={3,}|#{3,}|-{3,}|\\*{3,}", RegexOption.IGNORE_CASE)
        )

        // Patterns for excessive special characters that might disrupt processing
        private val EXCESSIVE_SPECIAL_CHARS = Regex("[!@#$%^&*(){}\\[\\]|\\\\:;\"'<>,.?/~`+=_-]{20,}")

        // Pattern for detecting base64-like encoded content that might hide malicious instructions
        private val BASE64_LIKE = Regex("[A-Za-z0-9+/]{50,}={0,2}")
    }

    fun sanitizeConversationInput(conversation: String): String {
        // Basic length validation
        if (conversation.length > MAX_INPUT_LENGTH) {
            throw InvalidInputException("Conversation text exceeds maximum allowed length of $MAX_INPUT_LENGTH characters")
        }

        // Check line count
        val lines = conversation.lines()
        if (lines.size > MAX_LINES) {
            throw InvalidInputException("Conversation exceeds maximum allowed lines of $MAX_LINES")
        }

        // Check for suspicious patterns
        for (pattern in SUSPICIOUS_PATTERNS) {
            if (pattern.containsMatchIn(conversation)) {
                throw InvalidInputException("Input contains potentially malicious content")
            }
        }

        // Check for excessive special characters
        if (EXCESSIVE_SPECIAL_CHARS.containsMatchIn(conversation)) {
            throw InvalidInputException("Input contains excessive special characters")
        }

        // Check for base64-like content that might hide instructions
        val base64Matches = BASE64_LIKE.findAll(conversation).toList()
        if (base64Matches.size > 3) { // Allow some base64 content but not excessive amounts
            throw InvalidInputException("Input contains suspicious encoded content")
        }

        // Sanitize the input by removing potential control characters
        return conversation
            .replace(Regex("\\p{Cntrl}"), "") // Remove control characters
            .replace(Regex("\\s+"), " ") // Normalize whitespace
            .trim()
    }

    fun validateStyle(style: String): String {
        val allowedStyles = setOf("concise", "detailed", "bullet", "casual", "formal")
        val normalizedStyle = style.lowercase().trim()

        if (normalizedStyle !in allowedStyles) {
            throw InvalidInputException("Invalid style. Allowed styles: ${allowedStyles.joinToString(", ")}")
        }

        return normalizedStyle
    }

}