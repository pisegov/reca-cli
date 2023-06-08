package data

import data.fingerprints.FingerprintsTable
import data.songs.SongsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

object DatabaseProvider {
    fun provide() {
        val properties = Properties()

        properties.load(File("local.properties").inputStream())

        Database.connect(
            url = "jdbc:mysql://localhost:3306/reca",
            driver = "com.mysql.cj.jdbc.Driver",
            user = properties.getProperty("mysql_user"),
            password = properties.getProperty("mysql_password")
        )

        transaction {
            SchemaUtils.create(FingerprintsTable)
            SchemaUtils.create(SongsTable)
        }
    }

    fun recreateTables() {
        transaction {
            SchemaUtils.drop(SongsTable)
            SchemaUtils.drop(FingerprintsTable)
            SchemaUtils.create(FingerprintsTable)
            SchemaUtils.create(SongsTable)
        }
    }
}