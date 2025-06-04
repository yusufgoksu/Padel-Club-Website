package data.database

import models.Club
import interfaces.IclubServices
import java.sql.SQLException

object ClubsDataDb : IclubServices {

    /**
     * Inserts a new club and returns the generated clubId.
     */
    override fun createClub(clubId: Int, name: String, userID: Int): Int {
        val sql = """
        INSERT INTO clubs (clubId, name, userId)
        VALUES (?, ?, ?);
    """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, clubId)     // Manuel ID gönderiliyor
                    stmt.setString(2, name)
                    stmt.setInt(3, userID)
                    val rowsInserted = stmt.executeUpdate()
                    if (rowsInserted == 0) throw SQLException("No rows inserted")
                    clubId  // Manuel verdiğin ID'yi döndür
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error creating club: ${e.message}", e)
        }
    }


    /**
     * Fetch a single club by its integer ID.
     */
    override fun getClubDetails(clubId: Int): Club? {
        val sql = """
            SELECT clubId, name, userId
            FROM clubs
            WHERE clubId = ?;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, clubId)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) Club(
                            clubID = rs.getInt("clubId"),
                            name   = rs.getString("name"),
                            userID = rs.getInt("userId")
                        ) else null
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching club details: ${e.message}", e)
        }
    }

    /**
     * List all clubs.
     */
    override fun getAllClubs(): List<Club> {
        val sql = "SELECT clubId, name, userId FROM clubs;"
        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.executeQuery().use { rs ->
                        val list = mutableListOf<Club>()
                        while (rs.next()) {
                            list += Club(
                                clubID = rs.getInt("clubId"),
                                name   = rs.getString("name"),
                                userID = rs.getInt("userId")
                            )
                        }
                        list
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error listing clubs: ${e.message}", e)
        }
    }
}
