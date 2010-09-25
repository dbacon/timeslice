create table ts_version_2 ( nothing char(1));

create table ts_tag
(
	whenstamp varchar(64),
	who varchar(32),
	what varchar(128)
);

create table ts_assign
(
    id integer,
    eff_from datetime,
    eff_until datetime,
    what varchar(128),
    billee varchar(128)
);

create table ts_prorata
(
    name varchar(128),
    component_name varchar(128),
    weight double
);

create table ts_version_2_done ( nothing char(1));