package au.org.noojee.irrigation;

public class IOSInstaller
{
	// https://dockyard.com/blog/2017/09/27/encouraging-pwa-installation-on-ios
	
	/*
	needsToSeePrompt() {
		  return ['iPhone', 'iPad', 'iPod'].includes(navigator.platform);
		}

	
	
	needsToSeePrompt(user) {
		  if (navigator.standalone) {
		    return false;
		  }
		  let today = moment();
		  let lastPrompt = Ember.get(user, 'lastSeenPrompt');
		  let days = today.diff(lastPrompt, 'days');
		  let isApple = ['iPhone', 'iPad', 'iPod'].includes(navigator.platform);
		  return (isNaN(days) || days > 14) && isApple;
		}

	activate() {
		  let currentUser = Ember.get(this, 'currentUser'); // a service we have to fetch user
		  if (currentUser) {
		    if (this.needsToSeePrompt(currentUser)) {
		      Ember.set(currentUser, 'lastSeenPrompt'. moment()); // set current time for prompt
		    // we had a specific route for showing the modal
		      // but this could be any action to prompt the user 
		      this.transitionTo('add-to-homescreen');
		    }
		  }
		  */
		}
