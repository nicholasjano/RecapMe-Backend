package com.recapme.backend.service

interface RateLimitingService {
    fun checkRateLimit(ipAddress: String)
    fun getRemainingRequests(ipAddress: String): Int
    fun getResetTime(ipAddress: String): Long
}