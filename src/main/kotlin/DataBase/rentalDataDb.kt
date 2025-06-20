package data.database

import interfaces.IrentalService
import models.Rental
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RentalDataDb : IrentalService {

    override fun createRental(
        clubId: Int,
        courtId: Int,
        userId: Int,
        date: String,
        duration: Int
    ): Rental {
        val sql = """
            INSERT INTO rentals (clubId, courtId, userId, date, duration)
            VALUES (?, ?, ?, ?, ?)
            RETURNING rentalId;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    val ts = java.sql.Timestamp.valueOf(
                        LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                    stmt.setInt(1, clubId)
                    stmt.setInt(2, courtId)
                    stmt.setInt(3, userId)
                    stmt.setTimestamp(4, ts)
                    stmt.setInt(5, duration)

                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            val rentalId = rs.getInt("rentalId")
                            Rental(rentalId, clubId, courtId, userId, date, duration)
                        } else {
                            throw SQLException("Failed to create rental.")
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error creating rental: ${e.message}", e)
        }
    }

    override fun getRentalDetails(rentalId: Int): Rental? {
        val sql = """
            SELECT rentalId, clubId, courtId, userId, date, duration
            FROM rentals
            WHERE rentalId = ?;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, rentalId)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            Rental(
                                rs.getInt("rentalId"),
                                rs.getInt("clubId"),
                                rs.getInt("courtId"),
                                rs.getInt("userId"),
                                rs.getTimestamp("date").toLocalDateTime()
                                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                rs.getInt("duration")
                            )
                        } else null
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching rental details: ${e.message}", e)
        }
    }

    override fun getRentals(clubId: Int, courtId: Int, date: String?): List<Rental> {
        val baseSql = """
            SELECT rentalId, clubId, courtId, userId, date, duration
            FROM rentals
            WHERE clubId = ? AND courtId = ?
        """.trimIndent()
        val sql = if (date != null) "$baseSql AND date::text LIKE ?;" else "$baseSql;"

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, clubId)
                    stmt.setInt(2, courtId)
                    if (date != null) stmt.setString(3, "$date%")

                    stmt.executeQuery().use { rs ->
                        val list = mutableListOf<Rental>()
                        while (rs.next()) {
                            list += Rental(
                                rs.getInt("rentalId"),
                                rs.getInt("clubId"),
                                rs.getInt("courtId"),
                                rs.getInt("userId"),
                                rs.getTimestamp("date").toLocalDateTime()
                                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                rs.getInt("duration")
                            )
                        }
                        list
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching rentals: ${e.message}", e)
        }
    }

    override fun getUserRentals(userId: Int): List<Rental> {
        val sql = """
            SELECT rentalId, clubId, courtId, userId, date, duration
            FROM rentals
            WHERE userId = ?;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, userId)
                    stmt.executeQuery().use { rs ->
                        val list = mutableListOf<Rental>()
                        while (rs.next()) {
                            list += Rental(
                                rs.getInt("rentalId"),
                                rs.getInt("clubId"),
                                rs.getInt("courtId"),
                                rs.getInt("userId"),
                                rs.getTimestamp("date").toLocalDateTime()
                                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                rs.getInt("duration")
                            )
                        }
                        list
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching user rentals: ${e.message}", e)
        }
    }

    override fun getAvailableRentalHours(clubId: Int, courtId: Int, date: String): List<String> {
        val sql = """
            SELECT date, duration
            FROM rentals
            WHERE clubId = ? AND courtId = ? AND date::text LIKE ?;
        """.trimIndent()

        val booked = mutableSetOf<String>()
        try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, clubId)
                    stmt.setInt(2, courtId)
                    stmt.setString(3, "$date%")
                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            val start = rs.getTimestamp("date").toLocalDateTime()
                            val end = start.plusHours(rs.getInt("duration").toLong())
                            var h = start.hour
                            while (h < end.hour) {
                                booked += "$date ${h}:00"
                                h++
                            }
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error computing booked hours: ${e.message}", e)
        }

        return (8..17).map { "$date ${it}:00" } - booked
    }

    override fun updateRental(rentalId: Int, date: String, duration: Int): Boolean {
        val sql = """
            UPDATE rentals
            SET date = ?, duration = ?
            WHERE rentalId = ?;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    val ts = java.sql.Timestamp.valueOf(
                        LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    )
                    stmt.setTimestamp(1, ts)
                    stmt.setInt(2, duration)
                    stmt.setInt(3, rentalId)
                    stmt.executeUpdate() > 0
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error updating rental: ${e.message}", e)
        }
    }

    override fun deleteRental(rentalId: Int): Boolean {
        val sql = """
            DELETE FROM rentals
            WHERE rentalId = ?;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, rentalId)
                    stmt.executeUpdate() > 0
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error deleting rental: ${e.message}", e)
        }
    }

    fun getUsersWithRentalCountsByCourt(courtID: Int): List<Pair<Int, Int>> {
        val sql = """
            SELECT userId, COUNT(*) as rental_count
            FROM rentals
            WHERE courtId = ?
            GROUP BY userId;
        """.trimIndent()

        val list = mutableListOf<Pair<Int, Int>>()

        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, courtID)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        list += Pair(
                            rs.getInt("userId"),
                            rs.getInt("rental_count")
                        )
                    }
                }
            }
        }
        return list
    }

    fun getCourtsWithRentalCountsByUser(userId: Int): List<Pair<Int, Int>> {
        val sql = """
            SELECT courtId, COUNT(*) as rental_count
            FROM rentals
            WHERE userId = ?
            GROUP BY courtId;
        """.trimIndent()

        val list = mutableListOf<Pair<Int, Int>>()

        Database.getConnection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setInt(1, userId)
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        list += Pair(
                            rs.getInt("courtId"),
                            rs.getInt("rental_count")
                        )
                    }
                }
            }
        }
        return list
    }

}
