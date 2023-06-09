package data

import data.fingerprints.FingerprintsTable
import data.songs.SongsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import constants.mysql_user
import constants.mysql_password

object DatabaseProvider {
    fun provide() {
        Database.connect(
            url = "jdbc:mysql://localhost:3306/reca",
            driver = "com.mysql.cj.jdbc.Driver",
            user = mysql_user,
            password = mysql_password
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