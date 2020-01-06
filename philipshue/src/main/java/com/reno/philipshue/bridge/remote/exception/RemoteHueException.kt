package com.reno.philipshue.bridge.remote.exception

import java.lang.RuntimeException

/**
 * reference
 *
 * https://developers.meethue.com/develop/hue-api/remote-hue-api-error-messages/
 *
 * sample
 * https://medium.com/@janczar/http-errors-with-kotlin-rx-and-retrofit-34e905aa91dd
 *
 * */
open class RemoteHueException(error: Throwable) : RuntimeException(error)

//400
class BadRequestException(error: Throwable) : RemoteHueException(error)

//401
class UnAuthorizedException(error: Throwable) : RemoteHueException(error)

//403
class ForbiddenException(error: Throwable) : RemoteHueException(error)

//404
class NotFoundException(error: Throwable) : RemoteHueException(error)

//405
class MethodNotAllowedException(error: Throwable) : RemoteHueException(error)

//415
class UnsupportedMediaTypeException(error: Throwable) : RemoteHueException(error)

//500
class InternalServerException(error: Throwable) : RemoteHueException(error)

//504
/**
 * {
 *  "fault":{
 *      "faultstring":"Invalid client identifier
 *      {0}",
 *      "detail":{
 *          "errorcode":"oauth.v2.InvalidClientIdentifier"
 *      }
 *  }
 * }
 * */
class GatewayTimeoutException(error: Throwable) : RemoteHueException(error)

// Incorrect method
/**
 * [
 *  {
 *      "error":{
 *          "type":4,
 *          "address":"/",
 *          "description":"method, PUT, not available for resource, /"
 *      }
 *  }
 * ]
 */