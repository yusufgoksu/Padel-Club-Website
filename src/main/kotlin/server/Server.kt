import DataBase.RentalDataDb
import api.*
import data.database.UserDataDb
import org.http4k.server.SunHttp
import org.http4k.server.asServer
import org.http4k.routing.routes
import storage.ClubsDataMem
import storage.CourtsDataMem
import storage.RentalsDataMem
import storage.UsersDataMem

fun main() {
    // Web API'leri tanımla
    val app = routes(
        homeWebApi(),
        clubsWebApi(),
        courtsWebApi(),
        rentalsWebApi(),
        usersWebApi()
    )


    // Sunucuyu başlat
    val server = app.asServer(SunHttp(9000)).start()
    println("Server running on http://localhost:9000")

    // Test verilerini veritabanı kullanarak ekle
    println("Inserting test data into database...")

    // Kullanıcı ekleme
    val user1Id = UserDataDb.createUser("Yusuf", "yussadsaussaaf@example.com").toInt()
    val user2Id = UserDataDb.createUser("Mert", "mesardasat@example.com").toInt()
    val user3Id = UserDataDb.createUser("Ali", "aliadsaas@example.com").toInt()

    println("Users created: $user1Id, $user2Id, $user3Id")

    // Kiralama ekleme (Rental)
    val rental1 = RentalDataDb.createRental(1, 1, user1Id, "2025-03-27T14:00:00", 2)
    val rental2 = RentalDataDb.createRental(1, 2, user1Id, "2025-03-27T15:00:00", 2)
    val rental3 = RentalDataDb.createRental(1, 3, user1Id, "2025-03-27T16:00:00", 2)

    val rental4 = RentalDataDb.createRental(2, 1, user2Id, "2025-03-27T14:00:00", 2)
    val rental5 = RentalDataDb.createRental(2, 2, user2Id, "2025-03-27T15:00:00", 2)

    val rental6 = RentalDataDb.createRental(3, 1, user3Id, "2025-03-27T14:00:00", 2)

    println("Rentals created: $rental1, $rental2, $rental3, $rental4, $rental5, $rental6")


}