package com.betabranch

import scala.io.Source._
import java.net.HttpURLConnection
import unfiltered.request._
import unfiltered.response._
import com.google.appengine.api.blobstore._
import com.google.appengine.api.files._
import com.google.appengine.api.images._
import com.google.appengine.api.urlfetch._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonParser._

/** unfiltered plan */
class App extends unfiltered.filter.Plan with Template {
  import QParams._
  
  lazy val blobstoreService = BlobstoreServiceFactory.getBlobstoreService
  lazy val imageService = ImagesServiceFactory.getImagesService
  
  def intent = {
    case req @ GET(Path("/")) =>
      Ok 
    case req @ GET(Path("/image")) =>
      Ok ~> Json(('url.name -> blobstoreService.createUploadUrl("/upload")))
    case req @ GET(Path(Seg("image" :: id :: Nil))) =>
      Ok ~> Json(('url.name -> imageService.getServingUrl(new BlobKey(id))))
    case req @ POST(Path("/upload")) =>
      val blobs = blobstoreService.getUploadedBlobs(req.underlying)
      val blobKey = Option(blobs.get("image"))
      blobKey match {
        case Some(key) => Ok ~> Json(('id.name -> key.getKeyString))
        case _ => Ok ~> Json('error.name -> "image could not be loaded")
      }
  }
}
