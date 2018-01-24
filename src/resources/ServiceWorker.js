var CACHE_NAME = 'serviceworker-ex';
var CACHE_VERSION = 8;

var filesToCache = [
// '/',
// '/index.html',
  // '/css/styles.css',
  '/irrigation/js/app.js',
  '/irrigation/VAADIN/themes/mytheme/styles.css?v=8.1.5',
  '/irrigation/VAADIN/themes/mytheme/icons/pi-gation.png',
  '/irrigation/images/touch/chrome-touch-icon-192x192.png',
  '/irrigation/images/touch/chrome-touch-icon-512x512.png',
  '/irrigation/VAADIN/widgetsets/AppWidgetset/switch/styles.css',
  '/irrigation/VAADIN/themes/valo/shared/img/spinner.gif',
  '/irrigation/VAADIN/themes/valo/fonts/open-sans/OpenSans-Light-webfont.woff',
  '/irrigation/VAADIN/addons/app-layout/app-layout-resize-listener.js',
  '/irrigation/VAADIN/addons/app-layout/babel-helpers.js',
  '/irrigation/VAADIN/bower_components/webcomponentsjs/custom-elements-es5-adapter.js',
  '/irrigation/VAADIN/bower_components/webcomponentsjs/webcomponents-lite.js',
  '/irrigation/VAADIN/bower_components/polymer/polymer.html',
  '/irrigation/VAADIN/bower_components/iron-icons/iron-icons.html',
  '/irrigation/VAADIN/bower_components/paper-icon-button/paper-icon-button.html',
  '/irrigation/VAADIN/bower_components/app-layout/app-toolbar/app-toolbar.html',
  '/irrigation/VAADIN/bower_components/app-layout/app-drawer/app-drawer.html',
  '/irrigation/VAADIN/bower_components/app-layout/app-drawer-layout/app-drawer-layout.html',
  '/irrigation/VAADIN/bower_components/iron-icon/iron-icon.html',
  '/irrigation/VAADIN/bower_components/paper-behaviors/paper-inky-focus-behavior.html',
  '/irrigation/VAADIN/bower_components/paper-styles/default-theme.html',
  '/irrigation/VAADIN/bower_components/iron-flex-layout/iron-flex-layout.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/legacy/legacy-element-mixin.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/legacy/polymer-fn.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/legacy/templatizer-behavior.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/elements/dom-bind.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/elements/dom-repeat.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/elements/dom-if.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/elements/array-selector.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/elements/custom-style.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/legacy/mutable-data-behavior.html',
  '/irrigation/VAADIN/bower_components/iron-iconset-svg/iron-iconset-svg.html',
  '/irrigation/VAADIN/bower_components/iron-media-query/iron-media-query.html',
  '/irrigation/VAADIN/bower_components/app-layout/app-layout-behavior/app-layout-behavior.html',
  '/irrigation/VAADIN/bower_components/iron-meta/iron-meta.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/legacy/class.html',
  '/irrigation/VAADIN/bower_components/shadycss/apply-shim.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/mixins/element-mixin.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/mixins/gesture-event-listeners.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/mixins/dir-mixin.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/mixin.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/import-href.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/render-status.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/unresolved.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/legacy/polymer.dom.html',
  '/irrigation/VAADIN/bower_components/iron-behaviors/iron-button-state.html',
  'irrigation/VAADIN/bower_components/paper-behaviors/paper-ripple-behavior.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/templatize.html',
  '/irrigation/VAADIN/bower_components/paper-styles/color.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/boot.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/mixins/property-effects.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/mixins/mutable-data.html',
  '/irrigation/VAADIN/bower_components/polymer/polymer-element.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/debounce.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/flush.html',
  '/irrigation/VAADIN/bower_components/shadycss/custom-style-interface.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/style-gather.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/array-splice.html',
  '/irrigation/VAADIN/bower_components/iron-resizable-behavior/iron-resizable-behavior.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/mixins/property-accessors.html',
  '/irrigation/VAADIN/bower_components/shadycss/apply-shim.min.js',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/gestures.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/settings.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/case-map.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/resolve-url.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/elements/dom-module.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/flattened-nodes-observer.html',
  '/irrigation/VAADIN/bower_components/paper-ripple/paper-ripple.html',
  '/irrigation/VAADIN/bower_components/iron-a11y-keys-behavior/iron-a11y-keys-behavior.html',
  '/irrigation/VAADIN/bower_components/iron-behaviors/iron-control-state.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/path.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/mixins/template-stamp.html',
  '/irrigation/VAADIN/bower_components/polymer/lib/utils/async.html',
  '/irrigation/VAADIN/bower_components/shadycss/custom-style-interface.min.js',
  '/irrigation/VAADIN/themes/valo/fonts/vaadin-icons/Vaadin-Icons.woff',
  '/irrigation/VAADIN/themes/valo/fonts/open-sans/OpenSans-Regular-webfont.woff'
];

self.oninstall = function(event) 
{
	console.info("Service worker: oninstall called");
	
	event.waitUntil(
			caches.open(CACHE_NAME + '-v' + CACHE_VERSION).then(function(cache) 
					{
						return cache.addAll(filesToCache);
					})
	);
};

self.onactivate = function(event) 
{
	console.info("Service worker: onactive called");
  
	var currentCacheName = CACHE_NAME + '-v' + CACHE_VERSION;
  
	caches.keys().then(function(cacheNames) 
			{
				return Promise.all(
						cacheNames.map(function(cacheName) 
						{
							if (cacheName.indexOf(CACHE_NAME) == -1) 
							{
								return;
							}

					        if (cacheName != currentCacheName) 
					        {
					        	return caches.delete(cacheName);
					        }
						})
				);
			});
};

self.onfetch = function(event) 
{
	var request = event.request;
	console.debug("Service worker: onfetch called" + request.url);
  
	event.respondWith(
			caches.match(request).then(function(response) 
					{
						if (response) 
						{
							return response;
						}
						return fetch(request).then(function(response) 
								{
									var responseToCache = response.clone();
									caches.open(CACHE_NAME + '-v' + CACHE_VERSION).then(
											function(cache) 
											{
												cache.put(request, responseToCache)
												.catch(function(err) 
												{
													console.info("Unable to cache:" + request.url + ': ' + err.message);
												});
											});
									return response;
								});
					})
	);
};


// Communicate via MessageChannel.
self.addEventListener('message', function(event) 
		{
			console.info(`Received message from main thread: ${event.data}`);
			event.ports[0].postMessage(`Got message! Sending direct message back - "${event.data}"`);
		});

// Broadcast via postMessage.
function sendMessage(message) 
{
	self.clients.matchAll().then(function(clients) 
			{
				clients.map(function(client) 
						{
							return client.postMessage(message);
						})
			});
}

