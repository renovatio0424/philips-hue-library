package com.reno.philipshue.model

import com.stanfy.gsonxml.GsonXml
import com.stanfy.gsonxml.GsonXmlBuilder
import com.stanfy.gsonxml.XmlParserCreator
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

const val LOCATION_TEXT = "LOCATION: "
const val SERVER_TEXT = "SERVER: "
const val USN_TEXT = "USN: "
const val ST_TEXT = "ST: "
const val lineEnd = "\r\n"

data class UPnPDevice (// From SSDP Packet
    private val hostAddress: String, // SSDP Packet Header
    private val header: String
) {
    val location: String
    val server: String
    val uSN: String
    val sT: String

    init {
        location = parseHeader(header, LOCATION_TEXT)
        server = parseHeader(header, SERVER_TEXT)
        uSN = parseHeader(header, USN_TEXT)
        sT = parseHeader(header, ST_TEXT)
    }

    // XML content
    var descriptionXML: String? = null
        private set
    // From description XML
    private var deviceType: String? = null
    private var friendlyName: String? = null
    private var presentationURL: String? = null
    private var serialNumber: String? = null
    private var modelName: String? = null
    private var modelNumber: String? = null
    private var modelURL: String? = null
    private var manufacturer: String? = null
    private var manufacturerURL: String? = null
    private var uDN: String? = null
    private var uRLBase: String? = null

    fun update(xml: String?) {
        descriptionXML = xml
        xmlParse(xml)
    }

    override fun toString(): String {
        return "FriendlyName: " + friendlyName + lineEnd +
                "ModelName: " + modelName + lineEnd +
                "HostAddress: " + hostAddress + lineEnd +
                "Location: " + location + lineEnd +
                "Server: " + server + lineEnd +
                "USN: " + uSN + lineEnd +
                "ST: " + sT + lineEnd +
                "DeviceType: " + deviceType + lineEnd +
                "PresentationURL: " + presentationURL + lineEnd +
                "SerialNumber: " + serialNumber + lineEnd +
                "ModelURL: " + modelURL + lineEnd +
                "ModelNumber: " + modelNumber + lineEnd +
                "Manufacturer: " + manufacturer + lineEnd +
                "ManufacturerURL: " + manufacturerURL + lineEnd +
                "UDN: " + uDN + lineEnd +
                "URLBase: " + uRLBase
    }

    private fun parseHeader(
        mSearchAnswer: String,
        whatSearch: String
    ): String {
        var result = ""
        var searchLinePos = mSearchAnswer.indexOf(whatSearch)
        if (searchLinePos != -1) {
            searchLinePos += whatSearch.length
            val locColon = mSearchAnswer.indexOf(lineEnd, searchLinePos)
            result = mSearchAnswer.substring(searchLinePos, locColon)
        }
        return result
    }

    private fun xmlParse(xml: String?) {
        val parserCreator = XmlParserCreator {
            try {
                XmlPullParserFactory.newInstance().newPullParser()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
        val gsonXml: GsonXml = GsonXmlBuilder()
            .setXmlParserCreator(parserCreator)
            .create()
        val model: DescriptionModel = gsonXml.fromXml(xml, DescriptionModel::class.java)
        model.device?.let {
            friendlyName = it.friendlyName
            deviceType = it.deviceType
            presentationURL = it.presentationURL
            serialNumber = it.serialNumber
            modelName = it.modelName
            modelNumber = it.modelNumber
            modelURL = it.modelURL
            manufacturer = it.manufacturer
            manufacturerURL = it.manufacturerURL
            uDN = it.UDN
        }
        uRLBase = model.URLBase
    }

    data class Device(
        internal val deviceType: String? = null,
        internal val friendlyName: String? = null,
        internal val presentationURL: String? = null,
        internal val serialNumber: String? = null,
        internal val modelName: String? = null,
        internal val modelNumber: String? = null,
        internal val modelURL: String? = null,
        internal val manufacturer: String? = null,
        internal val manufacturerURL: String? = null,
        internal val UDN: String? = null
    )

    data class DescriptionModel(
        internal val device: Device? = null,
        internal val URLBase: String? = null
    )

}
