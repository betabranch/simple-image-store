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
import scala.collection.JavaConversions._

class App extends unfiltered.filter.Plan with Template {
  import QParams._
  
  lazy val blobstoreService = BlobstoreServiceFactory.getBlobstoreService
  lazy val imageService = ImagesServiceFactory.getImagesService
  lazy val urlFetchService = URLFetchServiceFactory.getURLFetchService
  lazy val fileService = FileServiceFactory.getFileService
  
  def fetchImage(href: String) =
    ImagesServiceFactory.makeImage(urlFetchService.fetchAsync(new java.net.URL(href)).get.getContent)
  
  def storeImage(image: Image) = {
    var file = fileService.createNewBlobFile("image/%s" format image.getFormat)
    var writeChannel = fileService.openWriteChannel(file, true)
    writeChannel.write(java.nio.ByteBuffer.wrap(image.getImageData))
    writeChannel.closeFinally
    file = new AppEngineFile(file.getFullPath)
    fileService.getBlobKey(file)
  }
  
  def deleteBlobs = new BlobInfoFactory().queryBlobInfos foreach { bi => blobstoreService.delete(bi.getBlobKey) }
  
  def intent = {
    case req @ GET(Path("/")) =>
      Ok 
    case req @ GET(Path("/image")) =>
      Ok ~> Json(('url.name -> blobstoreService.createUploadUrl("/upload")))
    case req @ GET(Path("/image/delete")) =>
      deleteBlobs
      Ok 
    case req @ GET(Path(Seg("image" :: id :: Nil))) =>
      Ok ~> Json(('url.name -> imageService.getServingUrl(new BlobKey(id))))
    case req @ POST(Path("/fetch") & Params(params)) =>
      val expected = for {
        href <- lookup("href") is
          required("missing href")
        height <- lookup("h") is 
            int { s => "'%s' is not an integer".format(s) }
        width <- lookup("w") is
            int { s => "'%s' is not an integer".format(s) }
      } yield {
        var image = fetchImage(href.get)
        (!height.isEmpty && !width.isEmpty) match {
          case true =>
            //do resize
            val resize = ImagesServiceFactory.makeResize(height.get.toInt, width.get.toInt)
            image = imageService.applyTransform(resize, image) 
          case _ =>
        }
        Ok ~> Json(('id.name -> storeImage(image).getKeyString))
      }
      expected(params) orFail { fails =>
        Ok ~> Json('error.name -> fails.head.toString)
      }
    case req @ POST(Path("/upload")) =>
      val blobs = blobstoreService.getUploadedBlobs(req.underlying)
      val blobKey = Option(blobs.get("image"))
      blobKey match {
        case Some(key) => Ok ~> Json(('id.name -> key.getKeyString))
        case _ => Ok ~> Json('error.name -> "image could not be loaded")
      }
  }
}
