// docs http://www.liferay.com/de/documentation/liferay-portal/6.0/development/-/ai/javascript

AUI().ready('liferay-hudcrumbs', 'liferay-navigation-interaction', function(A) {
	var navigation = A.one('#navigation');

	if (navigation) {
		navigation.plug(Liferay.NavigationInteraction);
	}

	var siteBreadcrumbs = A.one('.site-breadcrumbs');

	if (siteBreadcrumbs) {
		siteBreadcrumbs.plug(A.Hudcrumbs);
	}
});

// ---------- Fraunhofer FOKUS ----------------------

AUI()
		.ready(
				function(B) {
					// ---External link icon 20.05.2014 BEGIN
					$("a[href*='http://']:not([href*='" + location.hostname.replace("www.", "")+ "']),[href*='https://']:not([href*='" + location.hostname.replace("www.", "")+ "']),[href*='ftp://']:not([href*='" + location.hostname.replace("www.", "")+ "']),[href*='ftps://']:not([href*='" + location.hostname.replace("www.", "")+ "'])").each(function() {
						$(this).click(function(event) {
							event.preventDefault();
							event.stopPropagation();
							window.open(this.href, '_blank');
						}).addClass('externalLink');
						
						// ---External link icon 30.10.2014 TITLE
						$(this).attr({					      
					        title: "Besuch " + this.href + " (klicken, um in einem neuen Fenster zu öffnen)"
					    });
																					
					});
					// ---External link icon 20.05.2014 END
					
					// ---Suche Content
					$('#layout-column_column-3 form input[type="text"]').val(
							'Suche in Neues, Bibliothek und Portal');
					// $('#layout-column_column-3 form
					// input[type="text"]').addClass('defaultGrey');
					$('#layout-column_column-3 form input[type="text"]')
							.on(
									'focus',
									function() {
										var $this = $(this);
										if ($this.val() == 'Suche in Neues, Bibliothek und Portal') {
											$this.val('');
											// $(this).removeClass('defaultGrey');
										}
									})
							.on(
									'blur',
									function() {
										var $this = $(this);
										if ($this.val() == '') {
											$this
													.val('Suche in Neues, Bibliothek und Portal');
											// $(this).addClass('defaultGrey');
										}
									});

					// ---------- Placeholder input fields ---------------------
					if (!Modernizr.input.placeholder) {

						$('[placeholder]').focus(function() {
							var input = $(this);
							if (input.val() == input.attr('placeholder')) {
								input.val('');
								input.removeClass('placeholder');
							}
						}).blur(
								function() {
									var input = $(this);
									if (input.val() == ''
											|| input.val() == input
													.attr('placeholder')) {
										input.addClass('placeholder');
										input.val(input.attr('placeholder'));
									}
								}).blur();
						$('[placeholder]').parents('form').submit(function() {
							$(this).find('[placeholder]').each(function() {
								var input = $(this);
								if (input.val() == input.attr('placeholder')) {
									input.val('');
								}
							})
						});
					}

					if ($('#column-1 #p_p_id_3_ input[type=text]').val() == "") {
						$('#column-1 #p_p_id_3_ input[type=text]').val(
								'Seite durchsuchen');
						$('#column-1 #p_p_id_3_ input[type=text]').on('focus',
								function() {
									var $this = $(this);
									if ($this.val() == 'Seite durchsuchen') {
										$this.val('');
									}
								}).on('blur', function() {
							var $this = $(this);
							if ($this.val() == '') {
								$this.val('Seite durchsuchen');
							}
						});

					}
					try {
						$("#accordion").accordion({
							heightStyle : "content",
							active : false,
							collapsible : true
						});
					} catch (err) {
					}

					if ($('#share').length > 0) {
						try {
							$(document)
									.ready(
											function() {

												$.fn.socialSharePrivacy.settings.order = [
														'facebook', 'gplus',
														'twitter' ];
												$.fn.socialSharePrivacy.settings.path_prefix = '';
												$('#share')
														.socialSharePrivacy(
																{
																	uri : 'http://www.govdata.de',
																	"layout" : "line",
																	"services" : {
																		"buffer" : {
																			"status" : false
																		},
																		"delicious" : {
																			"status" : false
																		},
																		"disqus" : {
																			"status" : false
																		},
																		"fbshare" : {
																			"status" : false
																		},
																		"flattr" : {
																			"status" : false
																		},
																		"hackernews" : {
																			"status" : false
																		},
																		"linkedin" : {
																			"status" : false
																		},
																		"mail" : {
																			"status" : false
																		},
																		"pinterest" : {
																			"status" : false
																		},
																		"reddit" : {
																			"status" : false
																		},
																		"stumbleupon" : {
																			"status" : false
																		},
																		"tumblr" : {
																			"status" : false
																		},
																		"xing" : {
																			"status" : false
																		}
																	}
																});// share
											});// $(document).ready
						} // try
						catch (err) {
							$
									.getScript(
											"/govdata-theme/js/socialshareprivacy.js",
											function() {
												$
														.getScript(
																"/govdata-theme/js/modules/twitter.js",
																function() {
																	$
																			.getScript(
																					"/govdata-theme/js/modules/facebook.js",
																					function() {
																						$
																								.getScript(
																										"/govdata-theme/js/modules/gplus.js",
																										function() {
																											$
																													.getScript(
																															"/govdata-theme/js/locale/de/socialshareprivacy.js",
																															function() {
																																$
																																		.getScript(
																																				"/govdata-theme/js/locale/de/modules/facebook.js",
																																				function() {
																																					$
																																							.getScript(
																																									"/govdata-theme/js/locale/de/modules/twitter.js",
																																									function() {
																																										$
																																												.getScript(
																																														"/govdata-theme/js/locale/de/modules/gplus.js",
																																														function() {

																																															$.fn.socialSharePrivacy.settings.order = [
																																																	'facebook',
																																																	'gplus',
																																																	'twitter' ];
																																															$.fn.socialSharePrivacy.settings.path_prefix = '';
																																															$(
																																																	'#share')
																																																	.socialSharePrivacy(
																																																			{
																																																				uri : 'http://www.govdata.de',
																																																				"layout" : "line",
																																																				"services" : {
																																																					"buffer" : {
																																																						"status" : false
																																																					},
																																																					"delicious" : {
																																																						"status" : false
																																																					},
																																																					"disqus" : {
																																																						"status" : false
																																																					},
																																																					"fbshare" : {
																																																						"status" : false
																																																					},
																																																					"flattr" : {
																																																						"status" : false
																																																					},
																																																					"hackernews" : {
																																																						"status" : false
																																																					},
																																																					"linkedin" : {
																																																						"status" : false
																																																					},
																																																					"mail" : {
																																																						"status" : false
																																																					},
																																																					"pinterest" : {
																																																						"status" : false
																																																					},
																																																					"reddit" : {
																																																						"status" : false
																																																					},
																																																					"stumbleupon" : {
																																																						"status" : false
																																																					},
																																																					"tumblr" : {
																																																						"status" : false
																																																					},
																																																					"xing" : {
																																																						"status" : false
																																																					}
																																																				}
																																																			});// share
																																															// });//$(document).ready
																																														});
																																									});
																																				});
																															});
																										});
																					});
																});
											});
						}
					} // catch(err)

					try {
						$('.carousel').carousel({
							interval : 8000
						})
					} catch (err) {

					}

					try {
						$("#tabs").tabs();
					} catch (err) {

					}

				});
