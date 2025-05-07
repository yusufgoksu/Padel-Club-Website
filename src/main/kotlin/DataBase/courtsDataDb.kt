package DataBase


import interfaces.IcourtServices
import models.Court

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException


object CourtsDataDb : IcourtServices {
    // Function to add a new court
    override fun createCourt(name: String, cid: Int): String {
        val query = "INSERT INTO courts (name, cid) VALUES (?, ?) RETURNING crid;"
        val connection: Connection = Database.getConnection()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, name)
            preparedStatement.setInt(2, cid)
            val resultSet: ResultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return resultSet.getString("crid")
            }
            throw SQLException("Failed to create court")
        } finally {
            connection.close()
        }
    }

    // Function to get court details by CRID
    override fun getCourt(crid: Int): Court? {
        val query = "SELECT crid, name, cid FROM courts WHERE crid = ?;"
        val connection: Connection = Database.getConnection()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, crid)
            val resultSet: ResultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return Court(
                    clubId = resultSet.getInt("crid").toString(),
                    name = resultSet.getString("name"),
                    courtID = resultSet.getInt("cid").toString()
                )
            }
            return null
        } finally {
            connection.close()
        }
    }

    // Function to get all courts by club ID
    override fun getCourtsByClub(cid: Int): List<Court> {
        val query = "SELECT crid, name, cid FROM courts WHERE cid = ?;"
        val connection: Connection = Database.getConnection()
        val courts = mutableListOf<Court>()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, cid)
            val resultSet: ResultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                courts.add(
                    Court(
                        courtID = resultSet.getInt("crid").toString(),
                        name = resultSet.getString("name"),
                        clubId = resultSet.getInt("cid").toString()
                    )
                )
            }
            return courts
        } finally {
            connection.close()
        }
    }
}
