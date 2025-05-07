package DataBase

import interfaces.IclubServices
import models.Club


import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException


object ClubDataDb : IclubServices {

    // Function to add a new club
    override fun createClub(name: String, ownerUid: Int): String {
        val query = "INSERT INTO clubs (name, owner_uid) VALUES (?, ?) RETURNING cid;"
        val connection: Connection = Database.getConnection()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, name)
            preparedStatement.setInt(2, ownerUid)
            val resultSet: ResultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return resultSet.getString("cid")
            }
            throw SQLException("Failed to create club")
        } finally {
            connection.close()
        }
    }

    // Function to get club details by CID
    override fun getClubDetails(cid: Int): Club? {
        val query = "SELECT cid, name, owner_uid FROM clubs WHERE cid = ?;"

        val connection: Connection = Database.getConnection()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setInt(1, cid)

            val resultSet: ResultSet = preparedStatement.executeQuery()
            if (resultSet.next()) {
                return Club(
                    clubID = resultSet.getInt("cid").toString(),
                    name = resultSet.getString("name"),
                    ownerUid = resultSet.getInt("owner_uid").toString()
                )
            }
            return null
        } finally {
            connection.close()
        }
    }

    // Function to get all clubs
    override fun getAllClubs(): List<Club> {
        val query = "SELECT cid, name, owner_uid FROM clubs;"
        val connection: Connection = Database.getConnection()
        val clubs = mutableListOf<Club>()
        try {
            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            val resultSet: ResultSet = preparedStatement.executeQuery()
            while (resultSet.next()) {
                clubs.add(
                    Club(
                        clubID = resultSet.getInt("cid").toString(),
                        name = resultSet.getString("name"),
                        ownerUid = resultSet.getInt("owner_uid").toString()
                    )
                )
            }
            return clubs
        } finally {
            connection.close()
        }
    }
}
