package data.database

import interfaces.IcourtServices
import models.Court
import java.sql.SQLException

object CourtsDataDb : IcourtServices {

    /**
     * Yeni bir kort oluşturur ve geri dönen courtId'yi Int olarak verir
     */
    override fun createCourt(name: String, clubId: Int): Int {
        val sql = """
            INSERT INTO public.courts (name, cid)
            VALUES (?, ?)
            RETURNING crid AS courtId;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setString(1, name)
                    stmt.setInt   (2, clubId)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            rs.getInt("courtId")
                        } else {
                            throw SQLException("Failed to create court, no ID returned.")
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error creating court: ${e.message}", e)
        }
    }

    /**
     * Verilen courtId ile kort detaylarını getirir, yoksa null döner
     */
    override fun getCourt(courtId: Int): Court? {
        val sql = """
            SELECT crid AS courtId,
                   name,
                   cid  AS clubId
            FROM public.courts
            WHERE crid = ?;
        """.trimIndent()

        return try {
            Database.getConnection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    stmt.setInt(1, courtId)
                    stmt.executeQuery().use { rs ->
                        if (rs.next()) {
                            Court(
                                courtID = rs.getInt("courtId"),
                                name    = rs.getString("name"),
                                clubId  = rs.getInt("clubId")
                            )
                        } else {
                            null
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            throw RuntimeException("Error fetching court details: ${e.message}", e)
        }
    }

    /**
     * Belirli bir kulübe ait tüm kortları listeler
     */
    override fun getCourtsByClub(clubId: Int): List<Court> {
        val sql = """
            SELECT crid AS courtId,
                   name,
                   cid  AS clubId
            FROM public.courts
            WHERE cid = ?;
        """.trimIndent()

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
            throw RuntimeException("Error fetching courts by club: ${e.message}", e)
        }
    }
}
