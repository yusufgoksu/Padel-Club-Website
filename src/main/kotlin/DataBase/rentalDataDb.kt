package DataBase

import interfaces.IrentalService
import models.Rental

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object RentalDataDb : IrentalService {
    // Function to add a new rental
    override fun createRental(cid: Int, crid: Int, uid: Int, date: String, duration: Int): Int {
        val query = """
            INSERT INTO rentals (cid, crid, user_uid, date, duration)
            VALUES (?, ?, ?, ?, ?) RETURNING rid;
        """.trimIndent()
        val connection: Connection = Database.getConnection()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)

            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val localDateTime = LocalDateTime.parse(date, formatter)
            val sqlTimestamp = java.sql.Timestamp.valueOf(localDateTime)

            preparedStatement.setInt(1, cid)
            preparedStatement.setInt(2, crid)
            preparedStatement.setInt(3, uid)
            preparedStatement.setTimestamp(4, sqlTimestamp)
            preparedStatement.setInt(5, duration)
            val resultSet: ResultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return resultSet.getInt("rid")
            }
            throw SQLException("Failed to create rental")
        } finally {
            connection.close()
        }
    }

    // Function to get rental details by RID
    override fun getRentalDetails(rid: Int): Rental? {
        val query = "SELECT rid, cid, crid, user_uid, date, duration FROM rentals WHERE rid = ?;"
        val connection: Connection = Database.getConnection()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, rid)
            val resultSet: ResultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return Rental(
                    rentalID = resultSet.getInt("rentalid").toString(),
                    clubId = resultSet.getInt("cid").toString(),
                    courtId = resultSet.getInt("crid").toString(),
                    userId = resultSet.getInt("user_uid").toString(),
                    startTime = resultSet.getString("date"),
                    duration = resultSet.getInt("duration")
                )
            }
            return null
        } finally {
            connection.close()
        }
    }

    // Function to get rentals by club and court, optionally filtering by date
    override fun getRentals(cid: Int, crid: Int, date: String?): List<Rental> {
        val query = if (date != null) {
            "SELECT rid, cid, crid, user_uid, date, duration FROM rentals WHERE cid = ? AND crid = ? AND date LIKE ?;"
        } else {
            "SELECT rid, cid, crid, user_uid, date, duration FROM rentals WHERE cid = ? AND crid = ?;"
        }
        val connection: Connection = Database.getConnection()
        val rentals = mutableListOf<Rental>()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, cid)
            preparedStatement.setInt(2, crid)
            if (date != null) {
                preparedStatement.setString(3, "$date%")
            }
            val resultSet: ResultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                rentals.add(
                    Rental(
                        rentalID = resultSet.getInt("rid").toString(),
                        clubId = resultSet.getInt("cid").toString(),
                        courtId = resultSet.getInt("crid").toString(),
                        userId = resultSet.getInt("user_uid").toString(),
                        startTime = resultSet.getString("date"),
                        duration = resultSet.getInt("duration")
                    )
                )
            }
            return rentals
        } finally {
            connection.close()
        }
    }

    // Function to get rentals by user ID
    override fun getUserRentals(uid: Int): List<Rental> {
        val query = "SELECT rid, cid, crid, user_uid, date, duration FROM rentals WHERE user_uid = ?;"
        val connection: Connection = Database.getConnection()
        val rentals = mutableListOf<Rental>()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, uid)
            val resultSet: ResultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                rentals.add(
                    Rental(
                        rentalID = resultSet.getInt("rid").toString(),
                        clubId = resultSet.getInt("cid").toString(),
                        courtId = resultSet.getInt("crid").toString(),
                        userId = resultSet.getInt("user_uid").toString(),
                        startTime = resultSet.getString("date"),
                        duration = resultSet.getInt("duration")
                    )
                )
            }
            return rentals
        } finally {
            connection.close()
        }
    }

    // Function to get available rental hours
    override fun getAvailableRentalHours(cid: Int, crid: Int, date: String): List<String> {
        val bookedHoursQuery = """
            SELECT date, duration FROM rentals WHERE cid = ? AND crid = ? AND date LIKE ?;
        """.trimIndent()
        val connection: Connection = Database.getConnection()
        val bookedHours = mutableSetOf<String>()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(bookedHoursQuery)
            preparedStatement.setInt(1, cid)
            preparedStatement.setInt(2, crid)
            preparedStatement.setString(3, "$date%")
            val resultSet: ResultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                val rentalStart = LocalDateTime.parse(resultSet.getString("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                val rentalEnd = rentalStart.plusHours(resultSet.getInt("duration").toLong())
                var currentHour = rentalStart.hour
                while (currentHour < rentalEnd.hour) {
                    bookedHours.add("$date ${currentHour}:00")
                    currentHour++
                }
            }
        } finally {
            connection.close()
        }
        val allHours = (0..23).map { "$date ${it}:00" }
        return allHours - bookedHours
    }

    // Function to update rental date and duration by rental ID
    override fun updateRental(rid: Int, date: String, duration: Int): Boolean {
        val query = """
        UPDATE rentals 
        SET date = ?, duration = ? 
        WHERE rid = ?;
    """.trimIndent()

        val connection: Connection = Database.getConnection()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)

            val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
            val localDateTime = LocalDateTime.parse(date, formatter)
            val sqlTimestamp = java.sql.Timestamp.valueOf(localDateTime)

            preparedStatement.setTimestamp(1, sqlTimestamp)
            preparedStatement.setInt(2, duration)
            preparedStatement.setInt(3, rid)

            val affectedRows = preparedStatement.executeUpdate()
            return affectedRows > 0
        } finally {
            connection.close()
        }
    }

    // Function to delete rental by rental ID
    override fun deleteRental(rid: Int): Boolean {
        val query = "DELETE FROM rentals WHERE rid = ?;"
        val connection: Connection = Database.getConnection()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, rid)
            val affectedRows = preparedStatement.executeUpdate()
            return affectedRows > 0
        } finally {
            connection.close()
        }
    }
}