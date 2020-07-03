package picta

import java.io.{BufferedWriter, File, FileWriter}
import java.net.{HttpURLConnection, URL}

import almond.api.JupyterApi
import ujson.Value
import almond.interpreter.api.OutputHandler
import os.Path

object Html {
  /** this is the plotly.min.js script that is used to render the plots */
  val plotlyJs : String = {
    val is = getClass.getClassLoader.getResourceAsStream("plotly.min.js")
    scala.io.Source.fromInputStream(is).mkString
  }

  /** This function checks if an active network connection is available. It returns true if this is the case, false
    * otherwise.
    */
  def testNetworkConnection(): Boolean = {
    var activeConnection: Boolean = true
    val url: URL = new URL("https://www.google.com")
    val urlConn: HttpURLConnection = url.openConnection().asInstanceOf[HttpURLConnection]

    try {
      urlConn.connect()
      urlConn.setConnectTimeout(1)
      urlConn.setReadTimeout(1)
    } catch {
      case e: Throwable => activeConnection = false
    } finally urlConn.disconnect()

    activeConnection
  }

  val useCDN: Boolean = testNetworkConnection()

  /**
    * A function to generate the HTML corresponding to the Plotly plotting function.
    * @param traces: This is the trace data. It should be serialized as a json list.
    * @param layout: This should be the layout case class instance.
    * @param config: This should be the config case class instance.
    * @param graph_id: This is an internal id that allows the Plotly functions to find the chart element in the HTML.
    */
  def generateHTML(traces: Value, layout: Value, config: Value, scriptFlag: Boolean, graph_id: String): String = {
    var script = new StringBuilder()

    if (useCDN)
      script ++= """<script src="https://cdn.plot.ly/plotly-latest.min.js"></script>"""
    else
      script ++= s"""<script> ${plotlyJs} </script>"""

    val base_html = s"""
                       |<div align="center">
                       |<div id='graph_${graph_id}' style="width:100%; margin:0 auto;"></div>
                       |</div>
                       |<script>
                       | var traces = ${traces};
                       | var layout = ${layout};
                       | var config = ${config};
                       | Plotly.newPlot("graph_${graph_id}", traces, layout, config);
                       |</script>
                       |""".stripMargin

    if (scriptFlag) {
      script ++= base_html
      script.mkString
    }
    else {
      base_html
    }
  }

  /**
    * A function to inject the HTML into the Jupyter notebook.
    * @param html: This is the html represented as a string.
    * @param graph_id: This is required in order to generate a unique div id for the plotly chart to render in the right
    *                  place.
    */
  def writeHTMLToJupyter(html: String, graph_id: String)(implicit publish: OutputHandler): Unit = {
    publish.html(html)
  }

  /**
    * A function to write the chart to a .html and open a browser displaying the chart.
    * @param html: This is the html represented as a string.
    * @param graph_id: This is required in order to generate a unique file name for the chart.
    */
  def writeHTMLToFile(html: String, graph_id: String): Unit = {
    val osName = System.getProperty("os.name") match {
      case name if name.startsWith("Linux") => "linux"
      case name if name.startsWith("Mac") => "mac"
      case name if name.startsWith("Windows") => "win"
      case _ => throw new Exception("Unknown platform!")
    }

    /** Folders where images are saved is relative to the home folder */
    val save_directory = System.getProperty("user.home")+"/Conus/"
    if (!os.exists(Path(save_directory))) os.makeDir(Path(save_directory))
    val file_directory = save_directory+graph_id+".html"
    val fout = new File(file_directory)
    val bufferWriter = new BufferedWriter(new FileWriter(fout))
    bufferWriter.write(html)
    bufferWriter.close()

    val command = osName match {
      case "linux" => Some(Seq("xdg-open", fout.getAbsolutePath))
      case "mac" => Some(Seq("open", fout.getAbsolutePath))
      case "win" => Some(Seq("cmd", s"start ${fout.getAbsolutePath}"))
      case _ => None
    }

    command match {
      case Some(c) => sys.process.Process(c).run
      case None => Console.err.println(s"Chart could not be opened")
    }
  }

  /**
    * This sets the charts to be inline inside a Jupyter notebook.
    * @param publish (implicit): required to render the HTML in the almond notebook.
    * @param kernel (implicit): required to interact with the underlying Jupyter kernel instance.
    */
  def init_notebook_mode()(implicit publish: OutputHandler, kernel: JupyterApi): Unit = {
    kernel.silent(true)

    /** if internet connection; grab from cdn otherwise just inject the raw javascript */
    val html = if (useCDN) {
      val requirejs = {
        val is = getClass.getClassLoader.getResourceAsStream("require.min.js")
        scala.io.Source.fromInputStream(is).mkString
      }

      s"""
          |<script type='text/javascript'>${requirejs}</script>
          |<script type='text/javascript'>
          |require.config({
          |paths: {
          |        'plotly': "https://cdn.plot.ly/plotly-latest.min"
          |    },
          |})
          |require( ['plotly'], function(Plotly) {
          |window.Plotly = Plotly;
          |})
          |</script>
          |""".stripMargin
    }
    else {
      s"""
        |<script type='text/javascript'>
        |define( 'plotly', function(require, exports, module) {
        |${plotlyJs}
        |})
        |require( ['plotly'], function(Plotly) {
        |window.Plotly = Plotly;
        |})
        |</script>
        |""".stripMargin
    }

    publish.html(html)
  }

  /**
    * This plots the chart in the browser
    * @param traces: a list of trace data we wish to plot
    * @param layout: the layout case class specifying layout options
    * @param config: the config case class specificying the chart config options
    */
  def plotChart(traces: List[Value], layout: Value, config: Value): Unit = {
    val graph_id = System.currentTimeMillis().toString
    val html: String = generateHTML(traces, layout, config, true, graph_id)
    writeHTMLToFile(html, graph_id)
  }

  /**
    * This plots the chart inside a Jupyter notebook
    * @param traces: a list of trace data we wish to plot
    * @param layout: the layout case class specifying layout options
    * @param config: the config case class specifying the chart config options
    * @param publish (implicit): required to render the HTML in the almond notebook
    */
  def plotChart_inline(traces: List[Value], layout: Value, config: Value)(implicit publish: OutputHandler): Unit = {
    val graph_id = System.currentTimeMillis().toString
    val html: String = generateHTML(traces, layout, config, false, graph_id)
    writeHTMLToJupyter(html, graph_id)
  }
}