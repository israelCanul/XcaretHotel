package com.xcaret.xcaret_hotel.domain

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.xcaret.xcaret_hotel.view.config.LogHX
import org.json.JSONObject

data class SalesForceContact(
    var Id: String = "",
    var Email: String = "",
    var FirstName: String = "",
    var LastName: String = "",
    var Name: String = "",
    var Phone: String = "",
    var MailingAddress: String = "",
    var MailingCountry: String = "",
    var MailingCountryCode: String = "",
    var MailingPostalCode: String = "",
    var MailingState: String = "",
    var MailingStateCode: String = "",
    var External_Id__c: String = "",
){

    fun getCreateRequest(): JsonObject {
        val contactObject = getContactRequest()
        val jsonArrayContacts = JsonArray()
        jsonArrayContacts.add(contactObject)

        val request = JsonObject()
        request.add("Contact", jsonArrayContacts)
        request.addProperty("ContactDeveloperName", "Ex_Visitante")
        LogHX.i("CreateRequest", request.toString())
        return request
    }

    fun getEditRequest(): JsonObject {
        val contactObject = getContactRequest()
        contactObject.addProperty("CONTACT_ID__c", Id)
        val jsonArrayContacts = JsonArray()
        jsonArrayContacts.add(contactObject)

        val request = JsonObject()
        request.add("Contact", jsonArrayContacts)
        return request
    }

    private fun getContactRequest(): JsonObject {
        val contact = JsonObject()
        contact.addProperty("FirstName", FirstName)
        contact.addProperty("LastName", LastName)
        contact.addProperty("Email", Email)
        contact.addProperty("Phone", Phone)
        contact.addProperty("mailingCountryCodeIso__c", MailingCountryCode)
        contact.addProperty("mailingStateCodeIso__c", MailingStateCode)
        contact.addProperty("External_Id__c", External_Id__c)
        contact.addProperty("MailingPostalCode", MailingPostalCode)
        contact.addProperty("MailingAddress", MailingAddress)


        return contact
    }
}