package data.database

import models.Court
import interfaces.IcourtServices
import java.sql.SQLException

object CourtsDataDb : IcourtServices {

    override fun createCourt(name: String, clubId: Int): Court {
        val sql = """
        INSERT INTO courts (name, clubId)
        VALUES (?, ?)
        RETURNING courtId;
    """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, name)
                    stmt.setInt(2, clubId)

                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            val id = rs.getInt("courtId")
                            Court(courtId = id, name = name, clubId = clubId)
                        } else {
                            throw SQLException("Court ID not returned")
                        }
                    }
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
                            courtId = rs.getInt("courtId"),
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
                                courtId = rs.getInt("courtId"),
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
