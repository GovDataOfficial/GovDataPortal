/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package de.fhg.fokus.odp.entities.model;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.util.Accessor;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The extended model interface for the MetadataComment service. Represents a row in the &quot;entities_MetadataComment&quot; database table, with each column mapped to a property of this class.
 *
 * @author Brian Wing Shun Chan
 * @see MetadataCommentModel
 * @generated
 */
@ImplementationClassName(
	"de.fhg.fokus.odp.entities.model.impl.MetadataCommentImpl"
)
@ProviderType
public interface MetadataComment extends MetadataCommentModel, PersistedModel {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add methods to <code>de.fhg.fokus.odp.entities.model.impl.MetadataCommentImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	public static final Accessor<MetadataComment, Long> _ID_ACCESSOR =
		new Accessor<MetadataComment, Long>() {

			@Override
			public Long get(MetadataComment metadataComment) {
				return metadataComment.get_id();
			}

			@Override
			public Class<Long> getAttributeClass() {
				return Long.class;
			}

			@Override
			public Class<MetadataComment> getTypeClass() {
				return MetadataComment.class;
			}

		};

}