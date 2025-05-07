package tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import services.UserServices
import storage.UsersDataMem

class UserTests {

    @BeforeEach
    fun setup() {
        // Her testten önce bellekteki kullanıcıları temizle
        UsersDataMem.users.clear()
    }

    @Test
    fun `should create user with valid data`() {
        // Geçerli verilerle kullanıcı oluştur
        val user = UserServices.addUser("John Doe", "john.doe@example.com")

        // Kullanıcının başarıyla oluşturulduğunu doğrula
        assertNotNull(user.userId)
        assertEquals("John Doe", user.name)
        assertEquals("john.doe@example.com", user.email)
    }

    @Test
    fun `should not allow duplicate email`() {
        // İlk kullanıcıyı oluştur
        UserServices.addUser("John Doe", "john.doe@example.com")

        // Aynı e-posta ile ikinci kullanıcı oluşturmaya çalış
        val exception = assertThrows<IllegalArgumentException> {
            UserServices.addUser("Jane Doe", "john.doe@example.com")
        }

        // Doğru hata mesajı verildiğini kontrol et
        assertEquals("Email already exists", exception.message)
    }

    @Test
    fun `should get user by valid ID`() {
        // Bir kullanıcı oluştur
        val user = UserServices.addUser("John Doe", "john.doe@example.com")

        // Kullanıcıyı ID ile getir
        val retrievedUser = UserServices.getUserById(user.userId)

        // Doğru kullanıcıyı getirdiğini kontrol et
        assertNotNull(retrievedUser)
        assertEquals(user.userId, retrievedUser?.userId)
        assertEquals(user.name, retrievedUser?.name)
        assertEquals(user.email, retrievedUser?.email)
    }

    @Test
    fun `should return null for invalid ID`() {
        // Geçersiz ID ile kullanıcı getirilmeye çalışılıyor
        val user = UserServices.getUserById("non-existent-id")

        // Sonucun null olduğunu kontrol et
        assertNull(user)
    }

    @Test
    fun `should list all users`() {
        // Birden fazla kullanıcı oluştur
        UserServices.addUser("User 1", "user1@example.com")
        UserServices.addUser("User 2", "user2@example.com")

        // Tüm kullanıcıları getir
        val users = UserServices.getAllUsers()

        // Liste boyutunun doğru olduğunu kontrol et
        assertEquals(2, users.size)
    }

    @Test
    fun `should get user by email`() {
        // Kullanıcı oluştur
        val user = UserServices.addUser("Alice", "alice@example.com")

        // E-posta ile kullanıcıyı getir
        val foundUser = UserServices.getUserByEmail("alice@example.com")

        // Doğru kullanıcıyı getirdiğini doğrula
        assertNotNull(foundUser)
        assertEquals(user.userId, foundUser?.userId)
    }

    @Test
    fun `should return null for invalid email`() {
        // Geçersiz e-posta ile arama
        val user = UserServices.getUserByEmail("notfound@example.com")

        // Sonucun null olduğunu kontrol et
        assertNull(user)
    }

    @Test
    fun `should generate token for valid user`() {
        // Kullanıcı oluştur
        val user = UserServices.addUser("Bob", "bob@example.com")

        // Token üret
        val tokenPair = UserServices.generateUserToken(user.userId)

        // Token'ın geçerli olduğunu kontrol et
        assertNotNull(tokenPair)
        assertEquals(user.userId, tokenPair?.second)
    }

    @Test
    fun `should return null when generating token for invalid user`() {
        // Geçersiz kullanıcı ID'si ile token üretme girişimi
        val tokenPair = UserServices.generateUserToken("invalid-user-id")

        // Sonucun null olduğunu doğrula
        assertNull(tokenPair)
    }
}
