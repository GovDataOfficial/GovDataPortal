create table entities_MetadataComment (
	uuid_ VARCHAR(75) null,
	_id LONG not null primary key,
	userLiferayId LONG,
	metadataName VARCHAR(75) null,
	text_ VARCHAR(75) null,
	created DATE null
);