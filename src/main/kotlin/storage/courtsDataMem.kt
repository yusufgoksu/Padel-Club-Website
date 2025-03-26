package storage

import models.*

object CourtsDataMem {
    val users = mutableMapOf<String, User>()
    val clubs = mutableMapOf<String, Club>()
    val courts = mutableMapOf<String, Court>()
    val rentals = mutableMapOf<String, Rental>()
}
