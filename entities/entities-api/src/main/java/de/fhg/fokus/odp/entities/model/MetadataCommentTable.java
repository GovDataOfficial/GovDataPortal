/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package de.fhg.fokus.odp.entities.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;entities_MetadataComment&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see MetadataComment
 * @generated
 */
public class MetadataCommentTable extends BaseTable<MetadataCommentTable> {

	public static final MetadataCommentTable INSTANCE =
		new MetadataCommentTable();

	public final Column<MetadataCommentTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<MetadataCommentTable, Long> _id = createColumn(
		"_id", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<MetadataCommentTable, Long> userLiferayId =
		createColumn(
			"userLiferayId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<MetadataCommentTable, String> metadataName =
		createColumn(
			"metadataName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<MetadataCommentTable, String> text = createColumn(
		"text_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<MetadataCommentTable, Date> created = createColumn(
		"created", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private MetadataCommentTable() {
		super("entities_MetadataComment", MetadataCommentTable::new);
	}

}