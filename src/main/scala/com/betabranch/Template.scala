package com.betabranch

import unfiltered.request._
import unfiltered.response._

trait Template {

  case class Layout(docType: xml.dtd.DocType, nodes: scala.xml.Elem) extends ComposeResponse(
    HtmlContent ~> {
      val data = new java.io.StringWriter()
      xml.XML.write(data, nodes, "UTF-8", xmlDecl = false, doctype = docType)
      ResponseString(data.toString)
    })

  def layout(head: scala.xml.NodeSeq)(js: scala.xml.NodeSeq)(body: scala.xml.NodeSeq) = {
    Layout(xml.dtd.DocType("html", xml.dtd.SystemID("about:legacy-compat"), Nil),
      <html>
        <head>
          <meta charset="utf-8"/>
          <title>Bootstrap, from Twitter</title>
          <meta name="description" content=""/>
          <meta name="author" content=""/>
          <!--[if lt IE 9]>
         <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        <![endif]-->
          <link rel="stylesheet" href="http://twitter.github.com/bootstrap/1.3.0/bootstrap.css"/>
          <link rel="stylesheet" type="text/css" href="/css/app.css"/>
          { head }
        </head>
        <body>
          <div class="topbar">
            <div class="fill">
              <div class="container">
                <a class="brand" href="#">Project name</a>
                <ul class="nav">
                  <li class="active"><a href="#">Home</a></li>
                  <li><a href="#about">About</a></li>
                  <li><a href="#contact">Contact</a></li>
                </ul>
                <form action="" class="pull-right">
                  <input class="input-small" type="text" placeholder="Username"/>
                  <input class="input-small" type="password" placeholder="Password"/>
                  <button class="btn" type="submit">Sign in</button>
                </form>
              </div>
            </div>
          </div>
          <div class="container">
            <div class="content">
              <div class="page-header">
                <h1>Page name <small>Supporting text or tagline</small></h1>
              </div>
              <div class="row">
                <div class="span10">
                  <h2>Main content</h2>
                  { body }
                </div>
                <div class="span4">
                  <h3>Secondary content</h3>
                </div>
              </div>
            </div>
            <footer>
              <p>&copy; Company 2011</p>
            </footer>
          </div>
          <script type="text/javascript" src="/js/app.js"></script>
          { js }
        </body>
      </html>)
  }

}