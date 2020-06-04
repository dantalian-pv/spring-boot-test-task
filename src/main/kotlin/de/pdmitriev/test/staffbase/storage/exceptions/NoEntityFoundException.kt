package de.pdmitriev.test.staffbase.storage.exceptions

class NoEntityFoundException : StorageException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}