package data

import data.fingerprints.FingerprintsTable
import data.songs.SongsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import constants.mysql_user
import constants.mysql_password

object DatabaseProvider {
    lateinit var fingerprintsTable: FingerprintsTable
        private set

    lateinit var songsTable: SongsTable
        private set

    fun initDatabaseTables(fingerprintsTableName: String = "fingerprints", songsTableName: String = "songs") {

        fingerprintsTable = FingerprintsTable(fingerprintsTableName)
        songsTable = SongsTable(songsTableName)

        Database.connect(
            url = "jdbc:mysql://localhost:3306/reca",
            driver = "com.mysql.cj.jdbc.Driver",
            user = mysql_user,
            password = mysql_password
        )

        transaction {
            SchemaUtils.create(fingerprintsTable)
            SchemaUtils.create(songsTable)
        }
    }

    fun recreateTables() {
        transaction {
            SchemaUtils.drop(songsTable)
            SchemaUtils.drop(fingerprintsTable)
            SchemaUtils.create(fingerprintsTable)
            SchemaUtils.create(songsTable)
        }
    }
}