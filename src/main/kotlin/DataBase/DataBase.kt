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
                    CREATE SEQUENCE IF NOT EXISTS public.users_user_id_seq
                      START WITH 1 INCREMENT BY 1 CACHE 1
                """.trimIndent())
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS public.users (
                      user_id INTEGER NOT NULL DEFAULT nextval('public.users_user_id_seq'),
                      name    VARCHAR(100)   NOT NULL,
                      email   VARCHAR(255)   NOT NULL UNIQUE,
                      PRIMARY KEY (user_id)
                    )
                """.trimIndent())
                stmt.execute("ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;")

                // 3) clubs tablosu ve sequence
                stmt.execute("""
                    CREATE SEQUENCE IF NOT EXISTS public.clubs_club_id_seq
                      START WITH 1 INCREMENT BY 1 CACHE 1
                """.trimIndent())
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS public.clubs (
                      club_id  INTEGER NOT NULL DEFAULT nextval('public.clubs_club_id_seq'),
                      name     VARCHAR(100) NOT NULL,
                      owner_id INTEGER        NOT NULL,
                      PRIMARY KEY (club_id),
                      FOREIGN KEY (owner_id) REFERENCES public.users(user_id) ON DELETE CASCADE
                    )
                """.trimIndent())
                stmt.execute("ALTER SEQUENCE public.clubs_club_id_seq OWNED BY public.clubs.club_id;")

                // 4) courts tablosu ve sequence
                stmt.execute("""
                    CREATE SEQUENCE IF NOT EXISTS public.courts_crid_seq
                      START WITH 1 INCREMENT BY 1 CACHE 1
                """.trimIndent())
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS public.courts (
                      crid  INTEGER NOT NULL DEFAULT nextval('public.courts_crid_seq'),
                      name  VARCHAR(100) NOT NULL,
                      cid   INTEGER        NOT NULL,
                      PRIMARY KEY (crid),
                      FOREIGN KEY (cid) REFERENCES public.clubs(club_id) ON DELETE CASCADE
                    )
                """.trimIndent())
                stmt.execute("ALTER SEQUENCE public.courts_crid_seq OWNED BY public.courts.crid;")

                // 5) rentals tablosu ve sequence
                stmt.execute("""
                    CREATE SEQUENCE IF NOT EXISTS public.rentals_rid_seq
                      START WITH 1 INCREMENT BY 1 CACHE 1
                """.trimIndent())
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS public.rentals (
                      rid       INTEGER   NOT NULL DEFAULT nextval('public.rentals_rid_seq'),
                      cid       INTEGER   NOT NULL REFERENCES public.clubs(club_id)   ON DELETE CASCADE,
                      crid      INTEGER   NOT NULL REFERENCES public.courts(crid)     ON DELETE CASCADE,
                      user_uid  INTEGER   NOT NULL REFERENCES public.users(user_id)   ON DELETE CASCADE,
                      date      TIMESTAMP NOT NULL,
                      duration  INTEGER   NOT NULL,
                      PRIMARY KEY (rid)
                    )
                """.trimIndent())
                stmt.execute("ALTER SEQUENCE public.rentals_rid_seq OWNED BY public.rentals.rid;")
            }
            conn.commit()
        }
    }

    fun getConnection(): Connection =
        DriverManager.getConnection(URL, USER, PASS)
}
