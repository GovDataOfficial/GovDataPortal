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
    var signInLink = document.querySelector('li.sign-in > a');

    if (signInLink && signInLink.dataset.redirect === 'false') {
        signInLink.addEventListener('click', function(event) {
            event.preventDefault();

            var modalSignInURL = Liferay.Util.addParams(
                'windowState=exclusive',
                signInLink.href
            );

            Liferay.Util.fetch(modalSignInURL)
                .then(response => response.text())
                .then(response => {
                    if (response) {
                        Liferay.Util.openModal({
                            bodyHTML: response,
                            title: Liferay.Language.get('sign-in'),
                        });
                    }
                    else {
                        redirectPage();
                    }
                })
                .catch(() => redirectPage());
        });
    }

    function redirectPage() {
        window.location.href = signInLink.href;
    }
    }
);
