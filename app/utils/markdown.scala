package utils

import org.pegdown.PegDownProcessor

object Markdown {
  def markdownToHtml(markdown: String): String = new PegDownProcessor().markdownToHtml(markdown)
}