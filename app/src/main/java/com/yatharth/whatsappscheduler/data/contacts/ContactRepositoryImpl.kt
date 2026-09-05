package com.yatharth.whatsappscheduler.data.contacts

import android.content.Context
import android.provider.ContactsContract
import com.yatharth.whatsappscheduler.domain.model.Contact
import com.yatharth.whatsappscheduler.domain.repository.ContactRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ContactRepository {

    override suspend fun getContacts(query: String): List<Contact> = withContext(Dispatchers.IO) {
        val contactsList = mutableListOf<Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val selection = if (query.isNotBlank()) {
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} LIKE ?"
        } else null

        val selectionArgs = if (query.isNotBlank()) {
            arrayOf("%$query%")
        } else null

        val sortOrder = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC"

        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            val seenNumbers = mutableSetOf<String>()

            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex) ?: ""
                val name = cursor.getString(nameIndex) ?: "Unknown"
                val rawNumber = cursor.getString(numberIndex) ?: ""
                val normalizedNumber = normalizePhoneNumber(rawNumber)

                if (normalizedNumber.isNotBlank() && !seenNumbers.contains(normalizedNumber)) {
                    seenNumbers.add(normalizedNumber)
                    contactsList.add(Contact(id = id, name = name, phoneNumber = normalizedNumber))
                }
            }
        }

        contactsList
    }

    private fun normalizePhoneNumber(raw: String): String {
        val cleaned = raw.replace(Regex("[^0-9+]"), "")
        return if (cleaned.startsWith("+")) {
            cleaned
        } else {
            // Retain raw digits if no country code prefix
            cleaned
        }
    }
}
