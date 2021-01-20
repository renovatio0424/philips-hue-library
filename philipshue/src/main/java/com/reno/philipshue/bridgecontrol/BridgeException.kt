package com.reno.philipshue.bridgecontrol

class UnClickBridgeLinkButton :
    Exception("If you want to get the token, You should click bridge link button")

class UnknownBridgeException(val type: Int, description: String): Exception(description)