import java.sql.Connection
import java.sql.DriverManager

object Database {
    private const val URL  = "jdbc:postgresql://localhost:5432/postgres?connectTimeout=10&sslmode=prefer"
    private const val USER = "postgres"
    private const val PASS = "Hg268644"

    init {
        resetDatabase()
    }

    private fun resetDatabase() {
        DriverManager.getConnection(URL, USER, PASS).use { conn ->
            conn.autoCommit = false
            conn.createStatement().use { stmt ->
                // 1) Şemayı sıfırla
                stmt.execute("DROP SCHEMA IF EXISTS public CASCADE;")
                stmt.execute("CREATE SCHEMA public;")

                // 2) users tablosu ve sequence
                stmt.execute("""
                    CREATE SEQUENCE IF NOT EXISTS public.users_userId_seq START 1 INCREMENT 1 CACHE 1;
                """.trimIndent())
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS public.users (
                        userId INTEGER NOT NULL DEFAULT nextval('public.users_userId_seq'),
                        name   VARCHAR(100) NOT NULL,
                        email  VARCHAR(255) NOT NULL UNIQUE,
                        PRIMARY KEY (userId)
                    );
                """.trimIndent())
                stmt.execute("ALTER SEQUENCE public.users_userId_seq OWNED BY public.users.userId;")

                // 3) clubs tablosu ve sequence
                stmt.execute("""
                    CREATE SEQUENCE IF NOT EXISTS public.clubs_clubId_seq START 1 INCREMENT 1 CACHE 1;
                """.trimIndent())
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS public.clubs (
                        clubId  INTEGER NOT NULL DEFAULT nextval('public.clubs_clubId_seq'),
                        name    VARCHAR(100) NOT NULL,
                        userId  INTEGER NOT NULL,
                        PRIMARY KEY (clubId),
                        FOREIGN KEY (userId) REFERENCES public.users(userId) ON DELETE CASCADE
                    );
                """.trimIndent())
                stmt.execute("ALTER SEQUENCE public.clubs_clubId_seq OWNED BY public.clubs.clubId;")

                // 4) courts tablosu ve sequence
                stmt.execute("""
                    CREATE SEQUENCE IF NOT EXISTS public.courts_courtId_seq START 1 INCREMENT 1 CACHE 1;
                """.trimIndent())
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS public.courts (
                        courtId INTEGER NOT NULL DEFAULT nextval('public.courts_courtId_seq'),
                        name    VARCHAR(100) NOT NULL,
                        clubId  INTEGER NOT NULL,
                        PRIMARY KEY (courtId),
                        FOREIGN KEY (clubId) REFERENCES public.clubs(clubId) ON DELETE CASCADE
                    );
                """.trimIndent())
                stmt.execute("ALTER SEQUENCE public.courts_courtId_seq OWNED BY public.courts.courtId;")

                // 5) rentals tablosu ve sequence
                stmt.execute("""
                    CREATE SEQUENCE IF NOT EXISTS public.rentals_rentalId_seq START 1 INCREMENT 1 CACHE 1;
                """.trimIndent())
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS public.rentals (
                        rentalId INTEGER NOT NULL DEFAULT nextval('public.rentals_rentalId_seq'),
                        clubId   INTEGER NOT NULL,
                        courtId  INTEGER NOT NULL,
                        userId   INTEGER NOT NULL,
                        date     TIMESTAMP NOT NULL,
                        duration INTEGER NOT NULL,
                        PRIMARY KEY (rentalId),
                        FOREIGN KEY (clubId)  REFERENCES public.clubs(clubId)  ON DELETE CASCADE,
                        FOREIGN KEY (courtId) REFERENCES public.courts(courtId) ON DELETE CASCADE,
                        FOREIGN KEY (userId)  REFERENCES public.users(userId)  ON DELETE CASCADE
                    );
                """.trimIndent())
                stmt.execute("ALTER SEQUENCE public.rentals_rentalId_seq OWNED BY public.rentals.rentalId;")
            }
            conn.commit()
        }
    }

    fun getConnection(): Connection =
        DriverManager.getConnection(URL, USER, PASS)
}
