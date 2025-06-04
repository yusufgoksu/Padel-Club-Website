package data.database

import models.Court
import interfaces.IcourtServices
import java.sql.SQLException

object CourtsDataDb : IcourtServices {

    override fun createCourt(courtId: Int, name: String, clubId: Int): Int {
        val sql = """
        INSERT INTO courts (courtId, name, clubId)
        VALUES (?, ?, ?);
    """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, courtId)     // Manuel ID gönderiliyor
                    stmt.setString(2, name)
                    stmt.setInt(3, clubId)
                    val rowsInserted = stmt.executeUpdate()
                    if (rowsInserted == 0) throw SQLException("No rows inserted")
                    courtId  // Manuel verdiğin ID'yi döndür
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error creating court: ${e.message}", e)
        }
    }

    override fun getCourt(courtId: Int): Court? {
        val sql = """
            SELECT courtId, name, clubId
            FROM courts
            WHERE courtId = ?;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, courtId)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) Court(
                            courtID = rs.getInt("courtId"),
                            name    = rs.getString("name"),
                            clubId  = rs.getInt("clubId")
                        ) else null
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching court: ${e.message}", e)
        }
    }

    override fun getCourtsByClub(clubId: Int): List<Court> {
        val sql = "SELECT courtId, name, clubId FROM courts WHERE clubId = ?;"
        val list = mutableListOf<Court>()
        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, clubId)
                    stmt.executeQuery().use { rs ->
                        while (rs.next()) {
                            list += Court(
                                courtID = rs.getInt("courtId"),
                                name    = rs.getString("name"),
                                clubId  = rs.getInt("clubId")
                            )
                        }
                    }
                }
            }
            list
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching courts: ${e.message}", e)
        }
    }
}
