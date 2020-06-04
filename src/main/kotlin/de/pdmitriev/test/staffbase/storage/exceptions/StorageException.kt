package de.pdmitriev.test.staffbase.storage.exceptions

open class StorageException: RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
}