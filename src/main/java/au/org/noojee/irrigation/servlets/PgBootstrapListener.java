package au.org.noojee.irrigation.servlets;

import org.jsoup.nodes.Element;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;

final class PgBootstrapListener implements BootstrapListener
{
	private static final long serialVersionUID = 1L;

	@Override
	public void modifyBootstrapFragment(
			BootstrapFragmentResponse response)
	{
		// NOOP

	}

	@Override
	public void modifyBootstrapPage(BootstrapPageResponse response)
	{
		Element head = response.getDocument()
				.head();

		/** Add tags to make this a PWA app **/

		/** Give our app a title */
		head.prependElement("title").appendText("Pi-Gation");

		/** Icon for the home screen */
		head.prependElement("link")
				.attr("src", "/irrigation/VAADIN/themes/mytheme/images/pi-gation-192x192.png");

		/** Set the theme colour **/
		head.prependElement("meta")
				.attr("name", "theme-color")
				.attr("content", "#00b4f0");

		/** link to the manifest for the pwa **/
		head.prependElement("link")
				.attr("rel", "manifest")
				.attr("href", "VAADIN/manifest.json");

		/** Add the ProgressiveApp.js to the bottom of the page */
		Element body = response.getDocument().body();
		body.appendElement("script")
				.attr("type", "text/javascript")
				.attr("src", "./VAADIN/js/ProgressiveApp.js");
	}
}