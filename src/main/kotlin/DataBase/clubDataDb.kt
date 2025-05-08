package data.database

import models.Club
import interfaces.IclubServices
import java.sql.SQLException

object ClubsDataDb : IclubServices {

    /**
     * Inserts a new club and returns the generated integer club_id.
     */
    override fun createClub(name: String, userID: Int): Int {
        val sql = """
            INSERT INTO clubs (name, userId)
            VALUES (?, ?)
            RETURNING clubId;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, name)
                    stmt.setInt   (2, userID)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) rs.getInt("club_Id")
                        else throw SQLException("Club creation failed, no ID returned.")
                    }
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
            SELECT club_Id, name, userId
            FROM clubs
            WHERE club_Id = ?;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, clubId)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) Club(
                            clubID = rs.getInt("club_Id"),
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
        val sql = "SELECT club_id, name, user_id FROM clubs;"
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
