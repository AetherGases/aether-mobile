import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

val dataStoreModule = module {
    single<DataStore<Preferences>> { androidContext().sessionDataStore }
}