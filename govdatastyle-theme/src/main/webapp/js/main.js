// Modify this...

AUI.add('lang/autocomplete-list', function (e) {
  e.Intl.add('autocomplete-list', 'de', {
    item_selected: '{item} ausgewählt.',
    items_available: 'Vorschläge sind verfügbar. Benutze die auf- und abwärtstasten um durch die Vorschläge zu navigieren.'
  });
}, 'patched-v3.11.1');


AUI().ready(
	'liferay-navigation-interaction', 'liferay-sign-in-modal',
	function(A) {
		var signIn = A.one('li.sign-in a');

		if (signIn && signIn.getData('redirect') !== 'true') {
			signIn.plug(Liferay.SignInModal);
		}
	}
);
