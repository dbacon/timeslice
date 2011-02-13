create table ts_version_6 ( nothing char(1));

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

create table ts_ordering
(
    name varchar(128),
    member varchar(255),
    index integer
);

create table ts_conf
(
    username varchar(32),
    name varchar(255),
    type varchar(255),
    value varchar(255)
);

create unique index ind_ts_conf_01 on ts_conf (username, name, type, value);

create table ts_user
(
    id identity not null,
    active int not null,
    username varchar(32) not null,
    hashscheme varchar(32) not null,
    hashsalt varchar(255) not null,
    hashvalue varchar(255) not null,
    lastchanged timestamp not null
);
create unique index ind_ts_user_01 on ts_user (username);


create table ts_version_6_done ( nothing char(1));
