package info.nightscout.androidaps.database.daos

import androidx.room.Dao
import androidx.room.Query
import info.nightscout.androidaps.database.TABLE_PROFILE_SWITCHES
import info.nightscout.androidaps.database.daos.workaround.ProfileSwitchDaoWorkaround
import info.nightscout.androidaps.database.data.checkSanity
import info.nightscout.androidaps.database.entities.ProfileSwitch
import io.reactivex.Maybe
import io.reactivex.Single

@Suppress("FunctionName")
@Dao
internal interface ProfileSwitchDao : ProfileSwitchDaoWorkaround {

    @Query("SELECT * FROM $TABLE_PROFILE_SWITCHES WHERE id = :id")
    override fun findById(id: Long): ProfileSwitch?

    @Query("DELETE FROM $TABLE_PROFILE_SWITCHES")
    override fun deleteAllEntries()

    @Query("SELECT * FROM $TABLE_PROFILE_SWITCHES WHERE timestamp <= :timestamp AND (timestamp + duration) > :timestamp AND referenceId IS NULL AND isValid = 1 ORDER BY timestamp DESC LIMIT 1")
    fun getTemporaryProfileSwitchActiveAt(timestamp: Long): Maybe<ProfileSwitch>

    @Query("SELECT * FROM $TABLE_PROFILE_SWITCHES WHERE timestamp <= :timestamp AND  duration = 0 AND referenceId IS NULL AND isValid = 1 ORDER BY timestamp DESC LIMIT 1")
    fun getPermanentProfileSwitchActiveAt(timestamp: Long): Maybe<ProfileSwitch>

    @Query("SELECT * FROM $TABLE_PROFILE_SWITCHES WHERE referenceId IS NULL AND isValid = 1 ORDER BY timestamp DESC LIMIT 1")
    fun getAllProfileSwitches(): Single<List<ProfileSwitch>>

    @Query("SELECT * FROM $TABLE_PROFILE_SWITCHES WHERE timestamp >= :timestamp AND referenceId IS NULL ORDER BY timestamp ASC")
    fun getProfileSwitchDataIncludingInvalidFromTime(timestamp: Long): Single<List<ProfileSwitch>>

    @Query("SELECT * FROM $TABLE_PROFILE_SWITCHES WHERE timestamp >= :timestamp AND isValid = 1 AND referenceId IS NULL ORDER BY timestamp ASC")
    fun getProfileSwitchDataFromTime(timestamp: Long): Single<List<ProfileSwitch>>

}

internal fun ProfileSwitchDao.insertNewEntryImpl(entry: ProfileSwitch): Long {
    if (!entry.basalBlocks.checkSanity()) throw IllegalArgumentException("Sanity check failed for basal blocks.")
    if (!entry.icBlocks.checkSanity()) throw IllegalArgumentException("Sanity check failed for IC blocks.")
    if (!entry.isfBlocks.checkSanity()) throw IllegalArgumentException("Sanity check failed for ISF blocks.")
    if (!entry.targetBlocks.checkSanity()) throw IllegalArgumentException("Sanity check failed for target blocks.")
    return (this as TraceableDao<ProfileSwitch>).insertNewEntryImpl(entry)
}

internal fun ProfileSwitchDao.updateExistingEntryImpl(entry: ProfileSwitch): Long {
    if (!entry.basalBlocks.checkSanity()) throw IllegalArgumentException("Sanity check failed for basal blocks.")
    if (!entry.icBlocks.checkSanity()) throw IllegalArgumentException("Sanity check failed for IC blocks.")
    if (!entry.isfBlocks.checkSanity()) throw IllegalArgumentException("Sanity check failed for ISF blocks.")
    if (!entry.targetBlocks.checkSanity()) throw IllegalArgumentException("Sanity check failed for target blocks.")
    return (this as TraceableDao<ProfileSwitch>).updateExistingEntryImpl(entry)
}