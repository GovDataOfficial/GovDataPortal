/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package de.fhg.fokus.odp.entities.service.persistence.impl.constants;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class entitiesPersistenceConstants {

	public static final String BUNDLE_SYMBOLIC_NAME =
		"de.fhg.fokus.odp.entities.service";

	public static final String ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER =
		"(origin.bundle.symbolic.name=" + BUNDLE_SYMBOLIC_NAME + ")";

	public static final String SERVICE_CONFIGURATION_FILTER =
		"(&" + ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER + "(name=service))";

}