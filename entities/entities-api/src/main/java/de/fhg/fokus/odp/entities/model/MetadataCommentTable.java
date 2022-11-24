/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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