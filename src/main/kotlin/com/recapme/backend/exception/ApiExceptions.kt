package com.recapme.backend.exception

class InvalidInputException(message: String) : Exception(message)

class ExternalServiceException(message: String, cause: Throwable? = null) : Exception(message, cause)

class AuthenticationException(message: String) : Exception(message)

class ValidationException(message: String) : Exception(message)

class ServiceUnavailableException(message: String, cause: Throwable? = null) : Exception(message, cause)