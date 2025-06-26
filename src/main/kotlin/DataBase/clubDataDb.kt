package data.database

import models.Club
import interfaces.IclubServices
import java.sql.SQLException

object ClubsDataDb : IclubServices {

    /**
     * Inserts a new club and returns the generated clubId.
     */
    override fun createClub(name: String, userID: Int): Club {
        val sql = """
        INSERT INTO clubs (name, userID)
        VALUES (?, ?)
        RETURNING clubID, name, userID;
    """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, name)
                    stmt.setInt(2, userID)

                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            Club(
                                clubId = rs.getInt("clubID"),
                                name = rs.getString("name"),
                                userID = rs.getInt("userID")
                            )
                        } else {
                            throw IllegalStateException("Club creation failed.")
                        }
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
                            clubId = rs.getInt("clubId"),
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
                                clubId = rs.getInt("clubId"),
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
    override fun searchClubsByName(partialName: String): List<Club> {
        println("🔍 Searching clubs with: $partialName")  // LOG 1

        val sql = """
        SELECT clubId, name, userId 
        FROM clubs 
        WHERE name ILIKE ?
    """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, "%$partialName%") // 👈 contains match
                    stmt.executeQuery().use { rs ->
                        val results = mutableListOf<Club>()
                        while (rs.next()) {
                            results += Club(
                                clubId = rs.getInt("clubId"),
                                name = rs.getString("name"),
                                userID = rs.getInt("userId")
                            )
                        }
                        println("✅ SQL result count: ${results.size}")  // LOG 2
                        results
                    }
                }
            }
        } catch (e: SQLException) {
            println("❌ SQL error: ${e.message}")
            throw RuntimeException("Error searching clubs: ${e.message}", e)
        }
    }



}
